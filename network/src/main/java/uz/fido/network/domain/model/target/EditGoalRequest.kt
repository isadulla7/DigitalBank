package uz.fido.network.domain.model.target

import java.io.Serializable

class EditGoalRequest(
    var target_id: String,
    var aim_desc: String,
    var duration: String,
    var target_amount: String,
    var set_duration: String,
    var set_duration_type: String,
    var image_name: String,
    var confirmed: String,
    var decreasing_amount: String,
    var state: String,
    var from_objects: ArrayList<String>,
) : Serializable