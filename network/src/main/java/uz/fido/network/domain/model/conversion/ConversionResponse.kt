package uz.fido.network.domain.model.conversion

class ConversionResponse(
    val ext_id:String?="",
    val sms_length:Int=6,
    val code: Int? = null,
    val msg: String? = null,
    val string_line: String? = null,
)