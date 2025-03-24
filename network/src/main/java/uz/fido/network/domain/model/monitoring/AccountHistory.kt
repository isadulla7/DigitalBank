package uz.fido.network.domain.model.monitoring

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccountHistory(
    @SerializedName("account") var account: String,
    @SerializedName("debit") var debitAmount: String,
    @SerializedName("NumberTrans") var transactionNumber: String,
    @SerializedName("lnType") var lnType: String,
    @SerializedName("purpose") var purpose: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("dateExecute") var dateExecute: String? = null,
    @SerializedName("nameAcc") val accountName: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("coAcc") val creditAccount: String,
    @SerializedName("dtAccName") val debitAccountName: String,
    @SerializedName("coAccName") val creditAccountName: String,
    @SerializedName("coMfo") val creditMfo: String,
    @SerializedName("dtMfo") val debitMfo: String,
    @SerializedName("credit") val creditAmount: String? = null,
    @SerializedName("dtAcc") val debitAccount: String? = null,
    @SerializedName("account_type") var accountType: Int = 0
) : Serializable