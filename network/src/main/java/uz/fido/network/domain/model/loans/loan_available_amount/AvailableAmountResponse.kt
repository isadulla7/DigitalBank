package uz.fido.network.domain.model.loans.loan_available_amount

data class AvailableAmountResponse(
    val amount: String,
    val code: Int,
    val msg: String,
    val ora_msg: String
)