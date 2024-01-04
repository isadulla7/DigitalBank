package uz.fido.network.domain.model.customer

import java.io.Serializable

data class GetRatingsRequest(
    val page_number: Int,
    val page_item_size: Int,
    val start_date: String? = null,
    val end_date: String? = null
) : Serializable