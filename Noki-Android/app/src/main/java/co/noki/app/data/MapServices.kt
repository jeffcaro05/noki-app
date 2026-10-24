package co.noki.app.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.maplibre.android.geometry.LatLng
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class RoutePath(val coordinates: List<LatLng>, val meters: Double, val seconds: Double)
data class AddressMatch(val label:String, val coordinate:LatLng)
object MapServices {
    private val client = OkHttpClient.Builder().callTimeout(18, TimeUnit.SECONDS).build()
    suspend fun findAddress(context:Context, query:String):List<AddressMatch> {
        if(query.trim().length !in 6..180) throw NokiApiException("Escribe la dirección y la ciudad, entre 6 y 180 caracteres.")
        if(!android.location.Geocoder.isPresent()) throw NokiApiException("Este dispositivo no tiene un buscador de direcciones. Puedes elegir el punto en el mapa o usar tu ubicación.")
        val geocoder=android.location.Geocoder(context,java.util.Locale.forLanguageTag("es-CO"))
        try {
            val addresses:List<android.location.Address> = withTimeout(20000) {
                if(android.os.Build.VERSION.SDK_INT >= 33) suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocationName(query.trim(),5,object:android.location.Geocoder.GeocodeListener {
                        override fun onGeocode(addresses:MutableList<android.location.Address>) { if(continuation.isActive) continuation.resume(addresses) }
                        override fun onError(errorMessage:String?) { if(continuation.isActive) continuation.resumeWithException(NokiApiException("No pudimos buscar esa dirección. Revisa la conexión o elige el punto en el mapa.")) }
                    })
                } else withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(query.trim(),5).orEmpty()
                }
            }
            return addresses.filter {it.hasLatitude() && it.hasLongitude()}.map {
                AddressMatch(it.getAddressLine(0) ?: listOfNotNull(it.thoroughfare,it.subThoroughfare,it.locality,it.countryName).joinToString(", "),LatLng(it.latitude,it.longitude))
            }.distinctBy {it.label}
        } catch(e:TimeoutCancellationException) { throw NokiApiException("La búsqueda tardó demasiado. Intenta de nuevo o elige el punto en el mapa.") }
    }
    suspend fun drivingRoute(origin: LatLng, destination: LatLng): RoutePath = withContext(Dispatchers.IO) {
        val url = "https://router.project-osrm.org/route/v1/driving/${origin.longitude},${origin.latitude};${destination.longitude},${destination.latitude}?overview=full&geometries=geojson"
        client.newCall(Request.Builder().url(url).header("User-Agent", "Noki/0.4 (nokiappcorporation@gmail.com)").build()).execute().use { response ->
            if (!response.isSuccessful) throw NokiApiException("El servicio de rutas no respondió. Puedes abrir la dirección en tu aplicación de mapas.")
            val json = JSONObject(response.body!!.string())
            if (json.optString("code") != "Ok") throw NokiApiException("No encontramos una ruta por carretera entre estos lugares.")
            val route = json.getJSONArray("routes").getJSONObject(0)
            val coords = route.getJSONObject("geometry").getJSONArray("coordinates")
            RoutePath((0 until coords.length()).map { coords.getJSONArray(it).let { p -> LatLng(p.getDouble(1), p.getDouble(0)) } }, route.getDouble("distance"), route.getDouble("duration"))
        }
    }
    suspend fun currentLocation(context: Context): LatLng = withContext(Dispatchers.Main) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) throw NokiApiException("La ubicación no está autorizada. Puedes pulsar Elegir punto de partida para indicarlo en el mapa.")
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val providers = manager.getProviders(true).filter { (fine && it == LocationManager.GPS_PROVIDER) || it == LocationManager.NETWORK_PROVIDER }
        if (providers.isEmpty()) throw NokiApiException("La ubicación del teléfono está desactivada. Puedes usar un punto de partida manual en el mapa.")
        @Suppress("MissingPermission")
        val recent = providers.mapNotNull { runCatching { manager.getLastKnownLocation(it) }.getOrNull() }
            .filter { System.currentTimeMillis() - it.time < 120_000 }.minByOrNull { it.accuracy }
        if (recent != null) return@withContext LatLng(recent.latitude, recent.longitude)
        try {
            withTimeout(18000) {
                suspendCancellableCoroutine { continuation ->
                    val listener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            manager.removeUpdates(this)
                            if (continuation.isActive) continuation.resume(LatLng(location.latitude, location.longitude))
                        }
                    }
                    continuation.invokeOnCancellation { manager.removeUpdates(listener) }
                    @Suppress("MissingPermission")
                    try { providers.forEach { manager.requestLocationUpdates(it, 1000L, 0f, listener, Looper.getMainLooper()) } }
                    catch (_: SecurityException) { manager.removeUpdates(listener); if(continuation.isActive) continuation.resumeWithException(NokiApiException("No pudimos acceder a la ubicación. Usa un punto de partida manual.")) }
                }
            }
        } catch (_: TimeoutCancellationException) { throw NokiApiException("No logramos ubicarte. Usa un punto de partida manual en el mapa o vuelve a intentar al aire libre.") }
    }
}
