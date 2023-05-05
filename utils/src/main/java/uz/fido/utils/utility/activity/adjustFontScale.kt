package uz.fido.utils.utility.activity

import android.app.Activity
import android.content.Context.WINDOW_SERVICE
import android.content.res.Configuration
import android.view.WindowManager

fun Activity.adjustFontScale(configuration: Configuration) {
    if (configuration.fontScale > 1.20) {
        configuration.fontScale = 1.10.toFloat()
        val metrics = resources.displayMetrics
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager?
        if (windowManager != null) {
            windowManager.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density
            applicationContext.resources.updateConfiguration(configuration, metrics)
        }
    }
}