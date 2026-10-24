package co.noki.app.ui

import android.content.Context
import androidx.compose.runtime.*

enum class AppAppearance { SYSTEM, LIGHT, DARK }
data class AccessibilityOptions(val textScale: Float = 1f, val highContrast: Boolean = false, val reduceMotion: Boolean = false, val appearance: AppAppearance = AppAppearance.SYSTEM)
class AppPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("noki_preferences", Context.MODE_PRIVATE)
    var options by mutableStateOf(AccessibilityOptions(preferences.getFloat("textScale", 1f), preferences.getBoolean("highContrast", false), preferences.getBoolean("reduceMotion", false), runCatching { AppAppearance.valueOf(preferences.getString("appearance", "SYSTEM")!!) }.getOrDefault(AppAppearance.SYSTEM))); private set
    var welcomed by mutableStateOf(preferences.getBoolean("welcomed", false)); private set
    fun update(value: AccessibilityOptions) {
        options = value
        preferences.edit().putFloat("textScale", value.textScale).putBoolean("highContrast", value.highContrast).putBoolean("reduceMotion", value.reduceMotion).putString("appearance",value.appearance.name).apply()
    }
    fun welcomeDone() { welcomed = true; preferences.edit().putBoolean("welcomed", true).apply() }
}
val LocalAccessibility = staticCompositionLocalOf { AccessibilityOptions() }
val LocalAccessibilityUpdate = staticCompositionLocalOf<(AccessibilityOptions) -> Unit> { {} }
