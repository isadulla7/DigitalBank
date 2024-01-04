package uz.fido.utils.view.bottom_menu_anim

import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.doOnPreDraw

fun View.showAnimWithSlideUp(duration: Long = 300, endAction: () -> Unit = {}) {
    if (isNotVisible()) {
        show()
        this.doOnPreDraw {
            translationY = height.toFloat()
            alpha = 0.5f
            animate()
                .alpha(1f)
                .translationY(0f)
                .withEndAction(endAction)
                .setInterpolator(DecelerateInterpolator())
                .setDuration(duration)
                .start()
        }
    } else {
        endAction()
    }
}

fun View.hideAnimWithSlideDown(duration: Long = 300, endAction: () -> Unit = {}) {
    if (isVisible()) {
        animate()
            .alpha(0.5f)
            .translationY(height.toFloat())
            .withEndAction {
                hide()
                endAction()
            }
            .setInterpolator(AccelerateInterpolator())
            .setDuration(duration)
            .start()
    } else {
        endAction()
    }
}

fun View.hide(): View {
    this.visibility = (View.GONE)
    return this
}

fun View.show() {
    this.visibility = (View.VISIBLE)
}

fun View.isVisible(): Boolean {
    return this.visibility == (View.VISIBLE)
}

fun View.isNotVisible(): Boolean {
    return this.visibility != (View.VISIBLE)
}
