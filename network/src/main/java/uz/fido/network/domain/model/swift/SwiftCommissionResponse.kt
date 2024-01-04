package uz.fido.network.domain.model.swift

data class SwiftCommissionResponse(
    val code: Int,
    val usd_comission: Int,
    val commis_amount: Int,
    val commis_date: String,
    val msg: String,
    val request_id: Int
)