package uz.fido.network.domain.model.monitoring

import java.math.BigDecimal

class HeaderItem(date: String, sum: BigDecimal) {
    val date: String

    init {
        this.date = date
    }

    fun getDateFormatted(): String {
        var res = ""
        if (date.length == 8) {
            res = date.substring(0, 4) + "/"
            res = res + date.substring(4, 6) + "/"
            res = res + date.substring(6, 8) + " "

        }
        return res
    }
}