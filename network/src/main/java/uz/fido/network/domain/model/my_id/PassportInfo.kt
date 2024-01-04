package uz.fido.network.domain.model.my_id

import java.io.Serializable

data class PassportInfo(
    val FT_DOCUMENT_CLASS_CODE: String,
    val FT_ISSUING_STATE_CODE: String,
    val FT_PLACE_OF_BIRTH: String,
    val FT_MRZ_TYPE: String,
    val FT_OPTIONAL_DATA: String,
    val FT_ISSUING_STATE_NAME: String,
    val FT_MRZ_STRINGS: String,
    val name: String,
    val passportNo: String,
    val passportExpire: String,
    val dateOfBirth: String,
    val country: String,
    val sex: String,
    val age: String
) : Serializable