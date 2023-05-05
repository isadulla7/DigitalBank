package uz.fido.network.domain.model.target

import java.io.Serializable

class SetTargetRequest(
    var depType: String,
    var depId: String,
    var fund_object_value: String,
    var fund_object_type: String,
    var aim_desc: String,
    var duration: String,
    var target_amount: String,
    var amount: String,
    var set_duration: String,
    var set_duration_type: String,
    var confirmed: String,
    var decreasing_amount: String,
    var start_amount: String,
    var from_objects: ArrayList<String>,
    var image_name:String
) : Serializable