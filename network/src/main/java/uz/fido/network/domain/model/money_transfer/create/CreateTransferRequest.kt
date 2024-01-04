package uz.fido.network.domain.model.money_transfer.create

data class CreateTransferRequest(

    val amount: String,
    val phone: String,
    val countryCode: String,
    val cardNumber: String,
    val corFirstName: String,
    val corLastName: String,
    val corPatronymic: String,
    val transferId: String,
    val operationId: String,
    val city: String,
    val corPhone: String,
    val mtcn: String,

    val requestType: String? = "",
    val corAddress: String? = "",
    val corBirthDate: String? = "",
    val corBirthPlace: String? = "",
    val corCitizenship: String? = "",
    val corDocNumber: String? = "",
    val corDocSerial: String? = "",
    val corDocType: String? = "",
    val currencyCode: String? = ""
)