package uz.fido.utils.view.custom_app_bar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import uz.fido.utils.R
import uz.fido.utils.databinding.AppBarWithSubtitleBinding

/**
 * Created by Husniddin Muhammad Amin on 19.01.2023
 * Tashkent, Uzbekistan.
 */

class AppBarWithSubtitle(context: Context, attr: AttributeSet) : LinearLayoutCompat(context, attr) {

    private val binding: AppBarWithSubtitleBinding

    init {
        inflate(context, R.layout.app_bar_with_subtitle, this)
        binding = AppBarWithSubtitleBinding.bind(this)
        val attributes = context.obtainStyledAttributes(attr, R.styleable.AppBarWithSubtitle)
        binding.apply {
            title.text = attributes.getString(R.styleable.AppBarWithSubtitle_title)
            subtitle.text = attributes.getString(R.styleable.AppBarWithSubtitle_subtitle)
            additional.setImageDrawable(attributes.getDrawable(R.styleable.AppBarWithSubtitle_additionalIcon))
        }
        attributes.recycle()
    }

    fun setTitle(title: String) {
        binding.title.text = title
    }

    fun setSubtitle(subtitle: String) {
        binding.subtitle.text = subtitle
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