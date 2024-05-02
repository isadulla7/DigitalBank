package uz.fido.utils.view.custom_edit_text.amount_edit_text

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import uz.fido.utils.utility.format.Format
import uz.fido.utils.format.FormatUtil

class AmountTextWatcher internal constructor(
    private val editText: TextInputEditText,
    private var amountInterface: AmountInterface,
    private val minAmount: String? = null,
    private val maxAmount: String? = null
) : TextWatcher {
    private var previousCleanString: String? = null

    private fun noSpaceNoComma(text: String): String {
        var text = text
        if (text.contains(" ")) {
            text = text.replace(" ", "")
        }
        if (text.contains(",")) {
            text = text.replace(",", ".")
        }
        return text
    }

    private fun putMoneySpace(dataToSpace: String): String {
        val str = StringBuilder()
        var counter = -4
        var dot = false
        if (dataToSpace.contains(".")) {
            counter = -3
            dot = true
        }
        for (k in dataToSpace.length - 1 downTo 0) {
            if (counter == 3) {
                str.insert(0, dataToSpace[k] + " ")
                counter = if (dot) {
                    1
                } else {
                    0
                }
            } else {
                str.insert(0, dataToSpace[k])
                counter++
            }
        }
        return str.toString()
    }

    override fun afterTextChanged(editable: Editable?) {
        val str = editable.toString()
        if (str.isEmpty()) {
            amountInterface.checkedForAmount(false)
        }
        val cleanString = noSpaceNoComma(str)
        if (cleanString == previousCleanString || cleanString.isEmpty()) {
            return
        }
        previousCleanString = cleanString

        val formattedString: String = if (cleanString.contains(".")) {
            putMoneySpace(cleanString)
        } else {
            FormatUtil.toString(cleanString.toLong())
        }
        editable!!.replace(0, editable.length, formattedString)
        if (minAmount != null && maxAmount != null && editable.isNotEmpty()) {
            amountInterface.checkedForAmount(
                Format.checkForMinMaxAmount(
                    minAmount,
                    maxAmount,
                    amount = editable.toString()
                )
            )
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (editText.parent is TextInputLayout) {
            (editText.parent as TextInputLayout).error = null
            (editText.parent as TextInputLayout).isErrorEnabled = false
        }
    }

    companion object {
    }
}