package uz.fido.universaldigital.ui.fragments.login.confirm_sms.state

import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.UserIdentifyState.IDENTIFIED
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.UserIdentifyState.IDENTIFIED_BY_CARD
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.state.UserIdentifyState.NOT_IDENTIFIED

object UserIdentifyState {
    const val NOT_IDENTIFIED = 0
    const val IDENTIFIED = 1
    const val IDENTIFIED_BY_CARD = 2
    const val DEFAULT = NOT_IDENTIFIED
}

fun isIdentifiedByCard(userIdentifyState: Int) = userIdentifyState == IDENTIFIED_BY_CARD

fun isFullyIdentified(userIdentifyState: Int, userDeviceState: String) = userIdentifyState == IDENTIFIED && userDeviceState == DeviceIdentifyState.IDENTIFIED

fun isNotIdentified(userIdentifyState: Int, userDeviceState: String) = userIdentifyState == NOT_IDENTIFIED && userDeviceState == DeviceIdentifyState.NOT_IDENTIFIED

fun isUserNotIdentifiedButDeviceIdentified(userIdentifyState: Int, userDeviceState: String) = userIdentifyState == NOT_IDENTIFIED && userDeviceState == DeviceIdentifyState.IDENTIFIED

fun isUserIdentifiedButDeviceNot(userIdentifyState: Int, userDeviceState: String) = userIdentifyState == IDENTIFIED && userDeviceState == DeviceIdentifyState.NOT_IDENTIFIED


