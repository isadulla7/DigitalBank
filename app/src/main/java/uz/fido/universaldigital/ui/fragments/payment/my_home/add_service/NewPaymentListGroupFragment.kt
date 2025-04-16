package uz.fido.universaldigital.ui.fragments.payment.my_home.add_service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.FragmentListGroupPaymentBinding
import uz.fido.universaldigital.ui.fragments.payment.abc_adapter.MainPaymentsAdapter
import uz.fido.universaldigital.ui.fragments.payment.download_payment.DownloadPayment
import uz.fido.universaldigital.ui.fragments.payment.download_payment.DownloadPaymentInterface
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.payment_list.PaymentListFragment
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop

class NewPaymentListGroupFragment : DownloadPayment(), DownloadPaymentInterface {

    private lateinit var binding: FragmentListGroupPaymentBinding
    private lateinit var menuPaymentsAdapter: MainPaymentsAdapter
    private var templateType: Int? = null
    private var homeName: String? = null
    private var homeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgument()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentListGroupPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun getArgument() {
        arguments?.let {
            homeId = it.getString("id")
            homeName = it.getString("home_name")
            templateType = it.getInt(PaymentFragment.PAYMENT_OPERATION)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (homeName != null) binding.appBar.setTitle(homeName!!) else binding.appBar.setTitle(
            getString(R.string.service_category)
        )
        binding.appBar.setOnBackButtonClickListener { pop() }
        initAdapter()
        setPaymentStatusListener(this)
        checkPaymentForDownload()
        initPaymentListRv()
    }

    private fun initAdapter() {
        menuPaymentsAdapter = MainPaymentsAdapter {
            if (homeId != null) goto(
                R.id.paymentListFragment, bundleOf(
                    Const.OPERATION to "template",
                    PaymentListFragment.PAYMENT_GROUP to it,
                    "home_id" to homeId,
                    "home_name" to homeName
                )
            )
            else {
                if (templateType == PaymentFragment.PAYMENT_OPERATION_SAVE_TEMPLATE) {
                    goto(
                        R.id.paymentListFragment, bundleOf(
                            Const.OPERATION to "template",
                            PaymentListFragment.PAYMENT_GROUP to it,
                        )
                    )
                } else goto(
                    R.id.paymentListFragment, bundleOf(
                        Const.OPERATION to "auto_payment", PaymentListFragment.PAYMENT_GROUP to it
                    )
                )
            }
        }
    }

    private fun initPaymentListRv() {
        binding.payments.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = menuPaymentsAdapter
        }
    }

    private fun drawViews() {
        val paymentGroup = ArrayList<PaymentGroup>()
        paymentGroupsList.forEach { item ->
            if (homeId != null) {
                when (item.group_code?.toInt()) {
                    2, 4, 5, 7, 9 -> paymentGroup.add(item)
                }
            } else {
                if (templateType == PaymentFragment.PAYMENT_OPERATION_SAVE_TEMPLATE) paymentGroup.add(
                    item
                )
                else {
                    if (item.group_code != "1") paymentGroup.add(item)
                }
            }
        }
        menuPaymentsAdapter.submitList(paymentGroup)
    }

    private fun checkPaymentForDownload() {
        if (paymentGroupsList.size == 0) {
            checkForPaymentDownload()
        } else drawViews()
    }

    override fun getMutablePaymentList() {
        super.getMutablePaymentList()
        paymentGroupsList = downloadPaymentViewModel.paymentGroupMutableList.value!!
        drawViews()
    }

    override fun fetchCompleteFromDB() {
        drawViews()
    }

    override fun downloadPaymentSuccess() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            drawViews()
        }
    }
}