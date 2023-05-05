package uz.fido.network.domain.model.fines

import java.io.Serializable

data class GubddAccountListResponse(
    var code: Int,
    var gubdd_list: ArrayList<MyCar>,
    var comission_amount: Int,
    var msg: String
) : Serializable