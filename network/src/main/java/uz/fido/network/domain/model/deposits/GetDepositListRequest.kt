package uz.fido.network.domain.model.deposits

import java.io.Serializable

data class GetDepositListRequest(
    val command: String
): Serializable