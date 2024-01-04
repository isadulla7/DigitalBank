package uz.fido.network.domain.model.loans.overdraft

import java.io.Serializable

data class OverdraftDetail2(
    var limit: ArrayList<OverdraftDetail3>,
    var loanid: String,
    var cardNumber: String
) : Serializable