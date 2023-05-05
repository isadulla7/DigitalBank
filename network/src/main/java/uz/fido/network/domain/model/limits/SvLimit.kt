package uz.fido.network.domain.model.limits

import java.io.Serializable

data class SvLimit(
    val card_no: String,
    val lmt_id: String,
    val tdy: String,
    val lmt: String,
    val mem_no: String,
    val dsc: String,
    val end_date: String
) : Serializable