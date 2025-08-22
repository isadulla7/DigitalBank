package uz.fido.universaldigital.ui.fragments.monitoring.uzcard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import uz.fido.network.domain.model.monitoring.uzcard.UzcardMonitoringItem
import uz.fido.universaldigital.R
import uz.fido.utils.format.Format
import uz.fido.utils.view.custom_text_view.TextViewMedium
import uz.fido.utils.view.custom_text_view.TextViewRegular
import java.math.BigDecimal
import java.math.RoundingMode

const val DEBIT = "debit"
const val CREDIT = "credit"

fun UzcardMonitoringItem.getTranTypeSymbol(): String {
    return if (transactionType == CREDIT) "+" else "-"
}

fun TextViewMedium.setTextColor(context: Context, item: UzcardMonitoringItem) {
    setTextColor(ContextCompat.getColor(context, if (item.transactionType == CREDIT) R.color.monitoring_amount else R.color.mainTextColor))
}

@SuppressLint("SetTextI18n")
fun TextViewMedium.setMonitoringAmount(context: Context, item: UzcardMonitoringItem) {
    text = item.getTranTypeSymbol() + Format.formatAmount((item.transactionAmount.toBigDecimal().divide(BigDecimal(100),2, RoundingMode.HALF_UP)).toString()) + " " + context.getString(R.string.uzs)
}

fun ImageView.setMonitoringImage(item: UzcardMonitoringItem) {
    setImageResource(if (item.transactionType == CREDIT) R.drawable.ic_monitoring_plus else R.drawable.icon_monitoring)
}

fun TextViewRegular.setAdditionalInfo(context: Context, item: UzcardMonitoringItem) {
    text = if (item.cardNumber.length > 4) {
        "${context.getString(R.string.card)} •••• ${item.cardNumber.substring(item.cardNumber.length - 4, item.cardNumber.length)}"
    } else item.cardNumber
}

fun TextViewMedium.setCancelledInfo(context: Context, icon: ImageView, tvCancel: TextViewRegular, item: UzcardMonitoringItem) {
    if (item.cancelled == "true") {
        setTextColor(ContextCompat.getColor(context, R.color.mainTextColor))
        paintFlags = tvCancel.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        alpha = 0.5f
        tvCancel.text = context.getString(R.string.canselled)
        icon.setImageResource(R.drawable.icon_cansel_monitoring)
    } else {
        alpha = 1f
        paintFlags = 0
        tvCancel.text = ""
        icon.setImageResource(if (item.transactionType == CREDIT) R.drawable.ic_monitoring_plus else R.drawable.icon_monitoring)
    }
}