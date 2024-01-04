package uz.fido.network.domain.model.target

import java.io.Serializable

class GoalModel(
    var amount: String,
    var create_date: String,
    var fixed_duration: Int,
    var target_id: String,
    var fund_object_value: String,
    var state: String,
    var state_name: String,
    var confirmed: String,
    var fund_object_type: String,
    var current_amount: String,
    var image_name: String,
    var aim_desc: String,
    var start_amount: String,
    var target_amount: String,
    var next_payment_day: String,
    var decreasing_amount: String,
    var set_duration_type: String,
    var set_duration: String,
    var from_objects: ArrayList<String>
) : Serializable