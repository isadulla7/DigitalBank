package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.my_house.MyHouseGroup
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.MainMyHouseGroupBinding

class MainMyHouseAdapter(
    private var list: ArrayList<MyHouseGroup>,
    private val onClickHouse: (MyHouseGroup) -> Unit = {},
    private val addNewHouse: () -> Unit = {}
) : RecyclerView.Adapter<MainMyHouseAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MainMyHouseGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(myHouseGroup: MyHouseGroup) {
            if (myHouseGroup.id != "0") {
                binding.icon.setImageResource(R.drawable.ic_new_home_icon)
            }
            binding.text.text = myHouseGroup.name
            binding.father.setOnClickListener {
                if (myHouseGroup.id != "0") {
                    onClickHouse(myHouseGroup)
                } else {
                    addNewHouse()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MainMyHouseGroupBinding.inflate(
                LayoutInflater.from(parent.context),
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