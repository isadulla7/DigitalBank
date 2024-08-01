package uz.fido.universaldigital.ui.fragments.payment.abc_confirm

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.utils.const.Const
import uz.fido.utils.format.Format.convertFromTiynDivide
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto

fun Fragment.checkForPaymentSms(
    paymentService: PaymentService?,
    amount: String,
    card: CardResponse
): Boolean {
    var goToSms = false
    if (card.safe_mode == "Y" || card.pay_with_sms == "Y") {
        goToSms = true
    } else if (
        paymentService?.sms_control_limit.toString() != "-1" &&
        Format.convertFromStringToBigDecimal(convertFromTiynDivide(amount)) >=
        Format.convertFromStringToBigDecimal(paymentService?.sms_control_limit.toString())
    ) {
        goToSms = true
    }
    if (goToSms) {
        goto(
            R.id.confirmSmsFragment, bundleOf(
                Const.OPERATION to ConfirmSmsFragment.SMS_OPERATION_PAYMENT_KEY,
                ConfirmSmsFragment.SMS_FROM_OBJECT_VALUE to card.object_id,
                ConfirmSmsFragment.SMS_AMOUNT to amount,
                ConfirmSmsFragment.SMS_SERVICE_ID to paymentService?.service_id.toString()
            )
        )
    }
    return goToSms
}