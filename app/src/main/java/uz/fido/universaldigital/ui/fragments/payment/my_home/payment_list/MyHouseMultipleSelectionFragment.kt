package uz.fido.universaldigital.ui.fragments.payment.my_home.payment_list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.template.GetTemplateRequest
import uz.fido.network.domain.model.template.GetTemplateResponse
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMyHouseMultipleSelectionBinding
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.my_home.MyHomeViewModel
import uz.fido.universaldigital.ui.fragments.payment.my_home.adapter.MyHouseSelectionAdapter
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal
import java.sql.SQLException

@AndroidEntryPoint
class MyHouseMultipleSelectionFragment :
    BaseFragment<FragmentMyHouseMultipleSelectionBinding, MyHomeViewModel>(
        FragmentMyHouseMultipleSelectionBinding::inflate, MyHomeViewModel::class.java
    ) {
    private var list = ArrayList<Template>()
    private lateinit var dbHelper: DatabaseHelper
    private var totalAmount = BigDecimal(0)
    private lateinit var myHouseSeletionAdapter: MyHouseSelectionAdapter
    private var selectedCard: CardResponse? = null

    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            list = it.serializable<ArrayList<Template>>("list") as ArrayList<Template>
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            dbHelper = DatabaseHelper(requireContext())
            list.forEach {
                it.payment_service = dbHelper.getServiceByContractId(it.service_id.toString())
                it.payment_success = false
            }

        }
        init()
        onClickView()
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnPaymentList.setOnClickListener {
            if (list.size > 0 && selectedCard != null) {
                getTemplate(0)
            }
        }
    }

    private fun getTemplate(position: Int) {
        binding.btnPaymentList.setProgress(true)
        viewModel.getTemplate(
            getClientToken(), GetTemplateRequest(
                list[position].template_id,
                "N"
            )
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    list[position].template_response = it.data
                    if (list[position].payment_service != null) {
                        list[position].payment_params = checkForValues(
                            getPaymentDetails(list[position].payment_service!!),
                            it.data!!
                        )
                        if (position == list.size - 1) {
                            binding.btnPaymentList.setProgress(false)
                            goto(
                                R.id.myHouseMultiplePayFragment, bundleOf(
                                    "list" to list,
                                    "object" to selectedCard
                                )
                            )
                        } else {
                            getTemplate(position + 1)
                        }
                    } else {
                        getTemplate(position + 1)
                    }
                }

                Status.ERROR -> {

                }
            }
        }
    }

    private fun init() {
        list.forEach {
            totalAmount = totalAmount.plus(it.amount!!.toBigDecimal())
        }
        binding.amount.text =
            "${uz.fido.utils.format.Format.formatAmount(totalAmount.toString())} ${getString(R.string.uzs)}"
        initCards()

        binding.rec.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            myHouseSeletionAdapter = MyHouseSelectionAdapter(list)
            adapter = myHouseSeletionAdapter
        }
    }

    private fun checkForValues(
        inputParams: List<PaymentParams>,
        templateResponse: GetTemplateResponse
    ): ArrayList<PaymentParams> {
        val paymentParamsArrayList = ArrayList<PaymentParams>()
        for (paymentParams in inputParams) {
            val sampleParam = PaymentParams()
            sampleParam.payment_detail_code = paymentParams.payment_detail_code
            sampleParam.is_visible = paymentParams.is_visible
            sampleParam.param_type = paymentParams.param_type
            sampleParam.ord = paymentParams.ord
            sampleParam.param_length = paymentParams.param_length
            sampleParam.is_required = paymentParams.is_required
            sampleParam.is_read_only = paymentParams.is_read_only
            sampleParam.code = paymentParams.code
            sampleParam.mondatory = paymentParams.mondatory
            sampleParam.group_ord = paymentParams.group_ord
            sampleParam.def_value = paymentParams.def_value
            sampleParam.icon_name = paymentParams.icon_name
            sampleParam.level_position = paymentParams.level_position
            sampleParam.name = paymentParams.name
            sampleParam.hint = paymentParams.hint
            sampleParam.ref_code = paymentParams.ref_code
            for (templateKeyValue in templateResponse.template_details) {
                if (paymentParams.code == templateKeyValue.code) {
                    sampleParam.def_value = templateKeyValue.value.toString()
                    sampleParam.code = templateKeyValue.code!!
                }
            }
            paymentParamsArrayList.add(sampleParam)
        }
        return paymentParamsArrayList
    }

    private fun getPaymentDetails(paymentService: PaymentService): ArrayList<PaymentParams> {
        val inputParams: ArrayList<PaymentParams>
        var paymentParamsArrayList = ArrayList<PaymentParams>()
        try {
            inputParams = dbHelper.getPaymentDetails(paymentService.payment_detail_code!!)
            val hashSet = HashSet<PaymentParams>()
            hashSet.addAll(inputParams)
            inputParams.clear()
            inputParams.addAll(hashSet)
            if (inputParams.size > 0) {
                inputParams[0].ord?.let {
                    inputParams.sortWith { o1, o2 -> o1.ord!!.compareTo(o2.ord!!) }
                }
            }
            paymentParamsArrayList = inputParams
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return paymentParamsArrayList
    }


    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>,
                totalAmount.toString(),
                CurrencyConst.CURRENCY_CHAR_UZS
            ) { cardResponse ->
                cardResponse?.let { card ->
                    selectedCard = card
                    if ((totalAmount.toString()
                            .toBigDecimal() * BigDecimal("100")) <= card.balance.toBigDecimal() && cardResponse.state == "0"
                    ) {
                        binding.btnPaymentList.isEnabled(true)
                    } else {
                        binding.btnPaymentList.isEnabled(false)
                    }
                }
            }
        }
    }
}