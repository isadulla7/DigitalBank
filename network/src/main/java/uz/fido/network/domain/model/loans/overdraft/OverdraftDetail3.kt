package uz.fido.network.domain.model.loans.overdraft

import java.io.Serializable

data class OverdraftDetail3(
    var amount: String,
    var date: String,
    var state: String
) : Serializable