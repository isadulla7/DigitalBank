package uz.fido.network.domain.model.loans

class CreditPassportInfo(
    val data:List<CreditPasswordDetails>,
    val request_id:String?=null,
    val code:String?=null,
    val msg:String?=null,
)

class CreditPasswordDetails(
    val fullName:String?="",
    val productName:String?="",
    val filialName:String?="",
    val saldo28:String?="",
    val loanId:String?="",
    val amount:String?="",
    val loanContractId:String?=""
)