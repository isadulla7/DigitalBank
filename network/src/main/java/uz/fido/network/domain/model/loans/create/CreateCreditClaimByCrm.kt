package uz.fido.network.domain.model.loans.create

data class CreateCreditClaimByCrm(
    val pfProductId: String,
    val loanSum: String,
    val initialFeeType: String,
    val initialFeePer: String,
    val initialFeeSum: String,
    val ln_month: String
)