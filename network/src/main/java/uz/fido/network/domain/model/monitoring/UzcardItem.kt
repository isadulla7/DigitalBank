package uz.fido.network.domain.model.monitoring

import uz.fido.network.domain.model.monitoring.uzcard.SVMonitoringItem

class UzcardItem : ListItem() {
    var svMonitoringItem: SVMonitoringItem? = null
    override val type = TYPE_GENERAL
}