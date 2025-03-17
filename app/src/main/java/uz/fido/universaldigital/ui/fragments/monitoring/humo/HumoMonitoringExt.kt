package uz.fido.universaldigital.ui.fragments.monitoring.humo

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import uz.fido.network.domain.model.monitoring.humo.HumoMonitoringItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.fragments.monitoring.uzcard.CREDIT
import uz.fido.utils.format.Format
import uz.fido.utils.view.custom_text_view.TextViewMedium
import uz.fido.utils.view.custom_text_view.TextViewRegular

fun TextViewMedium.setTransactionName(context: Context, humoMonitoringItem: HumoMonitoringItem) {
    text = humoMonitoringItem.merchantName.ifEmpty { context.getString(R.string.humo_operation) }
}

fun TextViewRegular.setTransactionTime(humoMonitoringItem: HumoMonitoringItem) {
    text = if (humoMonitoringItem.transactionDate.length == 19) humoMonitoringItem.transactionDate.substring(10, 16) else "-"
}

fun TextViewRegular.setFormattedCardNumber(context: Context, humoMonitoringItem: HumoMonitoringItem) {
    text = if (humoMonitoringItem.cardNumber.length == 16) Format.formatCardNumberAllMonitoring(context, humoMonitoringItem.cardNumber) else humoMonitoringItem.cardNumber
}

fun HumoMonitoringItem.getTranTypeSymbol(): String {
    return if (transactionType == CREDIT) "+" else "-"
}

@SuppressLint("SetTextI18n")
fun TextViewMedium.setTransactionAmount(context: Context, humoMonitoringItem: HumoMonitoringItem) {
    text = humoMonitoringItem.getTranTypeSymbol() + Format.formatAmount((humoMonitoringItem.transactionAmount.toDouble() / 100).toString()) + " " + context.getString(R.string.uzs)
}

fun TextViewMedium.setTextColor(context: Context, humoMonitoringItem: HumoMonitoringItem) {
    setTextColor(ContextCompat.getColor(context, if (humoMonitoringItem.transactionType == CREDIT) R.color.monitoring_amount else R.color.mainTextColor))
}

fun ImageView.setMonitoringImage(humoMonitoringItem: HumoMonitoringItem) {
    setImageResource(if (humoMonitoringItem.transactionType == CREDIT) R.drawable.ic_monitoring_plus else R.drawable.icon_monitoring)
}