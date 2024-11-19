package uz.fido.network.domain.model.monitoring.filter

class FilterSaveVh(
    val startDate: String,
    val endDate: String,
    val maxAmount: String,
    val minAmount: String,
    val plusMinus: String,
    val cardNumber: String,
    val cardList: ArrayList<FilterCard>,
    val serviceList: ArrayList<UserPayedService>
)