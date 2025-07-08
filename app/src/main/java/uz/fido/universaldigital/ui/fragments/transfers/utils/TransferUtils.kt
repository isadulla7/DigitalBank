package uz.fido.universaldigital.ui.fragments.transfers.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.p2p.P2PInfoDto
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.fragments.transfers.transfer_history.P2PHistoryAdapter
import uz.fido.universaldigital.ui.fragments.transfers.transfer_history.PopularTransfersAdapter
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isNotActive
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.format.Format
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.utility.adapter.showSkeleton
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

fun getInfoCommand(receiverCard: CardResponse): String {
    return if (receiverCard.object_value.startsWith("AUZ") || receiverCard.object_value.startsWith(
            "DV"
        )
    ) "info&purse" else "info&card"
}

fun getInfoCommand(receiverCard: String): String {
    return if (receiverCard.startsWith("AUZ") || receiverCard.startsWith(
            "DV"
        )
    ) "info&purse" else "info&card"
}

fun setCommand(senderCardType: String, receiverCardType: String, senderCardDv: String? = "", receiverCardDv: String? = ""): String {
    val isSenderDeposit = senderCardDv == "Y"
    return if (isSenderDeposit) {
        if (receiverCardType == WALLET) {
            "purse&purse"
        } else "deposit&card"
    } else {
        when {
            senderCardType == WALLET && receiverCardType == WALLET -> "purse&purse"
            senderCardType == WALLET && receiverCardType != WALLET -> "purse&card"
            senderCardType != WALLET && receiverCardType == WALLET -> "card&purse"
            else -> "card&card"
        }
    }
}

fun getServiceIdInfo(receiverCard: CardResponse, senderCard: CardResponse): String {
    return if (receiverCard.object_value.startsWith("AUZ") || receiverCard.object_value.startsWith("DV") || senderCard.object_type == WALLET) "-12" else "-1"
}

fun getServiceIdInfo(receiverCard: String, senderCard: String): String {
    return if (receiverCard.startsWith("AUZ") || receiverCard.startsWith("DV") || receiverCard == WALLET || senderCard == WALLET) "-12" else "-1"
}

fun String.capitalizeWord(): String =
    if (this.length > 1) this[0].uppercaseChar().toString() + this.substring(1)
        .lowercase(Locale.getDefault()) else ""

fun checkCardNumber(cardNumber: String): Boolean {
    return if (cardNumber.length > 3) {
        cardNumber.startsWith("8600") || cardNumber.startsWith("6262") || cardNumber.startsWith("9860") || cardNumber.startsWith(
            "4685"
        ) || cardNumber.startsWith(
            "5116"
        ) || cardNumber.startsWith("4685") || cardNumber.startsWith("5130") || cardNumber.startsWith(
            "4685"
        ) || cardNumber.startsWith("6216") || cardNumber.startsWith(
            "4787"
        ) || cardNumber.startsWith(
            "5614"
        )
    } else true
}

fun checkCardAvailability(ccNumber: String): Boolean {
    if (!isOnlyNumbers(ccNumber)) {
        return false
    }
    var sum = 0
    var alternate = false
    for (i in ccNumber.length - 1 downTo 0) {
        var n = ccNumber.substring(i, i + 1).toInt()
        if (alternate) {
            n *= 2
            if (n > 9) {
                n = n % 10 + 1
            }
        }
        sum += n
        alternate = !alternate
    }
    return sum % 10 == 0
}

fun isOnlyNumbers(input: String): Boolean {
    val regex = Regex("^\\d+$")
    return regex.matches(input)
}

fun getUserNameFormatted(embossedName: String?): String {
    embossedName?.let { fullName ->
        try {
            return if (fullName.contains(" ")) {
                fullName.split(" ")[0].capitalizeWord() + " " + fullName.split(" ")[1].capitalizeWord()
            } else fullName.capitalizeWord()
        } catch (e: Exception) {
            return fullName
        }
    }
    return "No name"
}

fun getShortNameFormatted(embossedName: String?): String {
    embossedName?.let { fullName ->
        return if (fullName.contains(" ")) {
            val name = fullName.split(" ")[0]
            val surname = fullName.split(" ")[1]
            if (name.isNotEmpty() && surname.isNotEmpty()) {
                name[0].toString() + surname[0]
            } else fullName.subSequence(0, 1).toString()
        } else fullName.subSequence(0, 1).toString()
    }
    return "##"
}

