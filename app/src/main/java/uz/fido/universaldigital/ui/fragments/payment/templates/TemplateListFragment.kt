package uz.fido.universaldigital.ui.fragments.payment.templates

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.template.CreateTemplateRequest
import uz.fido.network.domain.model.template.DeleteTemplateRequest
import uz.fido.network.domain.model.template.GetTemplateListRequest
import uz.fido.network.domain.model.template.GetTemplateRequest
import uz.fido.network.domain.model.template.SetTemplateOrderRequest
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.databinding.FragmentTemplateListBinding
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.templates.adapter.TemplateListAdapter
import uz.fido.universaldigital.ui.fragments.payment.templates.dialog.AddTemplateDialog
import uz.fido.universaldigital.ui.fragments.payment.templates.dialog.TemplateOperationDialog
import uz.fido.universaldigital.ui.fragments.transfers.swift_transfer.InitTransferDetailsFragment
import uz.fido.utils.const.Const
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import uz.fido.utils.utility.view.recycler_view_drag.EditItemTouchHelperCallbackWidgets
import java.text.DecimalFormat


@AndroidEntryPoint
class TemplateListFragment : BaseFragment<FragmentTemplateListBinding, UtilsViewModel>(
    FragmentTemplateListBinding::inflate, UtilsViewModel::class.java
) {

    private var templateList = ArrayList<Template>()
    private var templatesAdapter: TemplateListAdapter? = null

    private lateinit var dialog: TemplateOperationDialog
    private var skeletonScreen: SkeletonScreen? = null
    private var downloadPaymentStart = false
    private var type = ""

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
    }

    private fun init() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        viewModel.templates.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
            } else {
                binding.emptyView.visibility = View.GONE
            }
        }

        binding.createTemplate.setOnClickListener {
            gotoWithSlide(
                R.id.newPaymentGroupListFragment, bundleOf(
                    PaymentFragment.PAYMENT_OPERATION to PaymentFragment.PAYMENT_OPERATION_SAVE_TEMPLATE
                )
            )
        }

        binding.templateList.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            templatesAdapter = TemplateListAdapter(requireContext(), object : BaseInterface {
                override fun openTemplate(item: Template) {
                    getTemplate(item, "open")
                }

                override fun templateOperation(position: Int, item: Template) {
                    super.templateOperation(position, item)
                    dialog = TemplateOperationDialog(object : BaseInterface {
                        @SuppressLint("SyntheticAccessor")
                        override fun selectTemplateType(templateType: String) {
                            if (templateType == Const.TEMPLATE_TYPE_1) {
                                getTemplate(item, "edit")
                            } else {
                                deleteTemplate(position, item.template_id)
                            }
                        }
                    })
                    dialog.show(childFragmentManager, "TAG")
                }

                @SuppressLint("SyntheticAccessor")
                override fun updateTemplateList(list: ArrayList<Template>) {
                    templateList = list
                    setTemplateOrder()
                }
            }, templateList)
            adapter = templatesAdapter
            val callback = EditItemTouchHelperCallbackWidgets(templatesAdapter!!)
            val mItemTouchHelper = ItemTouchHelper(callback)
            mItemTouchHelper.attachToRecyclerView(this)
        }
        getTemplateList()
    }

    private fun setTemplateOrder() {
        val ids = ArrayList<Int>()
        templateList.forEach { ids.add(it.template_id.toInt()) }
        viewModel.setTemplateOrder(
            getClientToken(), SetTemplateOrderRequest(
                template_group_id = PaymentFragment.TemplateGroups.DEFAULT_TEMPLATES.groupId.toString(),
                template_ids = ids
            )
        ).observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                    }

                    Status.ERROR -> {
                    }
                }
            }
        }
    }

