package uz.fido.network.domain.model.monitoring

import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringItem

class UzcardItem : ListItem() {
    var uzcardMonitoringItem: UzcardMonitoringItem? = null
    override val type = TYPE_GENERAL
}