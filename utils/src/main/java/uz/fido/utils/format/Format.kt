package uz.fido.utils.format

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import uz.fido.utils.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.StringTokenizer
import kotlin.math.pow
import kotlin.math.roundToInt

object Format {

    fun newDateFormat(date: String): String {
        return date.substring(6, 10) + "-" + date.substring(3, 5) + "-" + date.substring(0, 2)
    }

    fun firstLetterUpperCase(text: String): String {
        return if (text.isNotEmpty()) text[0].uppercaseChar().toString() + text.substring(
            0,
            text.length
        ) else ""
    }

    fun formatCardNumber(cardNumber: String): String {
        return if (cardNumber.length == 16) {
            cardNumber.substring(0, 4) + " " + cardNumber.substring(
                4,
                6
            ) + " •• •••• " + cardNumber.substring(12, cardNumber.length)
        } else {
            cardNumber
        }
    }

    fun formatCardNumberForCheque(cardNumber: String): String {
        return if (cardNumber.length == 16) {
            cardNumber.substring(0, 4) + " " + cardNumber.substring(
                4,
                6
            ) + "** **** " + cardNumber.substring(12, cardNumber.length)
        } else {
            cardNumber
        }
    }

    fun formatWalletNumber(cardNumber: String): String {
        return if (cardNumber.length == 11) {
            cardNumber.substring(0, 3) + " •• " + cardNumber.substring(7, cardNumber.length)
        } else {
            cardNumber
        }
    }

    fun formatCardNumberMonitoring(context: Context, cardNumber: String): String {
        return if (cardNumber.length == 16) {
            context.getString(R.string.no_card) + " •• " + cardNumber.substring(
                12,
                cardNumber.length
            )
        } else {
            cardNumber
        }
    }

    fun formatCardNumberAllMonitoring(context: Context, cardNumber: String): String {
        return if (cardNumber.length == 16) {
            context.getString(R.string.card) + " •• " + cardNumber.substring(12, cardNumber.length)
        } else {
            cardNumber
        }
    }

    fun formatCardNumberObjectMonitoring(context: Context, cardNumber: String): String {
        return if (cardNumber.length == 16) {
            context.getString(R.string.from_card) + " •• " + cardNumber.substring(
                12,
                cardNumber.length
            )
        } else {
            cardNumber
        }
    }

    fun formatAmount(str: String?): String {
        if (str == null) {
            return ""
        }
        if (str.isEmpty()) {
            return "0"
        }
        val rounded =
            str.replace(" ", "").replace(",", ".").toBigDecimal().setScale(2, RoundingMode.UP)
                .toString()
        var value = rounded
        if (value.contains(",")) {
            value = value.replace(",", ".")
        }
        val lst = StringTokenizer(value, ".")
        var str1: String = value
        var str2 = ""
        if (lst.countTokens() > 1) {
            str1 = lst.nextToken()
            str2 = lst.nextToken()
        }
        var str3 = java.lang.StringBuilder()
        var i = 0
        var j = -1 + str1.length
        if (str1[-1 + str1.length] == '.') {
            j--
            str3 = java.lang.StringBuilder(".")
        }
        var k = j
        while (true) {
            if (k < 0) {
                if (str2.isNotEmpty()) str3.append(".").append(str2)
                return formatWithoutDot2(str3.toString())
            }
            if (i == 3) {
                str3.insert(0, " ")
                i = 0
            }
            str3.insert(0, str1[k])
            i++
            k--
        }
    }

