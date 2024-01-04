package uz.fido.universaldigital.ui.fragments.payment.my_home.payment_list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardInfoRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.payment.CreatePaymentRequest
import uz.fido.network.domain.model.payment.PreparePaymentRequest
import uz.fido.network.domain.model.payment.PreparePaymentResponse
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMyHouseMultiplePayBinding
import uz.fido.universaldigital.ui.fragments.payment.my_home.MyHomeViewModel
import uz.fido.universaldigital.ui.fragments.payment.my_home.adapter.MyHousePayAdapter
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MyHouseMultiplePayFragment : BaseFragment<FragmentMyHouseMultiplePayBinding, MyHomeViewModel>(
    FragmentMyHouseMultiplePayBinding::inflate, MyHomeViewModel::class.java
) {
    private var list = ArrayList<Template>()
    private var selectedCard: CardResponse? = null
    private var myHousePayAdapter: MyHousePayAdapter? = null
    private var current = false
    private val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            list = it.serializable<ArrayList<Template>>("list") as ArrayList<Template>
            selectedCard = it.serializable<CardResponse>("object") as CardResponse?
        }

        init()
        onCLickView()

    }


    private fun onCLickView() {
        binding.appBar.setOnBackButtonClickListener {
            if (current)
                Navigation.findNavController(requireView())
                    .popBackStack(R.id.serviceFragment, false)
        }
        binding.btnEnter.setOnClickListener {
            if (current)
                goto(
                    R.id.basicSuccessFragment,
                    bundleOf(Const.OPERATION to BasicSuccessFragment.MY_HOME_PAYMENT_LIST)
                )
        }
    }

    private fun init() {
        binding.rec.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            myHousePayAdapter = MyHousePayAdapter(requireContext(), list)
            adapter = myHousePayAdapter
        }

        preparePayment(0)
    }

    private fun preparePayment(position: Int) {
        val item = list[position]
        if (item.payment_service == null) {
            list[position].error_text = getString(R.string.service_not_available)
            myHousePayAdapter?.notifyDataSetChanged()
            preparePayment(position + 1)
            return
        }
        var paymentParamsArrayList = item.payment_params
        val params = HashMap<String, String>()
        paymentParamsArrayList!!.forEach {
            if (it.is_required.equals("Y") && it.code.isNotEmpty()) {
                params[it.code] = it.def_value
            }
            if (it.code == "AMOUNT") {
                params[it.code] = Format.formatAmountToTiyn(item.amount!!)
            }
        }

        val levelPosition: String =
            if (paymentParamsArrayList[0].level_position.equals("-1")) {
                "2"
            } else {
                paymentParamsArrayList[0].level_position!!
            }
        val request = PreparePaymentRequest(
            service_id = item.service_id.toString(),
            payment_detail_code = item.service_group_code.toString(),
            command = item.payment_service!!.payment_type!!.toLowerCase().trim(),
            curr_level_position = levelPosition,
            params = params
        )
        viewModel.preparePaymentRequest(getClientToken(), request).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data as PreparePaymentResponse
                    paymentParamsArrayList = response.service_details
                    item.payment_params = paymentParamsArrayList
                    if (response.level_position == "-1") {
                        Const.request_id = response.request_id.toString()

//                         list[position].payment_success=true
//                         myHousePayAdapter?.notifyDataSetChanged()
//                         if (position!=list.size-1)
//                         preparePayment(position+1) else{
//                             current =true
//                         getCardList()}
                        createPayment(position)
                    } else {
                        preparePayment(position)
                    }
                }

                Status.ERROR -> {
                    list[position].error_text = it.message.toString()
                    myHousePayAdapter?.notifyDataSetChanged()
                    if (position != list.size - 1)
                        preparePayment(position + 1)

                }
            }
        }
    }

    private fun createPayment(position: Int) {
        val item = list[position]
        val paymentParamsArrayList = item.payment_params!!
        val params = HashMap<String, String>()
        paymentParamsArrayList.forEach {
            if (it.code != "CARD_NUMBER") {
                params[it.code] = it.def_value
            }
        }

        val paymentType = item.payment_service!!.payment_type!!.lowercase().trim()
        val model = CreatePaymentRequest(
            service_id = item.payment_service!!.service_id.toString(),
            params = params,
            from_object_id = selectedCard?.object_id.toString(),
            amount = params["AMOUNT"].toString(),
            command = if (selectedCard!!.object_type == "KL") "purse&${paymentType}" else "card&${paymentType}",
            i_request_id = Const.request_id
        )
        val path =
            if (item.payment_service!!.pay_request_method.isNullOrEmpty()) "CREATE_PAYMENT/" else item.payment_service!!.pay_request_method.toString()
        viewModel.createPaymentRequest(path, getClientToken(), model).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.ERROR -> {
                    list[position].error_text = it.message.toString()
                    myHousePayAdapter?.notifyDataSetChanged()
                    if (position != list.size - 1) {
                        preparePayment(position + 1)
                    } else {
                        getCardList()
                        current = true
                    }
                }

                Status.SUCCESS -> {
                    list[position].payment_success = true
                    myHousePayAdapter?.notifyDataSetChanged()
                    if (position != list.size - 1) {
                        preparePayment(position + 1)
                    } else {
                        getCardList()
                        current = true
                    }
                }
            }
        }
    }

    fun getCardList() {
        menuProductsViewModel.getCardListRequest(getClientToken()).observe(viewLifecycleOwner) {
            showProgress()
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { cardListResponse ->
                            if (cardListResponse.objects != null) {
                                val cards = cardListResponse.objects
                                menuProductsViewModel.updateCards(cards!!)
                                for (i in 0 until cards.size) {
                                    getCardInfo(i, cards)
                                }

                            } else {
                                menuProductsViewModel.updateCards(ArrayList())
                            }
                        }
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }

    private fun getCardInfo(
        position: Int, cardList: ArrayList<CardResponse>,
    ) {
        val ids = arrayListOf(cardList[position].object_id)
        val cardInfoRequest = CardInfoRequest(ids)
        menuProductsViewModel.getCardInfoRequest(getClientToken(), cardInfoRequest)
            .observe(viewLifecycleOwner) {
                hideProgress()
                when (it.status) {
                    Status.SUCCESS -> {
                        val response = it.data!!.objects
                        if (response != null && response.size > 0) {
                            cardList[position].apply {
                                balance = response[0].balance
                                processing_server_status =
                                    response[0].state.toString()
                                stateName =
                                    response[0].state_name.toString()
                                owerdraft_limit = response[0].overdraft_limit
                                pin_counter = response[0].pin_counter
                                overdraft_limit = response[0].overdraft_limit
                                object_status = response[0].object_status
                            }
                            menuProductsViewModel.updateCards(cardList)
                        } else {
                            showSnackbar(it.data.toString())
                        }
                    }

                    Status.ERROR -> {
                        showSnackbar(it.message.toString())
                    }
                }
            }
    }
}