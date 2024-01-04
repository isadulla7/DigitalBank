package uz.fido.network.domain.model.monitoring.home

import fido.mkbank.ui.ib.fragments.menu.menu_monitoring.model.ListItem

class HomeGeneralItem : ListItem() {
    var itemHomeHistory: ItemHomeHistory? = null
    override val type = TYPE_GENERAL
}