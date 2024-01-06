package uz.fido.universaldigital.ui.fragments.payment.templates.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.utils.const.APIServiceConst
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.view.recycler_view_drag.ItemTouchHelperAdapter
import uz.fido.utils.view.custom_text_view.TextViewMedium
import uz.fido.utils.view.custom_text_view.TextViewRegular
import uz.fido.utils.view.custom_text_view.TextViewSemiBold
import java.util.Collections

class TemplateListAdapter(
    private val context: Context, private val baseInterface: BaseInterface,
    private var list: ArrayList<Template>
) : RecyclerView.Adapter<TemplateListAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_template, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.textName.text = item.name
        if (item.icon_name != "" && item.service_group_code != "SWIFT")
            Picasso.get().load(APIServiceConst.UNIVERSAL_PAYMENT_PHOTO + item.icon_name)
                .error(R.drawable.ic_payments_placeholder).into(holder.imageView)
        else holder.imageView.setImageResource(R.drawable.ic_payments_placeholder)
        holder.amount.text = (item.amount.toString()) + " UZS"
        if (item.account.toString().isNotEmpty()) {
            holder.description.text = item.account
        } else {
            holder.description.text = item.service_group_name
        }
        if (item.service_group_code == "P2P") {
            holder.description.text = context.getString(R.string.transfer)
        }
        if (item.service_group_code == "PAYMENT_ONE_TIME") {
            holder.description.text = context.getString(R.string.payment)
        }
        holder.itemView.setOnClickListener {
            baseInterface.openTemplate(item)
        }
        if (item.service_group_code == "SWIFT") {
            holder.description.text = item.service_group_code
//            holder.imageView.setPadding(4, 4, 4, 4)
            holder.imageView.setImageResource(R.drawable.ic_transfer_swift)
        }
        if (item.balance.isNullOrEmpty() || item.balance.toString().startsWith("0")) {
            holder.textBalance.visibility = View.GONE
            holder.description.visibility = View.VISIBLE
        } else {
            holder.description.visibility = View.GONE
            holder.textBalance.visibility = View.VISIBLE
            if (item.balance.toString().startsWith("-")) {
                holder.textBalance.text = "${Format.formatAmount(item.balance)} UZS"
                holder.textBalance.setTextColor(
                    ContextCompat.getColor(
                        context,
                        uz.fido.utils.R.color.status_not_identified
                    )
                )
            } else {
                holder.textBalance.setTextColor(
                    ContextCompat.getColor(
                        context,
                        uz.fido.utils.R.color.status_identified
                    )
                )
                holder.textBalance.text = "${Format.formatAmount(item.balance)} UZS"
            }
        }
        holder.moreButton.setOnClickListener {
            baseInterface.templateOperation(position, item)
        }
    }

    fun setList(templates: ArrayList<Template>) {
        list = templates
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.icon)!!
        val textName = view.findViewById<TextViewMedium>(R.id.payment_name)!!
        val description = view.findViewById<TextViewRegular>(R.id.service_name)!!
        val amount = view.findViewById<TextViewRegular>(R.id.payment_amount)!!
        val moreButton = view.findViewById<ImageView>(R.id.more)!!
        val textBalance = view.findViewById<TextViewSemiBold>(R.id.text_balance)!!
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        for (i in 0 until list.size) {
            list[i].ord = i
        }
        baseInterface.updateTemplateList(list)
        return true
    }

    override fun onItemDismiss(position: Int) {}

}