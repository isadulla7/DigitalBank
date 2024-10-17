package uz.fido.network.domain.model.monitoring

import java.io.Serializable

data class AccountHistoriesRequest(
    val pageNumber: String? = null,
    val pageSize: String = "20",
    var type: String? = null,
    val account: String? = null,
    val codeFilial: String? = null,
    val dateClose: String? = null,
    val dateBegin: String? = null,
    val dateType: String? = null,
    val dateText: String? = null
) : Serializable