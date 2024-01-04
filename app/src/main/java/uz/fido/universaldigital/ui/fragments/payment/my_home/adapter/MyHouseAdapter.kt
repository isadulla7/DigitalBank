package uz.fido.universaldigital.ui.fragments.payment.my_home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.my_house.MyHouseGroup
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMyHouseGroupBinding

class MyHouseAdapter(
    private var list: ArrayList<MyHouseGroup>,
    private val context: Context,
    private val baseInterface: BaseInterface
) : RecyclerView.Adapter<MyHouseAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemMyHouseGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(myHouseGroup: MyHouseGroup) {
            binding.text.text = myHouseGroup.name

            binding.more.setOnClickListener {
                baseInterface.myHouseMoreIcon(myHouseGroup, "more")
            }
            binding.father.setOnClickListener {
                baseInterface.myHouseMoreIcon(myHouseGroup, "service")
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMyHouseGroupBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun setList(list: java.util.ArrayList<MyHouseGroup>) {
        this.list = list
        notifyDataSetChanged()
    }
}