//    override fun selectTemplateType(templateType: String) {
//        if (type.isNotEmpty() && type == "S") {
//            setFragmentResult(
//                InitTransferDetailsFragment.BANK_TRANSFER_OPERATION,
//                bundleOf(InitTransferDetailsFragment.BANK_TRANSFER_OPERATION to 2)
//            )
//            findNavController().navigateUp()
//        } else {
//            val bundle = Bundle()
//            bundle.putString("type", templateType)
//            gotoWithSlide(R.id.newPaymentGroupListFragment, bundle)
//        }
//    }

    private var addTemplateDialog: AddTemplateDialog? = null
    private fun renameTemplateNameDialog(item: Template) {
        addTemplateDialog = AddTemplateDialog(item.name.toString(), object : BaseInterface {
            override fun addTemplateName(name: String) {
                addTemplateDialog?.dismiss()
                templateList[templateList.indexOf(item)].name = name
                deleteTemplate(templateList.indexOf(item), item.template_id, true)
            }
        }, getString(R.string.edit_house))
        addTemplateDialog?.show(childFragmentManager, "TAG")
    }


    private fun deleteTemplate(position: Int, templateId: String, addTemplate: Boolean? = false) {
        showProgress()
        viewModel.deleteTemplate(
            getClientToken(), DeleteTemplateRequest(
                templateId
            )
        ).observe(viewLifecycleOwner) { resource ->
            resource?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                        if (addTemplate != null && addTemplate) {
                            saveTemplate(position)
                        } else {
                            hideProgress()
                            try {
                                templateList.removeAt(position)
                                templatesAdapter!!.notifyItemRemoved(position)
                                viewModel.updateTemplates(templateList)
                            }catch (e:Exception){}

                        }
                    }

                    Status.ERROR -> {
                        hideProgress()
                        showSnackbar(resource.message.toString())
                    }
                }
            }
        }
    }


    private fun saveTemplate(position: Int) {
        val item = templateList[position]
        val params = HashMap<String, String>()
        when (item.template_type.toString()) {
            TemplateTypes.TRANSFER_VIA_WALLET.templateType -> {
                params["CARD_NUMBER"] = item.service_type.toString()
            }

            TemplateTypes.TRANSFER_VIA_CARD.templateType -> {
                params["CARD_NUMBER"] = item.service_type.toString()
            }

            else -> {
                params["PHONE_NUMBER"] = item.service_type.toString()
            }
        }
        val model = CreateTemplateRequest(
            name = item.name.toString(),
            template_type = item.template_type.toString(),
            service_type = item.service_type.toString(),
            service_id = item.service_id.toString(),
            template_group_id = PaymentFragment.TemplateGroups.DEFAULT_TEMPLATES.toString(),
            payment_details = params
        )
        viewModel.createTemplate(getClientToken(), model).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    hideProgress()
                    templateList.removeAt(position)
                    templatesAdapter!!.notifyItemRemoved(position)
                    viewModel.updateTemplates(templateList)
                    getTemplateList()
                }

                Status.ERROR -> {
                    hideProgress()
                    showSnackbar(it.message.toString())
                }
            }
        }
    }


    internal fun getTemplate(item: Template, operation: String) {
        showProgress()
        viewModel.getTemplate(
            getClientToken(), GetTemplateRequest(
                item.template_id, "N"
            )
        ).observe(viewLifecycleOwner) { resource ->
            resource?.let { it ->
                when (it.status) {
                    Status.SUCCESS -> {
                        hideProgress()
                        val service =
                            DatabaseHelper(requireContext()).getServiceByContractId(item.service_id.toString())
                        service?.group_code = item.service_group_id.toString()
                        val list = it.data?.template_details
                        val bundle = Bundle()
                        bundle.putSerializable(PaymentFragment.PAYMENT_SERVICE, service)
                        bundle.putSerializable(PaymentFragment.PAYMENT_TEMPLATE_ITEM, item)
                        if (operation == "edit") {
                            bundle.putInt(
                                PaymentFragment.PAYMENT_OPERATION,
                                PaymentFragment.PAYMENT_OPERATION_EDIT_TEMPLATE
                            )
                        } else {
                            bundle.putInt(
                                PaymentFragment.PAYMENT_OPERATION,
                                PaymentFragment.PAYMENT_OPERATION_TEMPLATE
                            )
                        }
                        when (item.template_type) {
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
                                gotoWithSlide(R.id.paymentFragment, bundle)
                            }

                            TemplateTypes.REQUISITES.templateType -> {
                                bundle.putSerializable(
                                    PaymentFragment.PAYMENT_TEMPLATE_KEY_VALUE_LIST, list
                                )
                                list?.forEach {
                                    if (it.code == "RECEIVER_ACCOUNT") {
                                        if (it.value.toString().length != 20) {
                                            gotoWithSlide(
                                                R.id.transferToBudgetFragment, bundle
                                            )
                                        } else {
                                            if (it.value.toString().substring(5, 8) == "000") {
                                                gotoWithSlide(
                                                    R.id.transferToUzsAccountFragment, bundle
                                                )
                                            } else {
                                                gotoWithSlide(
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
                                        if (operation == "edit") {
                                            renameTemplateNameDialog(item)
                                        } else {
                                            gotoWithSlide(
                                                R.id.transferToCardFragment, bundle
                                            )
                                        }
                                    }
                                }
                            }

                            TemplateTypes.TRANSFER_VIA_MOBILE.templateType -> {
                                if (list != null) {
                                    if (list.isNotEmpty()) {
                                        bundle.putString("phone_number", list[0].value.toString())
                                        gotoWithSlide(R.id.transferByPhoneFragment, bundle)
                                    }
                                }
                            }

                            TemplateTypes.TRANSFER_VIA_WALLET.templateType -> {
                                if (list != null) {
                                    if (list.isNotEmpty()) {
                                        bundle.putString("card_number", list[0].value.toString())
                                        gotoWithSlide(R.id.transferByWalletFragment, bundle)
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
                                        bundle.putSerializable("temp_id", item.template_id)
                                        bundle.putInt(
                                            InitTransferDetailsFragment.BANK_TRANSFER_OPERATION,
                                            if (operation == "edit") 2 else 1
                                        )
                                        gotoWithSlide(R.id.initTransferDetailsFragment, bundle)
                                    }
                                }
                            }
                        }
                    }

                    Status.ERROR -> {
                        hideProgress()
                        showSnackbar(resource.message.toString())
                    }
                }
            }
        }
    }


    private fun getTemplateList() {
        viewModel.getTemplateList(
            getClientToken(), GetTemplateListRequest(
                "1"
            )
        ).observe(viewLifecycleOwner) { resource ->
            resource?.let { resource ->
                if (!downloadPaymentStart) {
                    skeletonScreen?.hide()
                }
                when (resource.status) {
                    Status.SUCCESS -> {
                        templateList = resource.data!!.templates
                        templateList.sortBy { item -> item.ord }
                        val tempTemplateList = ArrayList<Template>()
                        if (type.isNotEmpty() && type == "S") {
                            templateList.forEach { item ->
                                if (item.service_group_code == "SWIFT") {
                                    tempTemplateList.add(item)
                                }
                            }
                            viewModel.updateTemplates(tempTemplateList)
                            templatesAdapter!!.setList(tempTemplateList)
                        } else {
                            viewModel.updateTemplates(templateList)
                            templatesAdapter!!.setList(templateList)
                        }

                        if (templateList.isEmpty()) {
                            binding.emptyView.visibility = View.VISIBLE
                        } else {
                            binding.emptyView.visibility = View.GONE
                        }
                    }

                    Status.ERROR -> {
                        showSnackbar(resource.message.toString())
                    }
                }
            }
        }
    }


}