package uz.fido.universaldigital.ui.fragments.products.widgets.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.news.Notification
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemNotificationsBinding
import uz.fido.universaldigital.ui.utils.extensions.getFormattedDate

class NotificationsAdapter(
    private var baseInterface: BaseInterface,
    private var list: ArrayList<Notification>
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notification) {
            binding.title.text = item.title
            binding.description.text = item.text
            binding.date.text = itemView.context.getFormattedDate(item.created_on)
            if (item.is_read == "Y") {
                binding.father.setBackgroundResource(R.drawable.cornered_bg_12dp_stroke)
                binding.readState.visibility = View.GONE
            } else {
                binding.father.setBackgroundResource(R.drawable.cornered_bg_12dp_stroke_selected)
                binding.readState.visibility = View.VISIBLE
                binding.readState.text = itemView.context.getString(R.string.unread)
                binding.readState.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.monitoring_amount
                    )
                )
            }
            binding.father.setOnClickListener {
                baseInterface.openNotification(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun setList(news: ArrayList<Notification>) {
        list = news
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

}