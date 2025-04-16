package uz.fido.network.domain.model.monitoring

import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringItem

class HumoItem : ListItem() {
    var humoMonitoringItem: HumoMonitoringItem? = null
    override val type = TYPE_GENERAL
}