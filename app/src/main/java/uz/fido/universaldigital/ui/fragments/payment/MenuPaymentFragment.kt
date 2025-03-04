package uz.fido.universaldigital.ui.fragments.payment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.template.GetTemplateListRequest
import uz.fido.network.domain.model.template.GetTemplateRequest
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentMenuPaymentsBinding
import uz.fido.universaldigital.ui.fragments.payment.abc_adapter.MainPaymentsAdapter
import uz.fido.universaldigital.ui.fragments.payment.download_payment.DownloadPayment
import uz.fido.universaldigital.ui.fragments.payment.download_payment.DownloadPaymentInterface
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.payment_list.PaymentListFragment
import uz.fido.universaldigital.ui.fragments.payment.templates.TemplateTypes
import uz.fido.universaldigital.ui.fragments.payment.templates.adapter.PaymentTemplatesAdapter
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.ui.fragments.transfers.swift_transfer.InitTransferDetailsFragment
import uz.fido.universaldigital.ui.utils.extensions.showSnackbar
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.user.getClientToken
import java.text.DecimalFormat

@AndroidEntryPoint
class MenuPaymentFragment : DownloadPayment(), DownloadPaymentInterface, BaseInterface {

    private lateinit var paymentTemplatesAdapter: PaymentTemplatesAdapter
    private lateinit var menuPaymentsAdapter: MainPaymentsAdapter
    private lateinit var binding: FragmentMenuPaymentsBinding
    private val utilsViewModel: UtilsViewModel by activityViewModels()
    private var templatesSkeleton: SkeletonScreen? = null
    private var skeletonScreen: SkeletonScreen? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuPaymentsAdapter = MainPaymentsAdapter {
            goto(R.id.paymentListFragment, bundleOf(PaymentListFragment.PAYMENT_GROUP to it))
        }
        paymentTemplatesAdapter = PaymentTemplatesAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuPaymentsBinding.inflate(layoutInflater)
        checkLang()
        setPaymentStatusListener(this)
        initPaymentListRv()
        checkPaymentForDownload()
        initPaymentList()
        initPaymentTemplatesRv()
        initSetOnClickListeners()
        checkForPaymentTemplates()
        return binding.root
    }

    private fun initPaymentList() {
        downloadPaymentViewModel.paymentGroupMutableList.observe(viewLifecycleOwner) {
            skeletonScreen?.hide()
            paymentGroupsList = ArrayList()
            paymentGroupsList.addAll(it)
            menuPaymentsAdapter.submitList(paymentGroupsList)
        }
    }

    private fun checkPaymentForDownload() {
        if (paymentGroupsList.size == 0) {
            checkForPaymentDownload()
        }
    }

    private fun initPaymentListRv() {
        binding.payments.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = menuPaymentsAdapter
        }
    }

    private fun initPaymentTemplatesRv() {
        utilsViewModel.templates.observe(viewLifecycleOwner) {
            templatesSkeleton?.hide()
            paymentTemplatesAdapter.submitList(it as ArrayList<Template>)
        }
        binding.savedPayments.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = paymentTemplatesAdapter
        }
    }

    private fun initSetOnClickListeners() {
        binding.search.setOnClickListener {
            goto(R.id.searchEveryWhereFragment)
        }
        binding.autopayments.setOnClickListener { goto(R.id.autoPaymentFragment) }
        binding.paymentByQr.setOnClickListener { handleCameraPermission() }
        binding.myHome.setOnClickListener {
            goto(R.id.myHomeFragment)
        }
        binding.llTemplates.setOnClickListener {
            goto(R.id.templateListFragment)
        }
        binding.loanRepayment.setOnClickListener {
            openPaymentByServiceId(LOAN_PAYMENT)
        }
        binding.loanIshonch.setOnClickListener {
            openPaymentByServiceId(ISHONCH_SERVICE_ID)
        }
    }

    private fun drawViews() {
        downloadPaymentViewModel.paymentGroupMutableList.postValue(paymentGroupsList)
    }

    override fun fetchCompleteFromDB() {
        skeletonScreen?.hide()
        drawViews()
    }

    override fun downloadPaymentSuccess() {
        if (skeletonScreen != null) {
            skeletonScreen?.hide()
        }
        try {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                skeletonScreen?.hide()
                drawViews()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun downloadPaymentFailure() {
        skeletonScreen?.hide()
    }

    override fun downloadPaymentStart() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            skeletonScreen = showSkeleton(
                binding.payments, menuPaymentsAdapter, R.layout.shimmer_item_payment_group, 9
            )
        }
    }

    private fun checkForPaymentTemplates() {
        fetchTemplateList()
    }

    private fun fetchTemplateList() {
        utilsViewModel.getTemplateList(getClientToken(), GetTemplateListRequest("1"))
            .observe(viewLifecycleOwner) { resource ->
                templatesSkeleton?.hide()
                if (resource.status == Status.SUCCESS) {
                    val templates = java.util.ArrayList<Template>()
                    resource.data?.let { templateResponse ->
                        templates.addAll(templateResponse.templates)
                        templates.add(0, Template())
                    }
                    utilsViewModel.updateTemplates(templates)
                }
            }
    }

    override fun addTemplate() {
        goto(R.id.newPaymentGroupListFragment, bundleOf(PaymentFragment.PAYMENT_OPERATION to PaymentFragment.PAYMENT_OPERATION_SAVE_TEMPLATE))
    }

    override fun openTemplate(template: Template) {
        utilsViewModel.getTemplate(
            getClientToken(), GetTemplateRequest(
                template.template_id, "N"
            )
        ).observe(viewLifecycleOwner) { resource ->
            resource?.let { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        val service =
                            DatabaseHelper(requireContext()).getServiceByContractId(template.service_id.toString())
                        service?.group_code = template.service_group_id.toString()
                        val list = it.data?.template_details
                        val bundle = Bundle()
                        bundle.putSerializable(PaymentFragment.PAYMENT_SERVICE, service)
                        bundle.putSerializable(PaymentFragment.PAYMENT_TEMPLATE_ITEM, template)
                        bundle.putInt(
                            PaymentFragment.PAYMENT_OPERATION,
                            PaymentFragment.PAYMENT_OPERATION_TEMPLATE
                        )

                        when (template.template_type) {
                            TemplateTypes.DEFAULT.templateType -> {
                                list?.forEach {
                                    if (it.code == "AMOUNT") {
                                        val format = DecimalFormat("0")
                                        val amount = format.format(it.value!!.toDouble() / 100)
                                        it.value = amount.toString()
                                    }
                                }
                                bundle.putSerializable(
                                    PaymentFragment.PAYMENT_TEMPLATE_KEY_VALUE_LIST, list
                                )
                                goto(R.id.paymentFragment, bundle)
                            }

                            TemplateTypes.REQUISITES.templateType -> {
                                bundle.putSerializable(
                                    PaymentFragment.PAYMENT_TEMPLATE_KEY_VALUE_LIST, list
                                )
                                list?.forEach {
                                    if (it.code == "RECEIVER_ACCOUNT") {
                                        if (it.value.toString().length != 20) {
                                            goto(
                                                R.id.transferToBudgetFragment, bundle
                                            )
                                        } else {
                                            if (it.value.toString().substring(5, 8) == "000") {
                                                goto(
                                                    R.id.transferToUzsAccountFragment, bundle
                                                )
                                            } else {
                                                goto(
                                                    R.id.transferToUsdAccountFragment, bundle
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            TemplateTypes.TRANSFER_VIA_CARD.templateType -> {
                                if (list != null) {
                                    if (list.isNotEmpty()) {
                                        bundle.putString("card_number", list[0].value.toString())

                                        goto(
                                            R.id.transferToCardFragment, bundle
                                        )
                                    }
                                }
                            }

                            TemplateTypes.TRANSFER_VIA_MOBILE.templateType -> {
                                if (list != null) {
                                    if (list.isNotEmpty()) {
                                        bundle.putString("phone_number", list[0].value.toString())
                                        goto(R.id.transferByPhoneFragment, bundle)
                                    }
                                }
                            }

                            TemplateTypes.TRANSFER_VIA_WALLET.templateType -> {
                                if (list != null) {
                                    if (list.isNotEmpty()) {
                                        bundle.putString("card_number", list[0].value.toString())
                                        goto(R.id.transferByWalletFragment, bundle)
                                    }
                                }
                            }

                            TemplateTypes.SWIFT_TRANSFER.templateType -> {
                                if (list != null) {
                                    if (list.isNotEmpty()) {
                                        var currency = ""
                                        list.forEach {
                                            if (it.code == "CURRENCY_CODE") {
                                                currency = if (it.value == "840") "USD" else "EUR"
                                            }
                                        }
                                        bundle.putString(
                                            InitTransferDetailsFragment.TRANSFER_CURRENCY, currency
                                        )
                                        bundle.putSerializable("details", list)
                                        bundle.putSerializable("temp_id", template.template_id)
                                        bundle.putInt(
                                            InitTransferDetailsFragment.BANK_TRANSFER_OPERATION, 1
                                        )
                                        goto(R.id.initTransferDetailsFragment, bundle)
                                    }
                                }
                            }
                        }
                    }

                    Status.ERROR -> {
                        showSnackbar(resource.message.toString())
                    }
                }
            }
        }
    }

    private fun openPaymentByServiceId(serviceId: String) {
        val service = DatabaseHelper(requireContext()).getServiceByContractId(serviceId)
        val bundle = Bundle()
        bundle.putSerializable(PaymentFragment.PAYMENT_SERVICE, service)
        bundle.putInt(PaymentFragment.PAYMENT_OPERATION, PaymentFragment.PAYMENT_OPERATION_PAYMENT)
        goto(R.id.paymentFragment, bundle)
    }

    private fun handleCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                goto(R.id.qrPaymentFragment)
            }

            else -> {
                cameraPermissionRequestLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private val cameraPermissionRequestLauncher: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            goto(R.id.qrPaymentFragment)
        } else {
            Toast.makeText(
                requireContext(),
                "Go to settings and enable camera permission to use this feature",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val ISHONCH_SERVICE_ID = "788"
        private const val LOAN_PAYMENT = "-2"
    }

}