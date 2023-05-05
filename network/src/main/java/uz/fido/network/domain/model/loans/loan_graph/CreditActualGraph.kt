package uz.fido.network.domain.model.loans.loan_graph

import java.io.Serializable

data class CreditActualGraph(
    var redempDate: String,
    var beforeRedempDebt: String,
    var v_Delta_Days: String,
    var factSaldo: String,
    var factTotal: String,
    var not_acctual_data: String,
    var percent: String,
    var factPerc: String,
    var redempTotal: String,
    var redempPerc: String,
    var redempDebt: String,
    var fact_debt: String
) : Serializable