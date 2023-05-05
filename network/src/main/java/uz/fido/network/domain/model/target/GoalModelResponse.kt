package uz.fido.network.domain.model.target

import java.io.Serializable

class GoalModelResponse(
    var set_duration: Int,
    var confirmed: String,
    var create_date: String,
    var from_objects: ArrayList<String>,
    var fixed_duration: Int,
    var target_id: String,
    var fund_object_value: String,
    var state: String,
    var fund_object_type: String,
    var next_payment_day: String,
    var request_id: String,
    var current_amount: String,
    var code: Int,
    var decreasing_amount: String,
    var set_duration_type: String,
    var image_name: String,
    var aim_desc: String,
    var msg: String,
    var start_amount: String,
    var target_amount: String,
) : Serializable