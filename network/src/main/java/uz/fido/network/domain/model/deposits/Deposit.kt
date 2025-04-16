package uz.fido.network.domain.model.deposits

import java.io.Serializable

data class Deposit(
    var type_dep: String = "",
    var max_sum: String = "",
    var replenishment_max: String = "",
    var partial_write: String = "",
    var keeping_time: String = "",
    var capitalization: String = "",
    var min_sum: Int = 0,
    var prolongation_sign: String = "",
    var dep_name: String = "",
    var allowed_shoot: String = "",
    var replenishment_min: Int = 0,
    var percent: String = "",
    var dep_id: Int = 0,
    var dep_type: Int = 0,
    var early_termination: String = "",
    var is_online: String = "",
    var created_on: String = "",
    var date_end: String = "",
    var currency_code: String = "",
    var description: String = "",
    var minimum_expense: String = "",
    var percent_child: String = "",
    var type_percent: String = "",
    var replenishment: String = "",
    var pay_to_card: String = "N",
    var pay_to_card_number: String = "",
    var group_id: Int = 0
) : Serializable

