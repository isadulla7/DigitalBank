package uz.fido.network.domain.model.swift

data class SwiftTransferListResponse(
    val code: Int,
    val responseBody: ResponseBody,
    val msg: String,
    val request_id: Int
)

data class ResponseBody(
    val data: ArrayList<SWIFTList>
)