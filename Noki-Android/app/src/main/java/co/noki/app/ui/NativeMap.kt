@file:Suppress("DEPRECATION")
package co.noki.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddLocationAlt
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.noki.app.data.CollectionPoint
import co.noki.app.data.RoutePath
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.Style
import org.maplibre.android.module.http.HttpRequestUtil
import okhttp3.OkHttpClient
import okhttp3.Cache
import java.io.File
import java.util.concurrent.TimeUnit

private object MapNetwork {
    var initialized = false
    fun configure(context: android.content.Context) {
        if (initialized) return
        MapLibre.getInstance(context.applicationContext)
        HttpRequestUtil.setLogEnabled(false)
        HttpRequestUtil.setOkHttpClient(OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS)
            .cache(Cache(File(context.cacheDir,"map-http"), 40L * 1024 * 1024))
            .addInterceptor { chain -> chain.proceed(chain.request().newBuilder().header("User-Agent", "Noki/0.4 (nokiappcorporation@gmail.com)").build()) }.build())
        initialized = true
    }
}
private val mapStyle = """{"version":8,"sources":{"osm":{"type":"raster","tiles":["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],"tileSize":256,"maxzoom":19,"attribution":"© OpenStreetMap contributors"}},"layers":[{"id":"osm","type":"raster","source":"osm"}]}"""

@Composable
fun NativeMap(points: List<CollectionPoint>, selectedId: String?, origin: LatLng?, route: RoutePath?,
              selected: (String) -> Unit, manualOrigin: (LatLng) -> Unit, choosingOrigin: Boolean = false, centerChanged: (LatLng) -> Unit = {},
              modifier: Modifier = Modifier.fillMaxWidth().height(330.dp), interactive: Boolean = true, initialCenter: LatLng? = null) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentSelect by rememberUpdatedState(selected)
    val currentOrigin by rememberUpdatedState(manualOrigin)
    val currentCenter by rememberUpdatedState(centerChanged)
    val reduceMotion=LocalAccessibility.current.reduceMotion
    var map by remember { mutableStateOf<MapLibreMap?>(null) }
    var loaded by remember { mutableStateOf(false) }
    var rendered by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var retry by remember { mutableIntStateOf(0) }
    val view = remember(retry) {
        MapNetwork.configure(context)
        MapView(context, MapLibreMapOptions.createFromAttributes(context).textureMode(true).foregroundLoadColor(android.graphics.Color.rgb(245,244,240))).apply {
            onCreate(null)
            addOnDidFinishRenderingMapListener { fully -> if (fully) { rendered = true; error=null } }
            addOnDidFailLoadingMapListener { error = "No se pudo cargar el mapa. Las direcciones siguen disponibles en la lista." }
            getMapAsync { ready ->
                ready.uiSettings.isLogoEnabled = false
                ready.uiSettings.isAttributionEnabled = true
                ready.uiSettings.isCompassEnabled = true
                ready.uiSettings.setAllGesturesEnabled(interactive)
                ready.cameraPosition = org.maplibre.android.camera.CameraPosition.Builder().target(initialCenter ?: LatLng(6.222, -75.574)).zoom(12.0).build()
                ready.setStyle(Style.Builder().fromJson(mapStyle)) { map = ready; loaded = true; ready.cameraPosition.target?.let(currentCenter) }
                ready.addOnCameraIdleListener { ready.cameraPosition.target?.let(currentCenter) }
                ready.setOnMarkerClickListener { marker -> marker.snippet?.takeIf { it.startsWith("point:") }?.removePrefix("point:")?.let(currentSelect); true }
                ready.addOnMapLongClickListener { currentOrigin(it); true }
            }
        }
    }
    LaunchedEffect(view) { kotlinx.coroutines.delay(20000); if(!rendered) error="El mapa tarda en cargar. Puedes consultar las direcciones en Lista o reintentar." }
    DisposableEffect(view, lifecycle) {
        var started = false
        var resumed = false
        fun sync() {
            val wantStart = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
            val wantResume = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            if (resumed && !wantResume) { view.onPause(); resumed = false }
            if (started && !wantStart) { view.onStop(); started = false }
            if (!started && wantStart) { view.onStart(); started = true }
            if (!resumed && wantResume) { view.onResume(); resumed = true }
        }
        val observer = LifecycleEventObserver { _, _ -> sync() }
        lifecycle.addObserver(observer); sync()
        onDispose { lifecycle.removeObserver(observer); if(resumed) view.onPause(); if(started) view.onStop(); view.onDestroy() }
    }
    LaunchedEffect(map, points, origin, route) {
        map?.let { ready ->
            ready.clear()
            points.forEach { p -> ready.addMarker(MarkerOptions().position(LatLng(p.latitude,p.longitude)).title(p.name).snippet("point:${p.id}")) }
            origin?.let { ready.addMarker(MarkerOptions().position(it).title("Punto de partida")) }
            route?.let { path -> ready.addPolyline(PolylineOptions().addAll(path.coordinates).color(android.graphics.Color.rgb(16,102,30)).width(5f)) }
        }
    }
    // New points update markers without moving a camera the user is exploring.
    LaunchedEffect(map, origin, route) {
        map?.let { ready ->
            if(route==null && origin!=null) {
                val update=CameraUpdateFactory.newLatLngZoom(origin,15.0)
                if(reduceMotion) ready.moveCamera(update) else ready.animateCamera(update,650)
            }
            route?.let { path ->
                if(path.coordinates.size >= 2) {
                    val update=CameraUpdateFactory.newLatLngBounds(LatLngBounds.Builder().includes(path.coordinates).build(),48)
                    if(reduceMotion) ready.moveCamera(update) else ready.animateCamera(update)
                }
            }
        }
    }
    LaunchedEffect(map, selectedId) {
        if (route == null && origin == null) points.firstOrNull { it.id == selectedId }?.let {
            val update=CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude,it.longitude),14.0)
            if(reduceMotion) map?.moveCamera(update) else map?.animateCamera(update)
        }
    }
    Box(modifier) {
            key(retry) { AndroidView(factory = { view }, modifier = Modifier.fillMaxSize().testTag("NokiNativeMap")) }
            if (rendered && loaded) Spacer(Modifier.size(1.dp).testTag("NokiMapRendered"))
            if (choosingOrigin) Surface(modifier = Modifier.align(Alignment.Center), color = androidx.compose.ui.graphics.Color.White,
                shape = androidx.compose.foundation.shape.CircleShape) { Icon(Icons.Outlined.AddLocationAlt,"Centro del mapa",Modifier.padding(8.dp).size(32.dp), tint=NokiBlue) }
        if (!rendered && error == null) Surface(Modifier.align(Alignment.TopCenter).padding(12.dp)) { Text("Cargando mapa…", Modifier.padding(12.dp),style = MaterialTheme.typography.bodyMedium) }
        error?.let { Surface(Modifier.align(Alignment.BottomCenter).padding(12.dp)) { Column(Modifier.padding(12.dp)) {
            Text(it, color = MaterialTheme.colorScheme.error); TextButton(onClick = { error=null; loaded=false; rendered=false; map=null; retry++ }) { Text("Reintentar mapa") }
        } } }
    }
}
