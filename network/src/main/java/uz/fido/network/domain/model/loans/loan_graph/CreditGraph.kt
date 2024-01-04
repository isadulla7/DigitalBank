package uz.fido.network.domain.model.loans.loan_graph

import java.io.Serializable

data class CreditGraph(
    val amount: String,
    val repaymentDate: String,
    val saldo: String,
    var interestOnTermDebt: String,
    var recommendedAmount: String,
    var position:Int=0

) : Serializable