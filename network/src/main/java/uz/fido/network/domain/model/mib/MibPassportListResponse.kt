package uz.fido.network.domain.model.mib

data class MibPassportListResponse(
    val passports: ArrayList<Mib>,
    val code: Int,
    val msg: String
)