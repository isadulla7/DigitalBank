package uz.fido.universaldigital.ui.fragments.products.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.news.Notification
import uz.fido.universaldigital.databinding.StackItemBinding

class CardStackAdapter(
    private val context: Context,
    private val list: ArrayList<Notification>,
    private val onCLick: (Notification) -> Unit
) : RecyclerView.Adapter<CardStackAdapter.VhStack>() {

    inner class VhStack(private val view: StackItemBinding) : RecyclerView.ViewHolder(view.root) {

        fun obBind(item: Notification) {
            view.notificationText.text = item.text.trim()
            view.notificationTitle.text = item.title.trim()
            view.notificationHide.setOnClickListener {
                onCLick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhStack {
        val binding = StackItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return VhStack(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: VhStack, position: Int) {
        holder.obBind(list[position])
    }

    fun setList(notification: Notification) {
        val position = list.indexOf(notification)
        list.remove(notification)
        notifyDataSetChanged()
    }


}