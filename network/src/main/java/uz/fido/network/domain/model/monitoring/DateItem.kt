package uz.fido.network.domain.model.monitoring

import fido.mkbank.ui.ib.fragments.menu.menu_monitoring.model.ListItem

class DateItem : ListItem() {
    var date: String? = null
    var amount:String=""
    override val type = TYPE_DATE
}