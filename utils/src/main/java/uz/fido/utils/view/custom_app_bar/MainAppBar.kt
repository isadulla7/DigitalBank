package uz.fido.utils.view.custom_app_bar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import uz.fido.utils.R
import uz.fido.utils.databinding.AppBarBinding
import uz.fido.utils.utility.fragment.pop

/**
 * Created by Husniddin Muhammad Amin on 19.01.2023
 * Tashkent, Uzbekistan.
 */
class MainAppBar(context: Context, attr: AttributeSet) : LinearLayoutCompat(context, attr) {

    private val binding: AppBarBinding

    init {
        inflate(context, R.layout.app_bar, this)
        binding = AppBarBinding.bind(this)
        val attributes = context.obtainStyledAttributes(attr, R.styleable.MainAppBar)
        binding.apply {
            title.text = attributes.getString(R.styleable.MainAppBar_title)
            additional.setImageDrawable(attributes.getDrawable(R.styleable.MainAppBar_additionalIcon))
        }
        attributes.recycle()
    }

    fun setTitle(title: String) {
        binding.title.text = title
    }

    fun setOnBackButtonClickListener(listener: OnClickListener) {
        binding.back.setOnClickListener(listener)
    }

    fun setOnBackButtonClickListener(function: () -> Unit) {
        binding.back.setOnClickListener {
            function.invoke()
        }
    }

    fun setAdditionalBtnVisibility(visible: Boolean) {
        binding.additional.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    fun setOnAdditionalBtnClickListener(listener: OnClickListener) {
        binding.additional.setOnClickListener(listener)
    }

    fun setOnAdditionalBtnClickListener(function: () -> Unit) {
        binding.additional.setOnClickListener {
            function.invoke()
        }
    }

}