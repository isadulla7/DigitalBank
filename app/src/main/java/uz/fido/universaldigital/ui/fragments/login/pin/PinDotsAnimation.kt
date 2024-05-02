package uz.fido.universaldigital.ui.fragments.login.pin

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentActivity
import uz.fido.utils.device.longVibrate
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.exp
import kotlin.math.sin

object PinDotsAnimation {

    private var interpolator = AccelerateDecelerateInterpolator()
    private var animationDuration = 400
    private var currentViewIndex = -1
    private var aheadTime = 200
    private var timer: Timer? = null

    private fun scaleAnimation(linearLayoutCompat: LinearLayoutCompat) {
        val view = linearLayoutCompat.getChildAt(currentViewIndex)
        val duration = (animationDuration / 2).toLong()
        view.animate().setInterpolator(interpolator).scaleX(1.25f).scaleY(1.25f)
            .setDuration(duration)
            .withEndAction {
                view.animate().setInterpolator(interpolator).scaleX(1f).scaleY(1f)
                    .setDuration(duration).start()
            }.start()
    }

    fun stopPinDotsAnimation() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    fun errorAnimation(view: View, activity: FragmentActivity) {
        longVibrate(activity)
        val frequency = 4f
        val delay = 3f
        val decayingSineWave = TimeInterpolator { input ->
            val raw = sin(frequency * input * 2 * Math.PI)
            (raw * exp(-input * delay.toDouble())).toFloat()
        }
        view.animate().xBy(-35f).setInterpolator(decayingSineWave).setDuration(300).start()
    }

    fun gatherAnimation(view: ImageView, progressBar: ProgressBar) {
        val x = progressBar.x + progressBar.width / 2 - view.width / 2
        view.animate().x(x).setDuration(400).withEndAction {
            view.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }.start()
    }

    fun reverseDot(view: ImageView, x: Float) {
        view.animate().x(x).setDuration(400).start()
    }

    fun zoomInAndOutAnim(imageView: ImageView) {
        val anim = ValueAnimator.ofFloat(1f, 1.35f)
        anim.duration = 100
        anim.addUpdateListener { animation ->
            imageView.scaleX = animation.animatedValue as Float
            imageView.scaleY = animation.animatedValue as Float
        }
        anim.repeatCount = 1
        anim.repeatMode = ValueAnimator.REVERSE
        anim.start()
    }

}