package uz.fido.network.domain.model.my_id

import java.io.Serializable

data class TemporaryRegistration(
    val Adress: String,
    val CountryID: String,
    val CountryValue: String,
    val DateFrom: String,
    val DateTill: String,
    val DistrictID: String,
    val DistrictValue: String,
    val RegionID: String,
    val RegionValue: String,
    val RegistrationDate: String
) : Serializable