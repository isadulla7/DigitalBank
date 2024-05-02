package uz.fido.utils.view.custom_edit_text.amount_edit_text

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.InputFilter
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import com.google.android.material.textfield.TextInputEditText
import uz.fido.utils.R
import kotlin.math.max

class AmountEditText(
    context: Context, attrs: AttributeSet
) : TextInputEditText(context, attrs), AmountInterface {

    private var amountInterface: AmountInterface? = null
    private var minAmount: String? = null
    private var maxAmount: String? = null

    override fun checkedForAmount(amountIsRight: Boolean) {
        amountInterface?.checkedForAmount(amountIsRight)
    }

    private val textPaint: TextPaint by lazy {
        TextPaint().apply {
            color = currentHintTextColor
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
            this.typeface = typeface
        }
    }

    private val prefixDrawable: PrefixDrawable by lazy { PrefixDrawable(paint) }

    private var suffixPadding: Float = 0f

    private var prefix: String = ""
        set(value) {
            if (value.isNotBlank()) {
                Log.v(TAG, "prefix: $value")
            }
            field = value
            prefixDrawable.text = value
            updatePrefixDrawable()
        }

    private var suffix: String? = null
        set(value) {
            if (!value.isNullOrBlank()) {
                Log.v(TAG, "suffix: $value")
            }
            field = value
            invalidate()
        }

    private val firstLineBounds = Rect()

    private var isInitialized = false

    private var amountTextWatcher = AmountTextWatcher(this, this, minAmount, maxAmount)

    var filter: InputFilter = InputFilter { charSequence, _, _, _, dstart, _ ->
        val temp = text!!.toString() + charSequence.toString()
        if (temp.contains(".")) {
            if (temp == ".") return@InputFilter "0."
            else if (temp.contains("..")) return@InputFilter ""
            if (dstart <= temp.indexOf(".")) return@InputFilter null
            val tmp = temp.substring(temp.indexOf(".") + 1)
            if (tmp.length > 2) return@InputFilter ""
        }
        null
    }

    init {
        this.filters = arrayOf(filter, InputFilter.LengthFilter(MAX_LENGTH))
        this.addTextChangedListener(amountTextWatcher)
        textPaint.textSize = textSize

        updatePrefixDrawable()
        isInitialized = true

        val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.AmountEditText)
        prefix = typedArray.getString(R.styleable.AmountEditText_prefix) ?: ""
        suffix = typedArray.getString(R.styleable.AmountEditText_suffix)
        suffixPadding = typedArray.getDimension(R.styleable.AmountEditText_suffixPadding, 0f)
        typedArray.recycle()
    }

    public override fun onDraw(c: Canvas) {
        textPaint.color = currentHintTextColor

        val lineBounds = getLineBounds(0, firstLineBounds)
        prefixDrawable.let {
            it.lineBounds = lineBounds
            it.paint = textPaint
        }

        super.onDraw(c)

        val text = text.toString()
        val prefixText: String = prefixDrawable.text
        if (text.isNotEmpty()) {
            textPaint.measureText(prefixText + text) + paddingLeft
        } else {
            textPaint.measureText(prefixText + hint) + paddingLeft
        }
        val suffixXPosition = textPaint.measureText(getText().toString()).toInt() + paddingLeft

        suffix?.let {
            c.drawText(it, max(suffixXPosition.toFloat(), suffixPadding), baseline.toFloat(), textPaint)
        }
    }

    private fun updatePrefixDrawable() {
        setCompoundDrawablesRelative(prefixDrawable, null, null, null)
    }

    companion object {
        private const val TAG = "AmountEditText"
        private const val MAX_LENGTH = 16
    }

}