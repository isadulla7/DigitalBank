package uz.fido.network.domain.model.loans

data class CreateCreditRequestNew(
    var amount: String, var to_object_value: String, var productId: String
) : java.io.Serializable
