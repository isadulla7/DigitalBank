package uz.fido.universaldigital.ui.utils.extensions

import io.paperdb.Paper
import uz.fido.utils.const.Const

fun checkForFingerPrintConfirmation(): Boolean {
    return Paper.book().read(Const.PAPER_PAYMENT_PIN_CONFIRMATION, false) == true
}