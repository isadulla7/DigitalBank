package uz.fido.network.domain.model.client_info

import java.io.Serializable

data class ClientDetailedInfo(
    val birthCountry: String,
    val birthDate: String? = "",
    val birthPlace: String,
    val citizenship: String,
    val clientCode: String,
    val clientId: String,
    val clientUid: String,
    val codeFilial: String,
    val docExpireDate: String,
    val docIssueDate: String,
    val docIssuePlace: String,
    val docType: String,
    val email: String,
    val firstName: String,
    val gender: String,
    val inn: String,
    val lastName: String,
    val maritalStatus: String,
    val middleName: String,
    val mobilePhone: String,
    val number: String,
    val phone: String,
    val pnfl: String,
    val residenceAddress: String,
    val residenceCountry: String,
    val residenceDistrict: String,
    val residenceRegion: String,
    val series: String? = "",
    val status: String,
    var addressRegDate: String? = null
) : Serializable