package uz.fido.network.domain.model.monitoring.home

import uz.fido.network.domain.model.monitoring.ListItem

class HomeGeneralItem : ListItem() {
    var itemHomeHistory: ItemHomeHistory? = null
    override val type = TYPE_GENERAL
}