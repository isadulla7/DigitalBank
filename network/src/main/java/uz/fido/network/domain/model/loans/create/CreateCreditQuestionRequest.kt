package uz.fido.network.domain.model.loans.create

data class CreateCreditQuestionRequest(
    val ccId: String,
    val params: HashMap<String, String>
)