package uz.fido.universaldigital.ui.dialogs.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.deposits.constructor.BxmCodeAndName
import uz.fido.universaldigital.R
import uz.fido.utils.view.custom_text_view.TextViewRegular

class NearBranchAdapter(
    private val list: ArrayList<BxmCodeAndName>,
    private val selectedBranch: (BxmCodeAndName) -> Unit
) : RecyclerView.Adapter<NearBranchAdapter.ViewHolder>() {

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
        viewHolder.textView.text = list[i].name
        viewHolder.itemView.setOnClickListener {
            selectedBranch.invoke(list[i])
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextViewRegular>(R.id.textView)!!
    }

}