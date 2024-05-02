package uz.fido.universaldigital.ui.fragments.services.order_card.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.universaldigital.R
import uz.fido.utils.view.custom_text_view.TextViewMedium
import uz.fido.utils.view.custom_text_view.TextViewRegular

class OrderCardInfoAdapter(
    private val list: ArrayList<AllServiceLists>,
) : RecyclerView.Adapter<OrderCardInfoAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_card_info, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
//        if (list[i].price != null) {
//            viewHolder.textView.text = list[i].name + " - " + list[i].price + " sum"
//        } else {
//            viewHolder.textView.text = list[i].name
//        }
        viewHolder.textView.text = list[i].name
        viewHolder.costsTv.text = list[i].code
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextViewRegular>(R.id.title_tv)!!
        val costsTv = view.findViewById<TextViewMedium>(R.id.costs_tv)!!
    }


    override fun getItemCount(): Int {
        return list.size
    }
}