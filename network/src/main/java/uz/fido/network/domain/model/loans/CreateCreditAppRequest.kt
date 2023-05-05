package uz.fido.network.domain.model.loans

data class CreateCreditAppRequest(
    val employmentSign: String = "0",
    val periodUse: String, //selected range (mmmdd) format f.e. (01200) - 12month
    val productId: String, //group
    val summClaim: String, //user credit amount in tin
    val loanLinePurpose: String // purposeLoan from group model
)