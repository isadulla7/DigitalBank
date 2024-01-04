package uz.fido.network.domain.model.monitoring.filter

class FilterLocalMonitoring(
    val start_date: String? = null,
    val end_date: String? = null,
    val page_number: Int,
    val page_item_size: Int,
    val service_ids: ArrayList<Int>? = null,
    val object_ids: ArrayList<Int>? = null,
    val to_object_value: ArrayList<String>? = null,
    val max_amount: String? = null,
    val min_amount: String? = null
)