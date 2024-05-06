package uz.fido.universaldigital.ui.fragments.payment.my_home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ItemMyHouseAmountBinding
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.APIServiceConst
import uz.fido.utils.format.Format

class MyHouseAmountAdapter(
    private val context: Context,
    private val list: ArrayList<Template>,
    private val onCLick: (String, Int) -> Unit
) : RecyclerView.Adapter<MyHouseAmountAdapter.VhMyHouse>() {


    inner class VhMyHouse(val binding: ItemMyHouseAmountBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(template: Template) {
            binding.etAmount.addTextChangedListener {
                template.amount = it.toString().replace(" ", "")
                if (!it.isNullOrEmpty()) {
                    onCLick.invoke(it.toString().replace(" ", ""), adapterPosition)
                }
            }

            binding.text.text = template.name.toString()
            binding.personText.text = template.account_text
            binding.person.text = template.account

            if (template.icon_name != "") Picasso.get()
                .load(Keys.paynetPhotoUrl() + template.icon_name)
                .error(R.drawable.ic_payments_placeholder).into(binding.icon)
            else binding.icon.setImageResource(R.drawable.ic_payments_placeholder)

            if (!template.balance.isNullOrEmpty() && !template.balance.toString()
                    .startsWith("0")
            ) {
                var balance = template.balance

                if (template.balance!!.startsWith("-")) {
                    balance = balance!!.replace("-", "")
                    binding.amount.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.color_auto_no_activ
                        )
                    )
                    binding.amount.text = "-${Format.formatAmount(balance)} UZS"
                } else {
                    balance = balance!!.replace("+", "")
                    binding.amount.setTextColor(
                        ContextCompat.getColor(
                            context, R.color.mainTextColor
                        )
                    )
                    template.amount = "0"
                    binding.amount.text = "${Format.formatAmount(balance)} UZS"
                }
            } else {
                template.amount = "0"
                binding.amount.text = "----"
            }

            if (!template.amount.isNullOrEmpty() && template.amount.toString() != "0") {
                binding.etAmount.setText(template.amount)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhMyHouse {
        val view = ItemMyHouseAmountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VhMyHouse(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: VhMyHouse, position: Int) {

        holder.onBind(list[position])
    }
}