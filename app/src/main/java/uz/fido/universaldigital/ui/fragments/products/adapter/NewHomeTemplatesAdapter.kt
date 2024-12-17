package uz.fido.universaldigital.ui.fragments.products.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ItemNewAddTemplateBinding
import uz.fido.universaldigital.databinding.ItemNewPaymentTemplateBinding
import uz.fido.universaldigital.ui.utils.extensions.loadTemplateImage

class NewHomeTemplatesAdapter(
    private val baseInterface: BaseInterface,
    private var templates: ArrayList<Template>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderAdd(private val binding: ItemNewAddTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.addTemplate.setOnClickListener {
                baseInterface.addTemplate()
            }
        }
    }

    inner class ViewHolder(private val binding: ItemNewPaymentTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Template) {
            binding.apply {
                templateName.text =
                    item.name.toString().ifEmpty { itemView.context.getString(R.string.no_name) }
                templateDescription.text = item.account_text
                if (item.account.toString().isNotEmpty()) {
                    binding.templateDescription.text = item.account
                } else {
                    binding.templateDescription.text = item.service_group_name
                }
                if (item.service_group_code == "P2P") {
                    binding.templateDescription.text = itemView.context.getString(R.string.transfer)
                }
                if (item.service_group_code == "PAYMENT_ONE_TIME") {
                    binding.templateDescription.text =
                        itemView.context.getString(R.string.transfer_to_account)
                }
                icon.loadTemplateImage(item.icon_name.toString())
                template.setOnClickListener {
                    baseInterface.openTemplate(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            ViewHolderAdd(
                ItemNewAddTemplateBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            ViewHolder(
                ItemNewPaymentTemplateBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun setList(temp: ArrayList<Template>) {
        templates = temp
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = templates.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(templates[position])
        } else {
            (holder as ViewHolderAdd).bind()
        }
    }

}