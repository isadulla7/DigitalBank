package uz.fido.network.domain.model.swift

data class SwiftTransferResponse(
    val code: Int,
    val `data`: List<BICData>,
    val maxPage: Int,
    val msg: String,
    val request_id: Int,
    val totalRecords: Int
)