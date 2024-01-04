package uz.fido.utils.utility.view

import android.os.Build
import android.text.Html
import androidx.core.text.HtmlCompat
import uz.fido.utils.view.custom_edit_text.amount_edit_text.AmountEditText
import uz.fido.utils.view.custom_edit_text.mask_edit_text.MaskEditText
import uz.fido.utils.view.custom_text_view.TextViewMedium

fun TextViewMedium.setHtmlHint(text: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    } else {
        this.text = Html.fromHtml(text)
    }
}

fun MaskEditText.setHtmlHint(text: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.hint = Html.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    } else {
        this.hint = Html.fromHtml(text)
    }
}

fun AmountEditText.setHtmlHint(text: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.hint = Html.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    } else {
        this.hint = Html.fromHtml(text)
    }
}