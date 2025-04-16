package uz.fido.network.domain.model.monitoring.filter

import java.io.Serializable

class FilterCard(
    val object_id: Int,
    val object_name: String,
    val object_type: String,
    val object_value: String="",
    val state: String,
    var is_selected_monitoring:Boolean=false,
    var type:Int=0,
): Serializable