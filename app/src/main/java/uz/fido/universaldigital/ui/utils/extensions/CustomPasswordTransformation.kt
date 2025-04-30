package uz.fido.universaldigital.ui.utils.extensions

import android.text.method.PasswordTransformationMethod
import android.view.View

class CustomPasswordTransformation : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return object : CharSequence {
            override val length: Int get() = source.length
            override fun get(index: Int): Char = '•'
            override fun subSequence(startIndex: Int, endIndex: Int) = this
        }
    }
}