    private fun formatWithoutDot2(amount: String): String {
        try {
            return if (amount.substring(amount.length - 3, amount.length) == ".00")
                amount.replace(".00", "")
            else amount
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun BigDecimal.formatDecimalSeparator(): String {
        return toString()
            .reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()
    }

    var locale: Locale? = null
        get() {
            field = Locale("en", "UK")
            return field
        }

    private var formatSymbols: DecimalFormatSymbols? = null
        get() {
            field = DecimalFormatSymbols(locale)
            field?.decimalSeparator = '.'
            field?.groupingSeparator = ' '
            return field
        }

    private var pattern = "#,###.##"

    private var decimalFormat: DecimalFormat? = null
        get() {
            field = DecimalFormat(pattern, formatSymbols)
            return field
        }

    fun formatMoney(input: String): String? {
        var res: String? = null
        try {
            res = decimalFormat?.format(BigDecimal(input.toDouble().toString()))
        } catch (ex: NumberFormatException) {
            res = "0"
            return res
        }
        return res
    }

    fun convertFromTiynDivide(str: String): String {
        if (str.isEmpty()) {
            return str
        }
        val amount = str.replace(" ", "").replace(",", ".").toBigDecimal().divide(BigDecimal(100))
        return amount.toString()
    }

    fun currencyCode(code: String): String {
        return when (code) {
            "000" -> "UZS"
            "840" -> "$"
            "643" -> "RUB"
            "392" -> "JPY"
            "756" -> "CHF"
            "826" -> "GBP"
            "978" -> "EUR"
            else -> ""
        }
    }

    fun monitoringDate(date: String): String {
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val parsedDate: Date = df.parse(date) as Date
        val calendar = Calendar.getInstance().apply { time = parsedDate }
        val formatPattern = if (calendar.get(Calendar.YEAR) == currentYear) {
            "dd MMMM, EEEE"
        } else {
            "dd MMMM, yyyy"
        }
        return SimpleDateFormat(formatPattern, Locale.getDefault()).format(parsedDate)
    }

    fun formatAmountToTiyn(amount: String?): String {
        if (amount.isNullOrBlank()) {
            return "0"
        }
        val bigD = BigDecimal(amount.replace(" ", "").replace(",", "."))
        val newAmount = bigD.multiply(BigDecimal(100))
        var newAmountStr = newAmount.toString().replace(",", ".")
        if (newAmountStr.contains(".")) {
            newAmountStr = newAmountStr.substring(0, newAmountStr.indexOf("."))
        }
        return newAmountStr
    }

    fun formatAmountFromTiynToInteger(amount: String): String {
        var newAmount = amount.replace(" ", "").replace(",", ".")
        if (newAmount.startsWith("0")) {
            return newAmount
        }
        newAmount = if (newAmount.contains(".")) {
            newAmount.substring(0, newAmount.indexOf("."))
        } else {
            if (newAmount.length > 2)
                newAmount.substring(0, amount.length - 2)
            else newAmount
        }
        return newAmount
    }

    fun View.takeScreenShot(activity: Activity, callback: (Bitmap?) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.window?.let { window ->
                val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
                val locationOfViewInWindow = IntArray(2)
                this.getLocationInWindow(locationOfViewInWindow)
                try {
                    PixelCopy.request(
                        window, Rect(
                            locationOfViewInWindow[0],
                            locationOfViewInWindow[1],
                            locationOfViewInWindow[0] + this.width,
                            locationOfViewInWindow[1] + this.height
                        ), bitmap, { copyResult ->
                            if (copyResult == PixelCopy.SUCCESS) {
                                callback(bitmap)
                            }
                        }, Handler(Looper.myLooper()!!)
                    )
                } catch (e: IllegalArgumentException) {
                    // PixelCopy may throw IllegalArgumentException, make sure to handle it
                    e.printStackTrace()
                    callback(null)
                }
            }
        } else {
            this.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(this.drawingCache)
            this.isDrawingCacheEnabled = false
            callback(bitmap)
        }
    }

    fun setDepositOperationTitle(
        debit: String,
        context: Context,
        dtAcc: String,
        coAcc: String
    ): String {
        return if (debit == "0") {
            when (dtAcc.take(5)) {
                "22403", "22405" -> context.getString(R.string.accured_interest_deposit)
                "22406", "22606" -> context.getString(R.string.re_registration_deposit)
                "16377", "19997", "23206", "29816", "45249", "45253", "45294" -> context.getString(R.string.correctional_operations)
                "51106", "50106", "50606" -> context.getString(R.string.accural_of_interest)
                else -> context.getString(R.string.deposit_replenishment)
            }
        } else {
            when (coAcc.take(5)) {
                "45233", "45249", "45253", "45294" -> context.getString(R.string.bank_service)
                "29842" -> context.getString(R.string.transferrred_to_inactivity)
                "22403", "22405" -> context.getString(R.string.excees_interests_returned)
                "20406", "20606" -> if (dtAcc.take(5) == "22403" || dtAcc.take(5) == "22405") {
                    context.getString(R.string.capitalization_of_interest)
                } else {
                    context.getString(R.string.re_registration_deposit)
                }

                "19934", "23206", "29896" -> context.getString(R.string.correctional_operations)
                "10101", "23504" -> context.getString(R.string.interest_withdrawal)
                "51106", "50106", "50606" -> context.getString(R.string.re_calculate_percents)
                else -> context.getString(R.string.deposit_withdrawal)
            }
        }
    }

    fun formatDepositDate(date: String, firstFormatStr: String, secondFormatStr: String): String {
        val df = SimpleDateFormat(firstFormatStr, Locale.getDefault())
        val secondFormat = SimpleDateFormat(secondFormatStr, Locale.getDefault())
        val newdate = df.parse(date)
        return secondFormat.format(newdate.time)

    }

    fun naiveRound(num: Float, decimalPlaces: Int): Double {
        val p = 10.0.pow(decimalPlaces.toDouble())
        return (num * p).roundToInt() / p
    }
}