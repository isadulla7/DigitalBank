package uz.fido.network.domain.model.monitoring

import java.io.Serializable

data class AccountHistory(
    var account: String,
    var debit: String,
    var NumberTrans: String,
    var lnType: String,
    var purpose: String? = null,
    var date: String? = null,
    var dateExecute: String? = null,
    val nameAcc: String? = null,
    val type: String? = null,
    val codeFliall: String? = null,
    val coAcc: String,
    val dtAccName: String,
    val coAccName: String,
    val coMfo: String,
    val dtMfo: String,
    val credit: String? = null,
    val dtAcc: String? = null
) : Serializable