package uz.fido.universaldigital.ui.fragments.transfers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import coil.load
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.utils.recyclerview.MenuServiceItem
import uz.fido.utils.view.custom_text_view.TextViewMedium
import uz.fido.utils.view.custom_text_view.TextViewRegular

class MenuTransfersAdapter(
    private val context: Context,
    private val data: List<MenuServiceItem>,
    private var onItemClickListener: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_menu_transfer_type, parent, false)
        val item = data[position]
        itemView.apply {
            findViewById<TextViewMedium>(R.id.transfer_type_name).text =
                item.serviceName
            findViewById<TextViewRegular>(R.id.transfer_type_desc).text =
                item.serviceDescription
            findViewById<ImageView>(R.id.transfer_icon).load(item.icon) {
                crossfade(true)
            }
            findViewById<LinearLayoutCompat>(R.id.father).setOnClickListener {
                onItemClickListener.invoke(item.serviceId)
            }
//            findViewById<TextViewMedium>(R.id.tv_soon).isVisible = item.serviceId == 500
        }
        return itemView
    }
}