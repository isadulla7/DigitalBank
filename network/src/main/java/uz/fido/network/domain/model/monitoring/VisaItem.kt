package uz.fido.network.domain.model.monitoring

import uz.fido.network.domain.model.monitoring.currency_card.CurrencyCardMonitoringItem

class VisaItem : ListItem() {
    var visaMonitoringItem: CurrencyCardMonitoringItem? = null
    override val type = TYPE_GENERAL
}