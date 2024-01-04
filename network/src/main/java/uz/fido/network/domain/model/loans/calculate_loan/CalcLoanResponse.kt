package uz.fido.network.domain.model.loans.calculate_loan

import uz.fido.network.domain.model.loans.calculate_loan_manual.CalculatedData

data class CalcLoanResponse(
    val code: Int,
    val msg: String,
    val operations: ArrayList<CalculatedData>,
    val ora_msg: String
)