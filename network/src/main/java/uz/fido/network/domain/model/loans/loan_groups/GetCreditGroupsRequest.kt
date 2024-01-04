package uz.fido.network.domain.model.loans.loan_groups

import java.io.Serializable

data class GetCreditGroupsRequest(
    val token: String,
    val command: String
): Serializable