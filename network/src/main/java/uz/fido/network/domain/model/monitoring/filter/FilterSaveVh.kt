package uz.fido.network.domain.model.monitoring.filter

class FilterSaveVh(
    val startDate: String = "",
    val endDate: String = "",
    val maxAmount: String = "",
    val minAmount: String = "",
    val operationType: String = "",
    val cardNumber: String = "",
    val cardList: ArrayList<FilterCard> = arrayListOf(),
    val serviceList: ArrayList<UserPayedService> = arrayListOf()
)