fun showTransferSkeleton(adapter: PopularTransfersAdapter, rv: RecyclerView): SkeletonScreen {
    return showSkeleton(
        rv, adapter, R.layout.shimmer_item_history, 5
    )
}

fun showTransferSkeleton(adapter: P2PHistoryAdapter, rv: RecyclerView): SkeletonScreen {
    return showSkeleton(
        rv, adapter, R.layout.shimmer_item_history, 5
    )
}

@SuppressLint("SetTextI18n")
fun TextView.setMinMaxAmount(
    selectedCard: CardResponse?,
    receiverCard: String?,
    currentAmount: EditText,
    p2PInfoDto: P2PInfoDto?,
    context: Context
): Boolean {
    val etAmount = currentAmount.text.toString().replace(" ", "").ifEmpty { "0" }
    val formattedAmount = etAmount.toBigDecimal()
    if (p2PInfoDto != null && selectedCard != null && receiverCard != null) {
        val minAmount = p2PInfoDto.minAmount?.toBigDecimal()?.divide(100.toBigDecimal()) ?: 1000.0.toBigDecimal()
        val maxAmount = p2PInfoDto.maxAmount?.toBigDecimal()?.divide(100.toBigDecimal()) ?: 15000000.0.toBigDecimal()
        val percent = p2PInfoDto.percent?.toDouble()?.toBigDecimal() ?: 0.0.toBigDecimal()
        val totalAmount = formattedAmount + (formattedAmount.divide(100.toBigDecimal())) * percent

        when {
            !p2PInfoDto.isSuccess -> {
                setTextColor(ContextCompat.getColor(context, R.color.brandRedColor))
                text = p2PInfoDto.errorMessage
                return false
            }

            formattedAmount < minAmount -> {
                setTextColor(ContextCompat.getColor(context, R.color.brandBlueColor_50))
                text = context.getString(R.string.min_amount) + " " +
                        Format.formatAmount(minAmount.toString()) + " " +
                        context.getString(R.string.sum_text)
                return false
            }

            formattedAmount > maxAmount -> {
                setTextColor(ContextCompat.getColor(context, R.color.brandRedColor))
                text = context.getString(R.string.max_amount) + " " +
                        Format.formatAmount((maxAmount).toString()) + " " +
                        context.getString(R.string.sum_text)
                return false
            }

            selectedCard.object_value == receiverCard -> {
                setTextColor(ContextCompat.getColor(context, R.color.brandRedColor))
                text = context.getString(R.string.sender_and_receiver_the_same)
                return false
            }

            totalAmount > selectedCard.balance.toBigDecimal().divide(100.toBigDecimal()) -> {
                setTextColor(ContextCompat.getColor(context, R.color.brandRedColor))
                text = context.getString(R.string.insufficient_amount)
                return false
            }

            selectedCard.isNotActive() -> {
                setTextColor(ContextCompat.getColor(context, R.color.brandRedColor))
                return false
            }

            formattedAmount == BigDecimal(0) -> {
                setTextColor(ContextCompat.getColor(context, R.color.brandRedColor))
                return false
            }

            else -> {
                setTextColor(ContextCompat.getColor(context, R.color.brandBlueColor_50))
                text =
                    context.getString(R.string.commission) + " " + percent.toString() + "% (" + Format.formatAmount(Format.convertFromTiynDivide((formattedAmount * percent).toString())) + " " + context.getString(
                        R.string.sum_text
                    ) + ") "
                return true
            }
        }
    } else return false
}

fun Context.formatErrorMessage(message: String? = null): String {
    if (message == "CARD_EXPIRED") return getString(R.string.card_expired)
    return getString(R.string.card_not_found)
}

@SuppressLint("SetTextI18n")
fun TextView.setTransactionPercent(amount: BigDecimal, percent: BigDecimal) {
    val sum = BigDecimal(100)
    val calculatedPercent = Format.formatAmount(amount.multiply(percent).divide(sum, 2, RoundingMode.HALF_UP).toString())
    text = context.getString(R.string.commission_with_dots) + " " + percent + "% (" + calculatedPercent + " UZS )"
}