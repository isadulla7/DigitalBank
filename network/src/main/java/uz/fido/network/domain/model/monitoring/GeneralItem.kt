package uz.fido.network.domain.model.monitoring

import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import fido.mkbank.ui.ib.fragments.menu.menu_monitoring.model.ListItem


class GeneralItem : ListItem() {
    var svMonitoringItem: LocalMonitoring? = null
    override val type = TYPE_GENERAL
}