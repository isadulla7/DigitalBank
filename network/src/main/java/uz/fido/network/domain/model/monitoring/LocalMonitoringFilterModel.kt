package fido.mkbank.ui.ib.fragments.menu.menu_monitoring.model

import java.io.Serializable

data class LocalMonitoringFilterModel(
    var serviceId: String,
    var userId: String,
    var isChecked: Boolean = false,
) : Serializable