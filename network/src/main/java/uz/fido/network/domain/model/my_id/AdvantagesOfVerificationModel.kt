package uz.fido.network.domain.model.my_id

import java.io.Serializable

data class AdvantagesOfVerificationModel(
    var id:Int=0,
    var header_name:String,
    var name:String,
    var viewType:Int?=1,
    var verification: String,
    var unVerification: String
): Serializable