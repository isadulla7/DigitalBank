package uz.fido.universaldigital.ui.fragments.login.pin

import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentPinCodeBinding

@AndroidEntryPoint
class PinCodeFragment : BaseFragment<FragmentPinCodeBinding, PinCodeViewModel>(
    FragmentPinCodeBinding::inflate, PinCodeViewModel::class.java
) {

    companion object {
        const val PIN_OPERATION = "operation_type"
        const val PIN_OPERATION_CHANGE_PIN = "change_pin"
        const val PIN_OPERATION_SIGN_UP = "sign_up"
        const val PIN_OPERATION_SET_PIN = "set_pin_code"
        const val PIN_OPERATION_SIGN_IN = "sign_in"
        const val PIN_OPERATION_SET_HUMO_PAY = "set_humo_pay_pin"
        const val PIN_OPERATION_HUMO_PAY = "humo_pay_pin"
        const val PIN_OPERATION_PAYMENT = "confirm_payment"
    }

}