package uz.fido.universaldigital.ui.fragments.monitoring.local

import android.annotation.SuppressLint
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.monitoring.filter.FilterSaveVh
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.format.Format
import uz.fido.utils.view.custom_text_view.TextViewMedium
import uz.fido.utils.view.custom_text_view.TextViewRegular
import java.math.BigDecimal
import java.math.RoundingMode

fun LocalMonitoring.isCredit() = transactionType == "credit"

fun Fragment.getOperationType(filterSaveVh: FilterSaveVh): Int {
    return when (filterSaveVh.operationType) {
        getString(R.string.enrollments) -> 0
        getString(R.string.write_offs) -> 1
        else -> 2
    }
}

fun FilterSaveVh.getSelectedCards(): ArrayList<Int> {
    val selectedObjectIds = arrayListOf<Int>()
    val selectedCards = cardList.filter { it.is_selected_monitoring }
    if (selectedCards.isNotEmpty()) {
        cardList.forEach { if (!it.is_selected_monitoring) selectedObjectIds.add(it.object_id) }
    } else {
        cardList.forEach { selectedObjectIds.add(it.object_id) }
    }
    return selectedObjectIds
}

fun FilterSaveVh.getServiceIdsAndPartnerObj(): Pair<ArrayList<Int>, ArrayList<String>> {
    val selectedServiceIds = arrayListOf<Int>()
    val selectedPartnerObj = arrayListOf<String>()
    val serviceIdCheck = serviceList.filter { it.service_current }
    if (serviceIdCheck.isEmpty()) {
        serviceList.forEach { selectedServiceIds.add(it.service_id ?: 0) }
    } else {
        serviceList.forEach { userPayedService ->
            if (userPayedService.service_current) {
                selectedServiceIds.add(userPayedService.service_id ?: 0)
                userPayedService.list.forEach {
                    selectedPartnerObj.add(it.partnerObj)
                }
            }
        }
    }
    return Pair(selectedServiceIds, selectedPartnerObj)
}

fun FilterSaveVh.getMaxAmount(): String? {
    return if (maxAmount.isNotEmpty()) "${
        maxAmount.replace(
            " ",
            ""
        )
    }00" else null
}

fun FilterSaveVh.getMinMinAmount(): String? {
    return if (minAmount.isNotEmpty()) "${
        minAmount.replace(
            " ",
            ""
        )
    }00" else null
}

fun TextViewMedium.setTransactionName(localMonitoring: LocalMonitoring) {
    text = localMonitoring.name.ifEmpty { context.getString(R.string.no_name) }
}

fun TextViewRegular.setTransactionTime(localMonitoring: LocalMonitoring) {
    text = if (localMonitoring.createdDate.length == 19) localMonitoring.createdDate.substring(10, 16) else localMonitoring.createdDate
}

fun ImageView.setTransactionImage(localMonitoring: LocalMonitoring) {
    if (localMonitoring.iconName.isNotEmpty()) {
        Picasso.get().load(Keys.paynetPhotoUrl() + localMonitoring.iconName).error(R.drawable.icon_monitoring).into(this)
    } else setImageResource(R.drawable.icon_monitoring)
}

fun TextViewMedium.setTransactionAmountColor(localMonitoring: LocalMonitoring) {
    if (localMonitoring.isCredit()) {
        setTextColor(
            ContextCompat.getColor(
                context, R.color.monitoring_amount
            )
        )
    } else {
        setTextColor(
            ContextCompat.getColor(
                context, R.color.mainTextColor
            )
        )
    }
}

fun LocalMonitoring.getTranTypeSymbol() = if (isCredit()) "+" else "-"

@SuppressLint("SetTextI18n")
fun TextViewMedium.setTransactionAmount(localMonitoring: LocalMonitoring) {
    val sum = BigDecimal(100)

    text = localMonitoring.getTranTypeSymbol() + Format.formatAmount(
        localMonitoring.amount.toBigDecimal().divide(sum, 2, RoundingMode.HALF_UP).toString()
    ) + " " + Format.currencyCode(localMonitoring.currencyCode)
}

fun TextViewRegular.setAdditionalInfo(localMonitoring: LocalMonitoring) {
    text = when {
        localMonitoring.serviceId != "-1" -> context.getText(R.string.payment)
        localMonitoring.transactionType == "credit" -> formatCard(localMonitoring.partnerObj, true)
        else -> formatCard(localMonitoring.senderCard.ifEmpty { localMonitoring.partnerObj }, false)
    }
}

private fun TextViewRegular.formatCard(cardNumber: String, isCredit: Boolean): String {
    return if (cardNumber.length == 16) {
        if (isCredit) Format.formatCardNumberMonitoring(context, cardNumber)
        else Format.formatCardNumberObjectMonitoring(context, cardNumber)
    } else {
        cardNumber
    }
}