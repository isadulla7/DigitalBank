package uz.fido.utils.view.custom_snackbar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.ContentViewCallback
import uz.fido.utils.R
import uz.fido.utils.const.AlertType
import uz.fido.utils.view.custom_text_view.TextViewRegular

class CustomSnackbarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    private val snackbarBackground: CardView
    private val snackbarImage: ImageView
    private val message: TextViewRegular

    init {
        View.inflate(context, R.layout.custom_snackbar_view, this)
        this.snackbarImage = findViewById(R.id.snacbar_image)
        this.message = findViewById(R.id.message)
        this.snackbarBackground = findViewById(R.id.snackbar_background)
    }

    fun makeSnackbar(message: String, alertType: AlertType) {

        val color: Int = when {
            alertType == AlertType.WARNING -> R.color.status_waiting
            alertType == AlertType.ERROR -> R.color.status_not_identified
            alertType == AlertType.SUCCESS -> R.color.status_identified
            message == context.getString(R.string.please_try_again_later) -> {
                R.color.status_waiting
            }

            else -> {
                R.color.primaryPurple
            }
        }

        snackbarBackground.setCardBackgroundColor(ContextCompat.getColor(context, color))
        this.message.text = message
        this.message.maxLines = 5
        snackbarImage.setImageResource(if (alertType == AlertType.SUCCESS) R.drawable.snackbar_success_image else R.drawable.snackbar_image_info)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        val scaleX = ObjectAnimator.ofFloat(snackbarImage, View.SCALE_X, 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(snackbarImage, View.SCALE_Y, 0f, 1f)
        val animatorSet = AnimatorSet().apply {
            interpolator = OvershootInterpolator()
            setDuration(500)
            playTogether(scaleX, scaleY)
        }
        animatorSet.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {}

}



