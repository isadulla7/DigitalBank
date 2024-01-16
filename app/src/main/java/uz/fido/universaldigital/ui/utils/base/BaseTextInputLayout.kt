package uz.fido.universaldigital.ui.utils.base

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.BaseTextInputLayoutBinding

open class BaseTextInputLayout constructor(
    context: Context, attrs: AttributeSet
) : LinearLayoutCompat(context, attrs) {

    private val binding: BaseTextInputLayoutBinding
    //small change

    init {
        val attributes =
            context.obtainStyledAttributes(attrs, uz.fido.utils.R.styleable.BaseTextInputLayout)
        inflate(context, R.layout.base_text_input_layout, this)
        binding = BaseTextInputLayoutBinding.bind(this)
        binding.apply {
            setHint(attributes.getString(uz.fido.utils.R.styleable.BaseTextInputLayout_hint))
            setMaxLines(
                attributes.getInteger(
                    uz.fido.utils.R.styleable.BaseTextInputLayout_maxLines, DEFAULT_MAX_LINES
                )
            )
            setMaxLength(
                attributes.getInteger(
                    uz.fido.utils.R.styleable.BaseTextInputLayout_maxLength,
                    DEFAULT_MAX_LENGTH
                )
            )
            setText(attributes.getString(uz.fido.utils.R.styleable.BaseTextInputLayout_text))
            setTextColor(
                attributes.getColor(
                    uz.fido.utils.R.styleable.BaseTextInputLayout_textColor,
                    ContextCompat.getColor(context, R.color.brandBlueColor)
                )
            )
        }
        attributes.recycle()
    }

    fun setHint(hintText: String?) {
        hintText?.let {
            binding.textInputLayout.hint = it
        }
    }

    fun setText(text: String?) {
        text?.let {
            binding.editText.setText(it)
        }
    }

    fun setTextColor(color: Int) {
        binding.editText.setTextColor(color)
    }

    fun setMaxLines(maxLines: Int) {
        binding.editText.maxLines = maxLines
    }

    fun setMaxLength(maxLength: Int) {
        binding.editText.filters = arrayOf(InputFilter.LengthFilter(maxLength))
    }

    fun setInputType() {

    }

    fun doAfterTextChanged(listener: (String) -> Unit) {
        binding.editText.doAfterTextChanged { editable ->
            editable?.let {
                listener.invoke(it.toString())
            }
        }
    }

    companion object {
        const val DEFAULT_MAX_LINES = 1
        const val DEFAULT_MAX_LENGTH = 50
    }

}