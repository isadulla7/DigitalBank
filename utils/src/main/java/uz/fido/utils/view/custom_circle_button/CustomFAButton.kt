package uz.fido.utils.view.custom_circle_button

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import uz.fido.utils.R

class CustomFAButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val faButton: FloatingActionButton
    private val progressView: ProgressBar

    init {
        View.inflate(context, R.layout.custom_fa_button, this)
        progressView = findViewById(R.id.progress_view)
        faButton = findViewById(R.id.fabutton)
    }

    fun setOnClickListener(function: () -> Unit) {
        faButton.setOnClickListener {
            function.invoke()
        }
    }

    fun isEnabled(isEnabled: Boolean) {
        faButton.isEnabled = isEnabled
    }

    fun setProgress(visibility: Boolean) {
        if (visibility) {
            progressView.visibility = View.VISIBLE
            faButton.isClickable = false
            faButton.setImageDrawable(null)
        } else {
            progressView.visibility = View.GONE
            faButton.setImageResource(R.drawable.ic_arrow_right)
            faButton.isClickable = true
        }
    }

}



