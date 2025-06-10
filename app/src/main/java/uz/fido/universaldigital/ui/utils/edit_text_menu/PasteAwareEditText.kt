package uz.fido.universaldigital.ui.utils.edit_text_menu

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PasteAwareEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    var onPaste: ((pastedText: String) -> Unit)? = null

    override fun onTextContextMenuItem(id: Int): Boolean {
        if (id == android.R.id.paste || id == android.R.id.pasteAsPlainText) {
            serviceCallNumber()
        }
        return super.onTextContextMenuItem(id)
    }

    private fun serviceCallNumber() {
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val clipText = clipboard.primaryClip?.getItemAt(0)?.text.toString()
            if (clipText.startsWith("+998")) {
                val cleaned = clipText
                    .removePrefix("+998")
                    .replace("998", "")
                    .replace(Regex("[^0-9]"), "")
                val result = "+998$cleaned"
                onPaste?.invoke(result)
            }
        }
    }
}