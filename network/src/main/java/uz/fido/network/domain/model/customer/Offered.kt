package uz.fido.network.domain.model.customer

import java.io.Serializable

data class Offered(
    val state_name: String,
    val phone_number: String,
    val state_id: Int,
    val name: String,
    val created_date: String,
    val amount: String
) : Serializable