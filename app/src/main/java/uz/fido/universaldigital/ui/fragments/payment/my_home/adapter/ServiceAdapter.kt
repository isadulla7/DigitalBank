package uz.fido.universaldigital.ui.fragments.payment.my_home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemMyHomeServiceBinding
import uz.fido.utils.const.APIServiceConst.PAYNET_PHOTO
import uz.fido.utils.format.Format

class ServiceAdapter(
    private val context: Context,
    private var list: ArrayList<Template>,
    private val baseInterface: BaseInterface
) : RecyclerView.Adapter<ServiceAdapter.VhService>() {


    inner class VhService(val binding: ItemMyHomeServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(template: Template) {
            binding.text.text = template.name

            binding.more.background = if (template.isCurrent) {
                ContextCompat.getDrawable(context, R.drawable.ic_my_home_okay_check)
            } else ContextCompat.getDrawable(context, R.drawable.ic_my_home_service)

            if (template.icon_name != "")
                Picasso.get().load(PAYNET_PHOTO + template.icon_name).error(R.drawable.ic_payments_placeholder).into(binding.icon)
            else binding.icon.setImageResource(R.drawable.ic_payments_placeholder)

            if (!template.balance.isNullOrEmpty() &&
                !template.balance.toString().startsWith("0")
            ) {
                var balance = template.balance

                if (template.balance!!.startsWith("-")) {
                    balance = balance!!.replace("-", "")
                    binding.amount.setTextColor(ContextCompat.getColor(context, R.color.brandRedColor))
                    binding.amount.text = "-${Format.formatAmount(balance)} UZS"
                } else {
                    balance = balance!!.replace("+", "")
                    binding.amount.setTextColor(ContextCompat.getColor(context, R.color.mainTextColor))
                    template.amount = "0"
                    binding.amount.text = "${Format.formatAmount(balance)} UZS"
                }
            } else {
                template.amount = "0"
                binding.amount.text = "----"
            }

            binding.personText.text = template.account_text
            binding.person.text = template.account


            if (template.service_state != "A") {
                binding.errorColor.visibility = View.VISIBLE
            } else {
                binding.errorColor.visibility = View.GONE
                binding.father.setOnClickListener {
                    baseInterface.listOperation(adapterPosition)
                    //baseInterface.myHouseService(template,adapterPosition)
                }
            }
            binding.more.setOnClickListener {
                baseInterface.myHomeListAdd(adapterPosition)
                //    baseInterface.listOperation(adapterPosition)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhService {
        return VhService(ItemMyHomeServiceBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: VhService, position: Int) {
        holder.onBind(list[position])
    }

    fun setList(templateList: java.util.ArrayList<Template>) {
        list = templateList
        notifyDataSetChanged()
    }
}