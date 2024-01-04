package uz.fido.network.domain.model.money_transfer.list

data class MoneyTransferHistory(
    val amount: String,
    val cardNumber: String,
    val city: String,
    val codeFilial: String,
    val comission: String,
    val corAddress: String,
    val corBirthDate: String,
    val corBirthPlace: String,
    val corCitizenship: String,
    val corDocNumber: String,
    val corDocSerial: String,
    val corDocType: String,
    val corFirstName: String,
    val corLastName: String,
    val corPatronymic: String,
    val currencyCode: String,
    val dateCreated: String,
    val phone: String,
    val requestType: String,
    val status: String,
    val statusName: String,
    val transferId: Int,
    val typeTransfer: String
)