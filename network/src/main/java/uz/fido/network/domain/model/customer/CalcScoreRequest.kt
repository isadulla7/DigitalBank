package uz.fido.network.domain.model.customer

import java.io.Serializable

data class CalcScoreRequest(
    val invited_user_id: String
) : Serializable