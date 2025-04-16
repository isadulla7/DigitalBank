package uz.fido.universaldigital.ui.fragments.products.widgets.search.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemSearchBinding
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_PAYMENT
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_PAYMENT_GROUP
import uz.fido.universaldigital.ui.fragments.products.widgets.search.model.SearchItem
import uz.fido.universaldigital.ui.utils.extensions.loadPaymentIcon
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.utility.view.recycler_view_drag.ItemTouchHelperAdapter

class SearchAdapter(
    private val list: ArrayList<SearchItem>,
    private val baseInterface: BaseInterface,
    private var searchText: String
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    inner class ViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchItem) {
            binding.name.text = item.name
            when (item.groupName) {
                GROUP_NAME_PAYMENT -> {
                    Picasso.get()
                        .load(Keys.paynetPhotoUrl() + item.paymentService?.icon_name)
                        .error(R.drawable.ic_payments_placeholder).into(binding.icon)
                }

                GROUP_NAME_PAYMENT_GROUP -> {
                    binding.icon.loadPaymentIcon(item.imageName.toString())
                }

                else -> {
                    binding.icon.setImageResource(R.drawable.ic_search)
                    binding.icon.setPadding(16)
                }
            }
            setHighLightedText(itemView.context, binding.name, searchText)
            binding.father.setOnClickListener {
                baseInterface.openSearchItem(item)
            }
        }
    }

    fun setHighLightedText(context: Context, tv: TextView, textToHighlight: String) {
        val tvt = tv.text.toString().lowercase()
        var ofe = tvt.indexOf(textToHighlight.lowercase(), 0)
        val wordToSpan: Spannable = SpannableString(tv.text)
        var ofs = 0
        while (ofs < tvt.length && ofe != -1) {
            ofe = tvt.indexOf(textToHighlight, ofs)
            if (ofe == -1) break else {
                wordToSpan.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.brandRedColor)),
                    ofe,
                    ofe + textToHighlight.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE)
            }
            ofs = ofe + 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) = true

    override fun onItemDismiss(position: Int) {}

}