package uz.fido.utils.view.custom_empty_view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import uz.fido.utils.R
import uz.fido.utils.databinding.CustomEmptyViewBinding

/**
 * Created by Husniddin Muhammad Amin on 19.01.2023
 * Tashkent, Uzbekistan.
 */

class EmptyView(context: Context, attr: AttributeSet) : LinearLayoutCompat(context, attr) {

    private val binding: CustomEmptyViewBinding

    init {
        inflate(context, R.layout.custom_empty_view, this)
        binding = CustomEmptyViewBinding.bind(this)
        val attributes = context.obtainStyledAttributes(attr, R.styleable.EmptyView)
        binding.apply {
            title.text = attributes.getString(R.styleable.EmptyView_ev_title)
            description.text = attributes.getString(R.styleable.EmptyView_ev_desc)
            if (attributes.getDrawable(R.styleable.EmptyView_ev_icon) != null) {
                icon.setImageDrawable(attributes.getDrawable(R.styleable.EmptyView_ev_icon))
            }
        }
        attributes.recycle()
    }

}