package uz.fido.universaldigital.ui.fragments.monitoring.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.universaldigital.R
import uz.fido.utils.format.Format
import uz.fido.utils.view.custom_text_view.TextViewMedium
import uz.fido.utils.view.custom_text_view.TextViewRegular

fun AccountHistory.isDebit() = debitAmount == "0"

fun TextViewMedium.setTransactionName(item: AccountHistory) {
    text = item.creditAccountName
}

fun TextViewRegular.setTransactionTime(item: AccountHistory) {
    text = if (item.dateExecute?.length == 19) item.dateExecute?.substring(10, 16) else "-"
}

fun TextViewMedium.setTextColor(context: Context, item: AccountHistory) {
    setTextColor(ContextCompat.getColor(context, if (item.isDebit()) R.color.monitoring_amount else R.color.mainTextColor))
}

fun AccountHistory.transactionTypeSymbol() = if (isDebit()) "+" else "-"

fun AccountHistory.currencyChar() = when (debitAccount?.substring(5, 8)) {
    "643" -> " RUB"
    "840" -> " USD"
    else -> " UZS"
}

@SuppressLint("SetTextI18n")
fun TextViewMedium.setTransactionAmount(item: AccountHistory) {
    text = item.transactionTypeSymbol() + Format.formatAmount(item.creditAmount.toString()) + item.currencyChar()
}

fun ImageView.setMonitoringImage(item: AccountHistory) {
    setImageResource(if (item.isDebit()) R.drawable.ic_monitoring_plus else R.drawable.icon_monitoring)
}

fun AccountHistory.getTransactionAmount(): String {
    return if (debitAmount == "0") {
        "${Format.formatAmount(creditAmount.toString())} ${currencyChar()}"
    } else {
        "${Format.formatAmount(debitAmount)} ${currencyChar()}"
    }
}