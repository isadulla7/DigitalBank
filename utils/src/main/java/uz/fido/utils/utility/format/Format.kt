package uz.fido.utils.utility.format

import android.annotation.SuppressLint
import android.content.Context
import android.text.*
import uz.fido.utils.R
import uz.fido.utils.const.CurrencyConst.CURRENCY_CHAR_EUR
import uz.fido.utils.const.CurrencyConst.CURRENCY_CHAR_RUB
import uz.fido.utils.const.CurrencyConst.CURRENCY_CHAR_USD
import uz.fido.utils.const.CurrencyConst.CURRENCY_CHAR_UZS
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_EUR
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_RUB
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_USD
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_UZS
import uz.fido.utils.const.CurrencyConst.DOLLAR_SIGN
import uz.fido.utils.log.Logger
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

class Format {

    companion object {

        fun sendFormat(amount: String): String {
            val formattedAmount = amount.replace(" ", "").replace(",", ".")
            return formattedAmount.toBigDecimal().multiply(100.toBigDecimal()).toString()
                .replace(",", ".")
        }

        fun naiveRound(num: Float, decimalPlaces: Int): Double {
            val p = 10.0.pow(decimalPlaces.toDouble());
            return (num * p).roundToInt() / p
        }

        fun formatChatDate(date: String): String {
            val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
            val secondFormat = SimpleDateFormat("HH:mm", Locale.US)
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                try {
                    secondFormat.format(df.parse(date)).toString()
                } catch (e: ParseException) {
                    date
                }
            } else {
                date
            }
        }

