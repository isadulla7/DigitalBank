package uz.fido.network.domain.model.loans.calculate_loan_manual

data class CalcLoanManualResponse(
    val code: Int,
    val msg: String,
    val operations: ArrayList<CalculatedData>,
    val ora_msg: String
)