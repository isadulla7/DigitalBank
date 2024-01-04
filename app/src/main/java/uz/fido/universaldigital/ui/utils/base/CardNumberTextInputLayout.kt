package uz.fido.universaldigital.ui.utils.base

import android.content.Context
import android.util.AttributeSet

class CardNumberTextInputLayout(context: Context, attrs: AttributeSet) :
    BaseTextInputLayout(context, attrs) {

    init {
        setMaxLength(MAX_LENGTH)
    }

    companion object {
        const val MAX_LENGTH = 20
    }
}