package uz.fido.network.domain.model.monitoring.uzcard

import java.io.Serializable

data class SVMonitoringItem(
    val merchant_name: String,
    val merchant_id: String,
    val ref_number: String,
    val tran_type: String,
    val terminal_id: String,
    val tran_date: String,
    val bank_code: String,
    val card_num: String,
    val tran_amount: String,
    val address: String,
    val reversal: String,
    val category_name: String
) : Serializable