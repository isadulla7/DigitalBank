package uz.fido.universaldigital.ui.utils.home_utils
import android.text.TextWatcher

abstract class DoAfterTextWatcher:TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

}