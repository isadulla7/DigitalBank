package uz.fido.utils.view.custom_text_view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet

class CarNumberTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        init()
    }

    private fun init() {
        typeface = Typeface.createFromAsset(
            context.resources.assets, "fonts/fe.ttf"
        )
    }
}