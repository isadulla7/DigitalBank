package uz.fido.network.domain.model.inn

import java.io.Serializable

data class InnInfoResponse(
    val address: String,
    val birthdate: String,
    val code: Int,
    val datemodify: String,
    val duty: String,
    val firstname: String,
    val isitd: String,
    val middlename: String,
    val msg: String,
    val ns10code: String,
    val ns10name: String,
    val ns11code: String,
    val ns11name: String,
    val ns13code: String,
    val ns13name: String,
    val passdate: String,
    val passnumber: String,
    val passorg: String,
    val passseries: String,
    val personalnum: String,
    val phone: String,
    val sex: String,
    val sexname: String,
    val surname: String,
    val tin: String,
    val tindate: String,
    val zipcode: String
) : Serializable