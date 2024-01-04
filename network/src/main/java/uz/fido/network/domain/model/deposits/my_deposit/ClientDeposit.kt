package uz.fido.network.domain.model.deposits.my_deposit

import java.io.Serializable

data class ClientDeposit(
    val account: String,
    val accountPercent: String,
    val amount: String,
    val book: String,
    val capitalization: String,
    val clientCode: String,
    val clientName: String,
    val closingDate: String,
    val currencyCode: String,
    val currencyChar: String,
    val depName: String,
    val depTemp: String,
    val description: String,
    val earlyTermination: String,
    val filialCode: String,
    val filialName: String,
    val inventoryDate: String,
    val isMobile: String,
    val localCode: String,
    val openDate: String,
    val partialWrite: String,
    val percent: String,
    var persSum: String? = "0",
    val prolongation: String,
    val prolongationDate: String,
    val prolongationSign: String,
    val replenishment: String,
    val savDepId: String,
    val status: String,
    val sumDep: String,
    val withdrawInterest: String,
    val interestPayable: String? = null
) : Serializable