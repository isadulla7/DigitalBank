package uz.fido.universaldigital.ui.fragments.payment.my_home.add_service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.my_house.MyHouseGroup
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.payment.TemplateKeyValue
import uz.fido.network.domain.model.template.GetTemplateResponse
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMyHouseServiceDetailsBinding
import uz.fido.universaldigital.databinding.ViewDepositCreateBinding
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.my_home.MyHomeViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MyHouseServicesDetailsFragment :
    BaseFragment<FragmentMyHouseServiceDetailsBinding, MyHomeViewModel>
        (FragmentMyHouseServiceDetailsBinding::inflate, MyHomeViewModel::class.java) {

    private var template: Template? = null
    private var getTemplateResponse: GetTemplateResponse? = null
    private var myHouse: MyHouseGroup? = null
    private var paymentService: PaymentService? = null

    companion object {
        const val ITEM_TEMPLATE = "template"
        const val ITEM_TEMPLATE_RESPONSE = "response"
        const val MY_HOUSE_ITEM = "MY_HOUSE_ITEM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            template = it.serializable(ITEM_TEMPLATE) as Template?
            getTemplateResponse = it.serializable(ITEM_TEMPLATE_RESPONSE) as GetTemplateResponse?
            myHouse = it.serializable(MY_HOUSE_ITEM) as MyHouseGroup?
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContinue.isEnabled(true)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener { nextPaymentFragment() }
        val dbHelper = DatabaseHelper(requireContext())
        paymentService = dbHelper.getServiceByContractId(template!!.service_id.toString())

        for (i in 0 until getTemplateResponse?.template_details!!.size) {
            if (getTemplateResponse!!.template_details[i].is_visible == "Y" &&
                getTemplateResponse!!.template_details[i].name!!.isNotEmpty()
            ) {
                drawViews(getTemplateResponse?.template_details!!, i)
            }
        }
    }

    private fun nextPaymentFragment() {
        gotoWithSlide(
            R.id.myHouseSinglePaymentFragment, bundleOf(
                MyHouseSinglePaymentFragment.MY_HOUSE_TEMPLATE to template,
                MyHouseSinglePaymentFragment.MY_HOUSE_TEMPLATE_RESPONSE to getTemplateResponse,
                MyHouseSinglePaymentFragment.MY_HOUSE_ITEM to myHouse
            )
        )
    }

    private fun drawViews(list: ArrayList<TemplateKeyValue>, position: Int) {
        val keyValue = list[position]
        val viewDepositCreateBinding =
            ViewDepositCreateBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = keyValue.name
        viewDepositCreateBinding.value.text = keyValue.value
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }
}