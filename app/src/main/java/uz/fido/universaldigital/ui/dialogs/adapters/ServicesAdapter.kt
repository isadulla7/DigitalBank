package uz.fido.universaldigital.ui.dialogs.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.utils.view.custom_text_view.TextViewRegular

@SuppressLint("SetTextI18n")
class ServicesAdapter(
    private val baseInterface: BaseInterface,
    private val list: ArrayList<AllServiceLists>,
    private val tag: String
) : RecyclerView.Adapter<ServicesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_all_service, viewGroup, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        if (list[i].price != null) {
            viewHolder.textView.text = list[i].name + " - " + list[i].price + " sum"
        } else {
            viewHolder.textView.text = list[i].name
        }
        viewHolder.itemView.setOnClickListener {
            baseInterface.setToEditText(list[i], tag)
        }
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextViewRegular>(R.id.textView)!!
    }

}