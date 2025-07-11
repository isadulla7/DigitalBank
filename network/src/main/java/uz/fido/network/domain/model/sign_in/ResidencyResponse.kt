package uz.fido.network.domain.model.sign_in

import com.google.gson.annotations.SerializedName

data class ResidencyResponse(
    @SerializedName("is_Resident")
    val isResident: String
)