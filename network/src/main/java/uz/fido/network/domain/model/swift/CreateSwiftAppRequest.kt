package uz.fido.network.domain.model.swift

import java.io.Serializable

data class CreateSwiftAppRequest(
    var request_code: String,
    var language: String,
    var device_type: String,
    var command: String,
    var amount: String,
    var from_object_id: String,
    var service_id: String,
    var client_id: String,
    var currency_code: String,
    var account_56a: String,
    var bicorbei_56a: String,
    var bicorbei_57a: String,
    var account_59a: String,
    var account_59: String,
    var nameandaddress_59: ArrayList<String>? = null,
    val nameandaddress_50k: ArrayList<String>? = null,
    val narrative_70: ArrayList<String>? = null,
    var bicorbei_59a: String,
    var code_71a: String
) : Serializable

data class GetSwiftCommissionRequest(
    var currency_code: String,
    var from_object_currency: String
) : Serializable
