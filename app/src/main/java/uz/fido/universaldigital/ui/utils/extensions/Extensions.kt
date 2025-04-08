package uz.fido.universaldigital.ui.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.DisplayMetrics
import android.view.PixelCopy
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import coil.load
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.ui.dialogs.BaseInfoDialog
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.setProcessingStatusIsNotWorking
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.universaldigital.ui.utils.validator.RangeValidator
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.const.ServerMessages.getMeaningFulMessage
import uz.fido.utils.device.vibrateTick
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.utility.fragment.goto
import java.io.Serializable
import java.util.Calendar
import java.util.GregorianCalendar

fun yearText(context: Context, year: Int): String {
    return when (year) {
        1 -> context.getString(R.string.year)
        2, 3, 4 -> context.getString(R.string.goda)
        else -> context.getString(R.string.let)
    }
}

fun getFormattedContact(phoneNumber: String): String {
    val formatted = phoneNumber.replace(" ", "")
    return when {
        formatted.startsWith("+998") && formatted.length == 13 -> formatted
        formatted.startsWith("998") && formatted.length == 12 -> "+$formatted"
        formatted.length == 9 -> "+998$formatted"
        else -> ""
    }
}

fun TextView.setHtmlText(text: String) {
    this.text = Html.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun TextInputLayout.setHtmlHint(text: String) {
    this.hint = Html.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun TextInputLayout.setPaymentHint(paymentParams: PaymentParams) {
    val hint = if (paymentParams.hint.toString()
            .isNotEmpty()
    ) paymentParams.hint else paymentParams.name
    this.hint = Html.fromHtml(hint, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun Fragment.hideSoftKeyboard() {
    activity?.hideSoftKeyboard()
}

fun Activity.hideSoftKeyboard() {
    if (currentFocus != null) {
        val inputMethodManager = getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    }
}

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
        key,
        T::class.java
    )

    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}

fun ImageView.loadTemplateImage(iconName: String) {
    val url = "${Keys.paynetPhotoUrl()}$iconName"
    this.load(url) {
        crossfade(true)
//        transformations(RoundedCornersTransformation(convertDpToPixel(12f, context)))
        error(R.drawable.ic_payments_placeholder)
    }
}

fun convertDpToPixel(dp: Float, context: Context?): Float {
    return if (context != null) {
        val resources = context.resources
        val metrics = resources.displayMetrics
        dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    } else {
        val metrics = Resources.getSystem().displayMetrics
        dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}

fun View.delayOnLifecycle(
    durationInMillis: Long,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    block: () -> Unit
): Job? = findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
    lifecycleOwner.lifecycle.coroutineScope.launch(dispatcher) {
        delay(durationInMillis)
        block()
    }
}

fun Fragment.showSnackbar(
    snackbarText: String,
    title: String? = null,
    buttonText: String? = null
) {
    val message = getMeaningFulMessage(snackbarText)
    if (message.isNotEmpty() && view != null) {
        val dialog = BaseInfoDialog(title ?: getString(R.string.error), message, buttonText)
        dialog.show(childFragmentManager, "")
    }
}

fun Context.getDrawable(name: String): Int {
    val resId = this.resources.getIdentifier(name, "drawable", this.packageName)
    return if (resId != 0) {
        resId
    } else R.drawable.bg_1
}

fun ImageView.setCurrencyFlag(currencyCode: String) {
    val icon = when (currencyCode) {
        "840" -> R.drawable.home_rate_usd
        "978" -> R.drawable.home_rate_eur
        "643" -> R.drawable.home_rate_ruble
        "826" -> R.drawable.rate_gbp
        "756" -> R.drawable.rate_chf
        "392" -> R.drawable.rate_jpy
        else -> R.drawable.home_rate_eur
    }
    this.load(icon)
}

fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    return ContextCompat.getDrawable(context, vectorResId)?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        draw(Canvas(bitmap))
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

fun Fragment.doTransferOperationByType(senderCard: CardResponse, receiverCard: CardResponse) {
    if (senderCard.currency_code == CurrencyConst.CURRENCY_CODE_UZS && receiverCard.currency_code == CurrencyConst.CURRENCY_CODE_UZS
    ) {
        goto(
            R.id.overMyCardsFragment,
            bundleOf(Const.SENDER_CARD to senderCard, Const.RECEIVER_CARD to receiverCard)
        )
        vibrateTick(requireContext())
    } else if ((senderCard.currency_code == CurrencyConst.CURRENCY_CODE_USD ||
                receiverCard.currency_code == CurrencyConst.CURRENCY_CODE_USD)
        && senderCard.currency_code != receiverCard.currency_code
    ) {
        goto(
            R.id.conversionFragment,
            bundleOf(Const.SENDER_CARD to senderCard, Const.RECEIVER_CARD to receiverCard)
        )
        vibrateTick(requireContext())
    } else return
}

fun Fragment.isInternetConnected(context: Context): Boolean {
    var result: Boolean

    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCapabilities = if (connectivityManager.activeNetwork != null) {
        connectivityManager.activeNetwork
    } else {
        result = false
        null
    }
    result = if (connectivityManager.getNetworkCapabilities(networkCapabilities) != null) {
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities)!!
        when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        false
    }
    if (!isDetached && !result) {
        showSnackbar(context.getString(R.string.error_no_internet_connection))
    }
    return result
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

fun showProgress(activity: Activity, progressText: String? = null) =
    (activity as BaseActivity).showProgress(progressText)

fun hideProgress(activity: Activity) = (activity as BaseActivity).hideProgress()

fun AppCompatTextView.setTextSpanned(text: CharSequence) {
    this.setTextFuture(
        PrecomputedTextCompat.getTextFuture(
            text, TextViewCompat.getTextMetricsParams(this), null
        )
    )
}

fun EditText.showSoftKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun formatStringToHtml(message: String): String {
    val constCrlUpperH = "&#1202;"
    val constCrlLowerH = "&#1203;"
    val constCrlUpperG = "&#1170;"
    val constCrlLowerG = "&#1171;"
    val constCrlUpperQ = "&#1178;"
    val constCrlLowerQ = "&#1179;"
    val constCrlUpperO = "&#1038;"
    val constCrlLowerO = "&#1118;"

    return message.replace("Ҳ", constCrlUpperH).replace("ҳ", constCrlLowerH)
        .replace("Ғ", constCrlUpperG).replace("ғ", constCrlLowerG)
        .replace("Қ", constCrlUpperQ).replace("қ", constCrlLowerQ).replace("Ў", constCrlUpperO)
        .replace("ў", constCrlLowerO)
}

fun limitRange(): CalendarConstraints.Builder {
    val constraintsBuilderRange = CalendarConstraints.Builder()
    val calendarStart: Calendar = GregorianCalendar.getInstance()
    val calendarEnd: Calendar = GregorianCalendar.getInstance()
    calendarStart.set(2000, 0, 1)
    val minDate = calendarStart.timeInMillis
    val maxDate = calendarEnd.timeInMillis
    constraintsBuilderRange.setStart(minDate)
    constraintsBuilderRange.setEnd(maxDate)
    constraintsBuilderRange.setValidator(RangeValidator(minDate, maxDate))
    return constraintsBuilderRange
}

fun Fragment.isUserIdentified(): Boolean {
    val userTypeId = getFromSecureStore(Const.PAPER_CLIENT_USER_TYPE_ID).toInt()
    return if (userTypeId != null) {
        userTypeId == 1 || userTypeId == 2
    } else false
}

fun Fragment.checkIdentificationAndGoto(direction: Int) {
    if (isUserIdentified()) {
        goto(direction)
    } else {
        goto(R.id.mainIdentificationFragment2, bundleOf("need_identification" to true))
    }
}

fun setCardState(
    item: CardResponse,
    textView: TextView,
    context: Context
) {
    when (item.processing_server_status) {
        "-1" -> {
            textView.visibility = View.VISIBLE
            textView.setProcessingStatusIsNotWorking(item)
            return
        }

        "0" -> {
            textView.visibility = View.GONE
            return
        }

        "-100" -> {
            textView.visibility = View.VISIBLE
            textView.text = context.getString(R.string.can_not_receive_balance)
            return
        }

        null -> {
            textView.visibility = View.GONE
            return
        }

        else -> {
            textView.visibility = View.VISIBLE
            textView.text = item.stateName
        }
    }
}

fun Activity.openPlayMarket() {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setData(Uri.parse(Const.PLAY_MARKET))
    }
    this.startActivity(intent)
}

fun String.fixQuestionMarks() = this.replace("?", "'")

fun String.containsNumber(): Boolean {
    val regex = "\\d+".toRegex()
    return regex.containsMatchIn(this)
}

fun String.hasLetter(): Boolean {
    val uppercaseRegex = Regex("[A-Z]")
    val lowercaseRegex = Regex("[a-z]")
    val hasUpperCase = uppercaseRegex.containsMatchIn(this)
    val hasLowerCase = lowercaseRegex.containsMatchIn(this)
    return hasUpperCase && hasLowerCase
}

fun String.hasSpecialSymbol(): Boolean {
    val regex = Regex("[^A-Za-z0-9 ]")
    return regex.containsMatchIn(this)
}

fun String.removeSpace() = trim().replace("\\s+".toRegex(), replacement = "")

fun String.capitalizeFirstChar(): String {
    return this.replaceFirstChar { it.uppercase() }
}

fun recordException(e: Exception, activity: Activity, functionName: String? = "") {
    FirebaseCrashlytics.getInstance().apply {
        setCustomKey("class_name", activity.javaClass.simpleName)
        setCustomKey("function_name", functionName.orEmpty())
        recordException(e)
    }
}

fun Activity.recordException(e: Exception, functionName: String? = "") {
    FirebaseCrashlytics.getInstance().apply {
        setCustomKey("class_name", this@recordException.javaClass.simpleName)
        setCustomKey("function_name", functionName.orEmpty())
        recordException(e)
    }
}

fun recordException(e: Exception, fragment: Fragment, functionName: String? = "") {
    FirebaseCrashlytics.getInstance().apply {
        setCustomKey("class_name", fragment.javaClass.simpleName)
        setCustomKey("function_name", functionName.orEmpty())
        recordException(e)
    }
}

fun Fragment.recordException(e: Exception, functionName: String? = "") {
    FirebaseCrashlytics.getInstance().apply {
        setCustomKey("class_name", this@recordException.javaClass.simpleName)
        setCustomKey("function_name", functionName.orEmpty())
        recordException(e)
    }
}

fun RadioGroup.setChildrenEnable(enable: Boolean) {
    for (i in 0 until this.childCount) {
        this.getChildAt(i).isEnabled = enable
    }
}