        fun formatChatDateToDay(date: String): String {
            val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
            val secondFormat = SimpleDateFormat("dd MMMM, EEEE", Locale.getDefault())
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                try {
                    secondFormat.format(df.parse(date)).toString()
                } catch (e: ParseException) {
                    date
                }
            } else {
                date
            }
        }

        fun phoneNumberFormat(string: String): String {
            var str = string
            val characters =
                arrayOf(" ", "#", "$", "*", "&", "%", "+", "-", "(", ")", "!", "^", ":", "_")
            for (character in characters) {
                if (str.contains(character)) {
                    str = str.replace(character, "")
                }
            }
            str = str.replace("[^\\x00-\\x7F]".toRegex(), "")
            if (str.contains("998")) str = str.substring(str.indexOf("998"))
            if (!str.contains("998")) {
                str = "998$str"
            }
            str = "+$str"
            return str
        }

        fun toPhoneFormat(phoneNumber: String): String {
            return if (phoneNumber.length == 12) {
                "+" + phoneNumber.substring(0, 3) + " " + phoneNumber.substring(
                    3, 5
                ) + " " + phoneNumber.substring(
                    5, 8
                ) + " " + phoneNumber.substring(
                    8, 10
                ) + " " + phoneNumber.substring(10, 12)
            } else phoneNumber
        }

        fun toPhoneFormatUZ(phoneNumber: String): String {
            return if (phoneNumber.length == 9) {
                "+998 " + phoneNumber.substring(0, 2) + " " + phoneNumber.substring(
                    2, 5
                ) + " " + phoneNumber.substring(
                    5, 7
                ) + " " + phoneNumber.substring(7, 9)
            } else phoneNumber
        }

        fun convertFromTiynDivide(str: String): String {
            if (str.isEmpty()) {
                return str
            }
            val amount =
                str.replace(" ", "").replace(",", ".").toBigDecimal().divide(BigDecimal(100))
            return amount.toString()
        }

        fun convertFromStringToBigDecimal(str: String): BigDecimal {
            if (str.isEmpty()) {
                return BigDecimal.ZERO
            }
            val amount = str.replace(" ", "").replace(",", ".")
            return amount.toBigDecimal()
        }

        fun conversionFormat(amount: Double): String {
            val plainText = amount.toBigDecimal().toPlainString().toDouble()
            val secondForm = String.format("%.2f", plainText)
            return formatAmount(secondForm)
        }

        fun formatAmountWithAppend(
            amount: String? = "0",
            currency: String? = "",
            context: Context,
            amountColor: Int? = null,
            textSize: Int? = null
        ): SpannableStringBuilder {
            val builder = SpannableStringBuilder()
            val filteredAmount = amount?.replace(" ", "")?.replace(",", ".")
            val newAmount =
                String.format("%.2f", filteredAmount?.toDouble()!! / 100).replace(",", ".")
            val roundedAmount = formatAmountRounded(
                newAmount.substring(0, newAmount.lastIndexOf(".")).replace(" ", "")
            )
            val word: Spannable = SpannableString(roundedAmount)
            builder.append(word)
            val amountWithCurrency =
                "${newAmount.subSequence(newAmount.indexOf("."), newAmount.length)} $currency"
            val wordTwo: Spannable = SpannableString(amountWithCurrency)
            builder.append(wordTwo)
            return builder
        }

        private fun formatAmountRounded(str: String?): String {
            if (str == null) {
                return ""
            }
            var value = str
            if (value != null) {
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
                        return str3.toString()
                    }
                    if (i == 3) {
                        str3.insert(0, " ")
                        i = 0
                    }
                    str3.insert(0, str1[k])
                    i++
                    k--
                }
            } else {
                return ""
            }
        }

        fun formatAmountToInteger(amount: String): String {
            return if (amount.isNotEmpty()) {
                val newAmount: String = amount.substring(0, amount.length - 2)
                amountWithSpacesInteger(newAmount)
            } else ""
        }

        fun formatAmountFromTiynToInteger(amount: String): String {
            var newAmount = amount.replace(" ", "").replace(",", ".")
            if (newAmount.startsWith("0")) {
                return newAmount
            }
            newAmount = if (newAmount.contains(".")) {
                newAmount.substring(0, newAmount.indexOf("."))
            } else {
                if (newAmount.length > 2) newAmount.substring(0, amount.length - 2)
                else newAmount
            }
            return newAmount
        }

        fun formatTiynToBigDecimal(amount: String?): BigDecimal {
            return if (amount != null && amount.isNotEmpty()) {
                val a = amount.replace(",", ".").replace(" ", "")
                a.toBigDecimal().divide(BigDecimal(100))
            } else {
                BigDecimal.ZERO
            }
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
            Logger.writeErrorLog(newAmountStr)
            return newAmountStr
        }

        private fun amountWithSpacesInteger(str: String): String {
            var value = str
            if (value != null) {
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
                var str3 = StringBuilder()
                var i = 0
                var j = -1 + str1.length
                if (str1[-1 + str1.length] == '.') {
                    j--
                    str3 = StringBuilder(".")
                }
                var k = j
                while (true) {
                    if (k < 0) {
                        if (str2.isNotEmpty()) str3.append(".").append(str2)
                        return str3.toString()
                    }
                    if (i == 3) {
                        str3.insert(0, " ")
                        i = 0
                    }
                    str3.insert(0, str1[k])
                    i++
                    k--
                }
            } else {
                return ""
            }
        }

        fun checkForPassword(password: String): Boolean {
            if (password.isNotEmpty()) {
                return password.length in 8..25
            }
            return false
        }

        fun checkForPhoneNumber(phoneNumber: String): Boolean {
            val phone = phoneNumber.replace(" ", "")
            val prefixList = arrayOf(
                "99897",
                "99888",
                "99894",
                "99893",
                "99890",
                "99891",
                "99895",
                "99898",
                "99899",
                "99833",
                "79903",
                "79905",
                "79906",
                "79909",
                "79687"
            )
            var noError = false
            if (phone.length != 12) {
                return false
            }
            prefixList.forEach {
                if (phone.startsWith(it)) {
                    noError = true
                }
            }
            return noError
        }

        fun checkForMinMaxAmount(minAmount: String, maxAmount: String, amount: String): Boolean {
            val minAmountInt = if (minAmount.contains(".") || minAmount.contains(",")) {
                var minAmountStr = minAmount.replace(",", ".").replace(" ", "")
                minAmountStr = minAmountStr.substring(0, minAmount.indexOf("."))
                minAmountStr.toLong()
            } else {
                minAmount.replace(" ", "").toLong()
            }
            val maxAmountInt = if (maxAmount.contains(".") || maxAmount.contains(",")) {
                var maxAmountStr = maxAmount.replace(",", ".").replace(" ", "")
                maxAmountStr = maxAmountStr.substring(0, maxAmount.indexOf("."))
                maxAmountStr.toLong()
            } else {
                maxAmount.replace(" ", "").toLong()
            }
            val amountInt = if (amount.contains(".") || amount.contains(",")) {
                var amountStr = amount.replace(",", ".").replace(" ", "")
                amountStr = amountStr.substring(0, amount.indexOf("."))
                amountStr.toLong()
            } else {
                amount.replace(" ", "").toLong()
            }
            if (amountInt < minAmountInt) {
                return false
            }
            if (maxAmountInt == 0L) {
                return true
            }
            return amountInt <= maxAmountInt
        }

        fun noSpace(text: String): String {
            return text.replace(" ", "")
        }

        fun formatCardNumber(cardNumber: String): String {
            return if (cardNumber.length == 16) {
                cardNumber.substring(0, 4) + " " + cardNumber.substring(
                    4, 6
                ) + " ** **** " + cardNumber.substring(12, cardNumber.length)
            } else {
                cardNumber
            }
        }

        fun formatCardNumberVisible(cardNumber: String): String {
            return if (cardNumber.length == 16) {
                cardNumber.substring(0, 4) + " " +
                        cardNumber.substring(4, 8) + " " +
                        cardNumber.substring(8, 12) + " " +
                        cardNumber.substring(12, 16)
            } else {
                cardNumber
            }
        }

        fun formatCardAccountNumber(number: String): String {
            return if (number.length == 20) {
                number.substring(0, 4) + " " +
                        number.substring(4, 8) + " " +
                        number.substring(8, 12) + " " +
                        number.substring(12, 16) + " " +
                        number.substring(16, 20)
            } else {
                number
            }
        }

        fun formatCardAndWalletNumber(cardNumber: String): String {
            return when (cardNumber.length) {
                16 -> {
                    cardNumber.substring(0, 4) + " " + cardNumber.substring(
                        4, 6
                    ) + "** **** " + cardNumber.substring(12, cardNumber.length)
                }

                11 -> {
                    cardNumber.substring(0, 3) + " **" + cardNumber.substring(7, cardNumber.length)
                }

                else -> {
                    cardNumber
                }
            }
        }

        fun formatCardNumberNew(cardNumber: String): String {
            return if (cardNumber.length == 16) {
                cardNumber.substring(12, cardNumber.length)
            } else {
                cardNumber
            }
        }

        fun formatWalletNumber(cardNumber: String): String {
            return if (cardNumber.length == 11) {
                cardNumber.substring(0, 3) + " **" + cardNumber.substring(9, cardNumber.length)
            } else {
                cardNumber
            }
        }

        fun formatCardName(cardName: String): String {
            return if (cardName.length > 20) {
                cardName.substring(0..20) + "..." + "   "
            } else {
                cardName
            }
        }

        fun phoneFormat(account: String): String {
            var str = account
            if (str.startsWith("+998") && str.length == 13) {
                str = account.substring(0, 6) + " " + account.substring(
                    6, 9
                ) + " " + account.substring(9, 11) + " " + account.substring(
                    11, account.length
                )
            }
            if (str.startsWith("998") && str.length == 12) {
                str = "+" + account.substring(0, 5) + " " + account.substring(
                    5, 8
                ) + " " + account.substring(8, 10) + " " + account.substring(
                    10, account.length
                )
            }
            return str
        }

        fun expireDate(str: String): String {
            if (str.isEmpty()) return str
            return try {
                val month = str.substring(0, 2)
                val day = str.substring(2)
                val date = day + month
                if (date.contains("-")) {
                    date.replace("-", ".")
                } else {
                    date.substring(0, 2) + "/" + date.substring(2, date.length)
                }
            } catch (e: Exception) {
                str
            }
        }

        fun getExpireDate(str: String): String {
            val month = str.substring(0, 2)
            val day = str.substring(2)
            val date = day + month
            return date
        }

        fun sentExpireDate(str: String): String {
            return if (str.isNotEmpty() && str.length > 3) {
                str.substring(2, 4) + str.substring(0, 2)
            } else {
                ""
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

            val lst = StringTokenizer(rounded, ".")
            var str1: String = rounded
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
                    return str3.toString()
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

        fun formatAmountScale(str: String?, currency: String): String {
            if (str == null) {
                return ""
            }
            val scale = if (currency == CURRENCY_CHAR_USD) 3 else 2
            if (str.isEmpty()) {
                return "0"
            }
            val rounded = str.replace(" ", "").replace(",", ".").toBigDecimal()
                .setScale(scale, RoundingMode.UP).toString()
            var value = rounded
            if (value != null) {
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
                        return str3.toString()
                    }
                    if (i == 3) {
                        str3.insert(0, " ")
                        i = 0
                    }
                    str3.insert(0, str1[k])
                    i++
                    k--
                }
            } else {
                return ""
            }
        }

        fun formatAmount(str: String?, scale: Int): String {
            if (str == null) {
                return ""
            }
            val rounded = str.replace(" ", "").replace(",", ".").toBigDecimal()
                .setScale(scale, RoundingMode.UP).toString()
            var value = rounded
            if (value != null) {
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
                        return str3.toString()
                    }
                    if (i == 3) {
                        str3.insert(0, " ")
                        i = 0
                    }
                    str3.insert(0, str1[k])
                    i++
                    k--
                }
            } else {
                return ""
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun getDateFromMilliseconds(millis: Long, dateFormat: String): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = /*seconds * 1000L*/millis
            return formatter.format(calendar.time)
        }

        fun getMilliseconds(givenDateString: String): Long {
            val df = SimpleDateFormat("yyyyMMdd")
            var timeInMilliseconds: Long = 0
            try {
                val mDate = df.parse(givenDateString)
                timeInMilliseconds = mDate.time
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return timeInMilliseconds
        }

        var locale: Locale? = null
            get() {
                field = Locale("en", "UK")
                return field
            }

        var formatSymbols: DecimalFormatSymbols? = null
            get() {
                field = DecimalFormatSymbols(locale)
                field?.decimalSeparator = '.'
                field?.groupingSeparator = ' '
                return field
            }

        var pattern = "#,###.##"

        var decimalFormat: DecimalFormat? = null
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

        fun formatAmountTariffs(summ: Int): String {
            return amountWithSpacesInteger(summ.toString())
        }


    }

    fun formatAmount(value: String?): String {
        var value = value
        if (value != null) {
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
            var str3 = StringBuilder()
            var i = 0
            var j = -1 + str1.length
            if (str1[-1 + str1.length] == '.') {
                j--
                str3 = StringBuilder(".")
            }
            var k = j
            while (true) {
                if (k < 0) {
                    if (str2.length > 0) str3.append(".").append(str2)
                    return str3.toString()
                }
                if (i == 3) {
                    str3.insert(0, " ")
                    i = 0
                }
                str3.insert(0, str1[k])
                i++
                k--
            }
        } else {
            return ""
        }
    }

    fun formattedDepositExpire(context: Context, expire: String): String {
        if (expire.isNotEmpty()) {
            when (expire.last()) {
                'D' -> {
                    return if (expire.dropLast(1) == "1") {
                        expire.dropLast(1) + " " + context.getString(R.string.day)
                    } else {
                        expire.dropLast(1) + " " + context.getString(R.string.days)
                    }
                }

                'M' -> {
                    return if (expire.dropLast(1) == "1") {
                        expire.dropLast(1) + " " + context.getString(R.string.month)
                    } else {
                        expire.dropLast(1) + " " + context.getString(R.string.months)
                    }
                }

                'Y' -> {
                    return if (expire.dropLast(1) == "1") {
                        expire.dropLast(1) + " " + context.getString(R.string.year)
                    } else {
                        expire.dropLast(1) + " " + context.getString(R.string.years)
                    }
                }

                else -> return expire
            }
        } else return context.getString(R.string.indefinite)
    }

    fun setMonitoringDate(context: Context, date: String): String {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DATE, -1)
        val yesterday = calendar.time
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val secondFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault())
        return when (date) {
            df.format(today) -> context.getString(R.string.today)
            df.format(yesterday) -> context.getString(R.string.yesterday)
            else -> if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                try {
                    val newFormat: Date = df.parse(date)
                    secondFormat.format(newFormat).toString()
                } catch (e: ParseException) {
                    date
                }
            } else {
                date
            }
        }
    }

    fun setChequeDate(context: Context, date: String): String {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DATE, -1)
        val yesterday = calendar.time
        val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        val secondFormat = SimpleDateFormat("dd MMMM, HH:mm", Locale.getDefault())
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                val newFormat: Date = df.parse(date)
                when (date.substring(0, 9)) {
                    df.format(today).substring(
                        0, 9
                    ) -> context.getString(R.string.today) + " " + secondFormat.format(newFormat)
                        .toString()

                    df.format(yesterday).substring(
                        0, 9
                    ) -> context.getString(R.string.yesterday) + " " + secondFormat.format(newFormat)
                        .toString()

                    else -> secondFormat.format(newFormat).toString()
                }
            } catch (e: ParseException) {
                date
            }
        } else {
            date
        }
    }

    fun setApplicationDate(date: String): String {
        val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
        val secondFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.US)
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                secondFormat.format(df.parse(date)!!).toString()
            } catch (e: ParseException) {
                date
            }
        } else {
            date
        }
    }

    fun firstLetterUpperCase(text: String): String {
        return if (text.isNotEmpty()) text[0].uppercase() + text.substring(0, text.length) else ""
    }

    fun percentAmount(amount: String, percent: String?): String {
        var newAmount = "0.00"
        if (amount.isNotEmpty() && percent != null) {
            val newPercent = (percent.toDouble() / 100);
            val summa = (amount.replace(" ", "").toDouble() * newPercent).toString()
                .split(".0")[0].toDouble()
            val maxsum = amount.replace(" ", "").toDouble() + summa
            newAmount = formatAmount(maxsum.toString())


        } else newAmount
        return newAmount
    }

    fun getCurrencyChar(currencyCode: String): String {
        return when (currencyCode) {
            CURRENCY_CODE_UZS -> CURRENCY_CHAR_UZS
            CURRENCY_CODE_USD -> DOLLAR_SIGN
            CURRENCY_CODE_RUB -> CURRENCY_CHAR_RUB
            CURRENCY_CODE_EUR -> CURRENCY_CHAR_EUR
            else -> ""
        }
    }


}