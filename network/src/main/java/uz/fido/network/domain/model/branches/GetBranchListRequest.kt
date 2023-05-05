package uz.fido.network.domain.model.branches

import java.io.Serializable

data class GetBranchListRequest(
    val command: String,
    val filial_type: String
) : Serializable