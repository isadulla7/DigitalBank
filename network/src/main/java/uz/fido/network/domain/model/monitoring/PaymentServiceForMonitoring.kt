package fido.mkbank.ui.ib.fragments.menu.menu_monitoring.model

import java.io.Serializable

data class PaymentServiceForMonitoring(
    var icon: String,
    var name: String,
    var serviceId: Int = 0,
    var isChecked: Boolean = false
) : Serializable