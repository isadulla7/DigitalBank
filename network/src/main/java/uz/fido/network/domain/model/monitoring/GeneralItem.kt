package uz.fido.network.domain.model.monitoring

import uz.fido.network.domain.model.payment.local_history.LocalMonitoring

class GeneralItem : ListItem() {
    var localMonitoringItem: LocalMonitoring? = null
    override val type = TYPE_GENERAL
}