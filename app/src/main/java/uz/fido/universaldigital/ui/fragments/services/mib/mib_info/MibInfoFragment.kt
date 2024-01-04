package uz.fido.universaldigital.ui.fragments.services.mib.mib_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.mib.Mib
import uz.fido.network.domain.model.mib.MibDetail
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMibInfoBinding
import uz.fido.universaldigital.databinding.ItemMibInfoDetailBinding
import uz.fido.universaldigital.ui.fragments.services.mib.MibViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.format.Format

@AndroidEntryPoint
class MibInfoFragment : BaseFragment<FragmentMibInfoBinding, MibViewModel>
    (FragmentMibInfoBinding::inflate, MibViewModel::class.java) {

    private var mib: Mib? = null
    private var mibDetail: MibDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mib = requireArguments().serializable<Mib>("mib") as Mib
        mibDetail = requireArguments().serializable<MibDetail>("mib_detail") as MibDetail
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvAmount.text = Format.formatAmount(mibDetail?.debet_summa) + " UZS"
        tvSetItem()
    }

    private fun tvSetItem() {
        mibDetail!!.features!!.forEach {
            val view =
                ItemMibInfoDetailBinding.inflate(LayoutInflater.from(requireContext()), null, false)
            var name = ""
            var personalAccount = ""
            when (it.field_key) {
                "fio" -> {
                    name = getString(R.string.fio)
                }

                "adress" -> {
                    name = getString(R.string.address_mib)
                }

                "purpose" -> {
                    name = getString(R.string.purpose_type)
                }

                "mib_branch" -> {
                    name = getString(R.string.fillial_mib)
                }

                "customer_code", "licshet" -> {
                    name = getString(R.string.personal_account)
                    personalAccount = it.field_value.toString()
                }
            }
            if (name.isNotEmpty()) {
                view.tvTip.text = name
                view.name.text = it.field_value
                binding.layoutMain.addView(view.root)
            }
        }
    }


}