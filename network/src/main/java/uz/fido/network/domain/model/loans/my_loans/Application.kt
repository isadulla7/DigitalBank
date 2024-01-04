package uz.fido.network.domain.model.loans.my_loans

import java.io.Serializable

data class Application(
    val claim_date: String,
    val claim_num: String,
    val description: String,
    val error_msg: String,
    val error_type: String,
    val guar_type: String,
    val initiation_date: String,
    val loan_term: String,
    val mfo: String,
    val perc_rate: String,
    val product_id: String,
    val product_name: String,
    val status: String,
    val summ_claim: String

) : Serializable