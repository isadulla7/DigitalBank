package uz.fido.network.domain.model.monitoring.categories

import java.io.Serializable

data class SetCategoryRequest(
    val merchant_id: String,
    val category_id: Int
) : Serializable