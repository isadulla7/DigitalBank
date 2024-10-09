package uz.fido.universaldigital.ui.fragments.monitoring.cheque

import java.io.Serializable

data class TransferChequeModel(
    val transactionDate: String,
    val transactionAmount: String,
    val transactionNumber: String,
    val transactionFee: String,
    val senderCardNumber: String,
    var senderCardName: String,
    val receiverCardNumber: String,
    var receiverCardName: String,
    val operationName: String,
    val totalAmount: String
) : Serializable