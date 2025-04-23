package uz.fido.network.domain.model.deposits.my_deposit

import java.io.Serializable

data class ClientDeposit(
    val account: String? = null,
    val accountPercent: String? = null,
    val amount: String? = null,
    val book: String? = null,
    val capitalization: String? = null,
    val clientCode: String? = null,
    val clientName: String? = null,
    val closingDate: String? = null,
    val currencyCode: String? = null,
    val currencyChar: String? = null,
    val depName: String? = null,
    val depTemp: String? = null,
    val description: String? = null,
    val earlyTermination: String? = null,
    val filialCode: String? = null,
    val filialName: String? = null,
    val inventoryDate: String? = null,
    val isMobile: String? = null,
    val localCode: String? = null,
    val openDate: String? = null,
    val partialWrite: String? = null,
    val percent: String? = null,
    var persSum: String? = "0",
    val prolongation: String? = null,
    val prolongationDate: String? = null,
    val prolongationSign: String? = null,
    val replenishment: String? = null,
    val savDepId: String? = null,
    val status: String? = null,
    val sumDep: String? = null,
    val withdrawInterest: String? = null,
    val interestPayable: String? = null
) : Serializable {
    fun isOfflineDeposit(): Boolean {
        return isMobile == "0"
    }
}