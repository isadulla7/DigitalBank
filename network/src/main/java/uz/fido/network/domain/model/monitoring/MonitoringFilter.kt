package fido.mkbank.ui.ib.fragments.menu.menu_monitoring.model

import java.io.Serializable

data class MonitoringFilter(
    var dateBegin: String,
    var dateEnd: String,
    var dateType: String? = null,
    var dateText: String? = null,
    var selectedCard: String? = null,
    var filterAction: Int? = 2,
    var selectedCards: ArrayList<String>? = ArrayList(),
    var service_ids: ArrayList<String>? = ArrayList(),
    var object_values: ArrayList<String>? = ArrayList(),
    var minAmount: String? = "",
    var maxAmount: String? = ""
) : Serializable