package uz.fido.network.domain.model.cards

import java.io.Serializable

data class OrderCardTypeResponse(
    val product_types: ArrayList<ProductType>,
    val request_id: Int,
    val code: Int
) : Serializable