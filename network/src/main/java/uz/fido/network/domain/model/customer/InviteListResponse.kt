package uz.fido.network.domain.model.customer

import java.io.Serializable

data class InviteListResponse(
    val curr_user_score: String,
    val totalPages: Int,
    val contact_list: ArrayList<Offered>,
    val last: String,
    val request_id: String,
    val code: String,
    val start_date: String,
    val end_date: String,
    val curr_user_position: Int,
    val reward_amount: String,
    val msg: String,
) : Serializable