package uz.fido.universaldigital.ui.utils.extensions

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import io.paperdb.Paper
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.fragments.login.pin.PinCodeFragment
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto

fun checkForFingerPrintConfirmation(): Boolean {
    return Paper.book().read(Const.PAPER_PAYMENT_PIN_CONFIRMATION, false)
}