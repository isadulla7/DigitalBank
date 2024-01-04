package uz.fido.network.domain.model.monitoring

import fido.mkbank.ui.ib.fragments.menu.menu_monitoring.model.ListItem
import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringItem

class UzcardItem : ListItem() {
    var svMonitoringItem: SVMonitoringItem? = null
    override val type = TYPE_GENERAL
}