package uz.fido.universaldigital.ui.fragments.payment.payment_list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentPaymentListBinding
import uz.fido.universaldigital.ui.fragments.payment.abc_adapter.PaymentListAdapter
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class PaymentListFragment :
    BaseSimpleFragment<FragmentPaymentListBinding>(FragmentPaymentListBinding::inflate) {

    private lateinit var paymentListAdapter: PaymentListAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var paymentGroup: PaymentGroup
    private var homeId: String? = null
    private var homeName: String? = null
    private var operation: String? = null

    companion object {
        const val PAYMENT_GROUP = "payment_group"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentGroup = requireArguments().serializable<PaymentGroup>(PAYMENT_GROUP) as PaymentGroup
        homeId = arguments?.getString("home_id")
        homeName = arguments?.getString("home_name")
        operation = arguments?.getString(Const.OPERATION)
        paymentListAdapter = PaymentListAdapter {
            openPaymentItem(it)
        }
        databaseHelper = DatabaseHelper(requireContext())

    }

    private fun checkHomeId() {
        if (homeId != null || operation != null) {
            binding.phoneNumberLayout.visibility = View.GONE
        }
    }


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        setTitle()
        initPaymentRv()
        initPaymentList()
        initPhoneNumberLayout()
        initSetOnClickListeners()
        initCellularPayment()
        checkHomeId()
    }

    private fun initPaymentRv() {
        binding.paymentList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paymentListAdapter
        }
    }

    private fun initPaymentList() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                val paymentList = paymentGroup.service_list
                paymentList?.sortBy { it.order }
                paymentListAdapter.submitList(paymentList)
            }
        }
    }

    private fun setTitle() {
        paymentGroup.name?.let {
            binding.appBar.setTitle(it)
        }
    }

    private fun initPhoneNumberLayout() {
        if (paymentGroup.group_code == "2") {
            binding.userPhoneNumber.text =
                Format.toPhoneFormat(getFromSecureStore(Const.PAPER_CLIENT_PHONE))
            binding.phoneNumberLayout.visibility = View.VISIBLE
        } else {
            binding.phoneNumberLayout.visibility = View.GONE
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun openPaymentItem(paymentService: PaymentService) {
        val bundle = Bundle()
        bundle.putSerializable(PaymentFragment.PAYMENT_SERVICE, paymentService)
        bundle.putString(PaymentFragment.PAYMENT_HOME_ID, homeId)
        bundle.putString(PaymentFragment.PAYMENT_HOME_NAME, homeName)

        if (operation != null)
            when (operation) {
                "template" -> {
                    bundle.putInt(
                        PaymentFragment.PAYMENT_OPERATION,
                        PaymentFragment.PAYMENT_OPERATION_SAVE_TEMPLATE
                    )
                    bundle.putString("payment_group_name", paymentGroup.name.toString())
                }

                "auto_payment" -> bundle.putInt(
                    PaymentFragment.PAYMENT_OPERATION,
                    PaymentFragment.PAYMENT_OPERATION_AUTO_PAYMENT_ADD
                )
            }
        else bundle.putInt(
            PaymentFragment.PAYMENT_OPERATION,
            PaymentFragment.PAYMENT_OPERATION_PAYMENT
        )
        gotoWithSlide(
            R.id.paymentFragment, bundle
        )
    }

    private fun initCellularPayment() {
        binding.phoneNumberLayout.setOnClickListener {
            when (getFromSecureStore(Const.PAPER_CLIENT_PHONE).substring(3, 5)) {
                "90", "91" -> {
                    gotoMobilePayments("51", databaseHelper)
                }

                "93", "94","50" -> {
                    gotoMobilePayments("53", databaseHelper)
                }

                "99", "95" -> gotoMobilePayments("687", databaseHelper)
                "97", "88" -> gotoMobilePayments("54", databaseHelper)
                "98" -> gotoMobilePayments("52", databaseHelper)
                else -> Toast.makeText(
                    requireContext(),
                    getString(R.string.wrong_format),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun gotoMobilePayments(
        paymentServiceId: String,
        mobileDBHelper: DatabaseHelper
    ) {
        val paymentService = mobileDBHelper.getServiceByContractId(paymentServiceId)
        val bundle = Bundle()
        if (paymentService != null) {
            bundle.putString(
                PaymentFragment.MOBILE_NUMBER,
                getFromSecureStore(Const.PAPER_CLIENT_PHONE)
            )
            bundle.putSerializable(PaymentFragment.PAYMENT_SERVICE, paymentService)
            bundle.putInt(
                PaymentFragment.PAYMENT_OPERATION,
                PaymentFragment.PAYMENT_OPERATION_PAYMENT
            )
            bundle.putString("back_type", "payment")
            gotoWithSlide(R.id.paymentFragment, bundle)
        }
    }

}