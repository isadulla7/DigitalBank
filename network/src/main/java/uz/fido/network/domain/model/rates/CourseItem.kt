package uz.fido.network.domain.model.rates

import java.io.Serializable

data class CourseItem(
    val sbCourse: Double,
    val endDate: String,
    val currencyCode: String,
    val currencyChar: String,
    val buyingRate: Double,
    val beginDate: String,
    val sellingRate: Double,
    val quoteCurrency: String,
    val quote_currency: String,
    var order: Int,
    var buyingRateDiff: Double? = 0.0,
    var sellingRateDiff: Double? = 0.0,
    var autoId: Int = 0
) : Serializable