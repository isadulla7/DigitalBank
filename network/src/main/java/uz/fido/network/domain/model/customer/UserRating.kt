package uz.fido.network.domain.model.customer

import java.io.Serializable

data class UserRating(
    val user_id: String,
    val reward_amount: String?=null,
    val position: Int?=null,
    val name: String?=null,
    val score: String?=null,
    val surname: String?=null
) : Serializable