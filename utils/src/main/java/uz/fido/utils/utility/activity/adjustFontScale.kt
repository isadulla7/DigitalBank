package uz.fido.utils.utility.activity

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context.WINDOW_SERVICE
import android.content.res.Configuration
import android.graphics.Color
import android.view.WindowManager
import androidx.core.content.ContextCompat
import uz.fido.utils.R

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

fun Activity.tintSystemBars(toColor: Int, fromColor: Int? = null) {
    val statusBarColor = ContextCompat.getColor(this, fromColor ?: R.color.whiteColor)
    val statusBarToColor = ContextCompat.getColor(this, toColor)
    val anim = ValueAnimator.ofFloat(0f, 1f)
    anim.addUpdateListener { animation -> // Use animation position to blend colors.
        val position = animation.animatedFraction
        val blended = blendColors(statusBarColor, statusBarToColor, position)
        window.statusBarColor = blended
    }
    anim.setDuration(10).start()
}

fun blendColors(from: Int, to: Int, ratio: Float): Int {
    val inverseRatio = 1f - ratio
    val r: Float = Color.red(to) * ratio + Color.red(from) * inverseRatio
    val g: Float = Color.green(to) * ratio + Color.green(from) * inverseRatio
    val b: Float = Color.blue(to) * ratio + Color.blue(from) * inverseRatio
    return Color.rgb(r.toInt(), g.toInt(), b.toInt())
}