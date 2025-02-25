package uz.fido.universaldigital.ui.fragments.products.new_design

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.paperdb.Paper
import kotlinx.coroutines.launch
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.my_house.MyHouseGroup
import uz.fido.network.domain.model.template.GetTemplateListRequest
import uz.fido.network.domain.model.template.GetTemplateRequest
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentMenuNewHomeBinding
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.templates.TemplateTypes
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.ui.fragments.products.adapter.MainMyHouseAdapter
import uz.fido.universaldigital.ui.fragments.products.adapter.NewFastAccessOperationAdapter
import uz.fido.universaldigital.ui.fragments.products.adapter.NewHomeTemplatesAdapter
import uz.fido.universaldigital.ui.fragments.products.dialog.ScanCardAndWalletDialog
import uz.fido.universaldigital.ui.fragments.products.model.FastAccessOperation
import uz.fido.universaldigital.ui.fragments.products.widgets.card_phone.CardNumberDialog
import uz.fido.universaldigital.ui.fragments.transfers.swift_transfer.InitTransferDetailsFragment
import uz.fido.universaldigital.ui.utils.extensions.getFastAccessOperationList
import uz.fido.universaldigital.ui.utils.extensions.getFormattedContact
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.universaldigital.ui.utils.extensions.showSnackbar
import uz.fido.universaldigital.ui.utils.home_utils.DoAfterTextWatcher
import uz.fido.universaldigital.ui.utils.home_utils.applyMask
import uz.fido.universaldigital.ui.utils.home_utils.mobileServiceId
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.user.getClientToken
import uz.scan_card.cardscan.ScanActivity
import java.text.DecimalFormat

abstract class BaseNewHomeFragment : Fragment(), BaseInterface, PermissionInterface {

    lateinit var binding: FragmentMenuNewHomeBinding
    private lateinit var databaseHelper: DatabaseHelper
    val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private val utilsViewModel: UtilsViewModel by activityViewModels()
    private lateinit var dialogCard: CardNumberDialog

    var mask = "#### #### #### ####"
    var typeCurrent = true
    var container: ViewGroup? = null
    private lateinit var operationDialog: ScanCardAndWalletDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuNewHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun initWidgets() {
        cardAndPhoneLayout()
        initFastAccessLayout()
        initHomeTemplates()
        initMyHome()
    }

    private fun cardAndPhoneLayout() {
        binding.btnContact.setOnClickListener {
            if (typeCurrent) {
                operationDialog = ScanCardAndWalletDialog(
                    scanCardClick = {
                        operationDialog.dismiss()
                        openCameraForCardRead()

                    },
                    contactClick = {
//                    dialogCard = CardNumberDialog(onClick = {
//                        goto(R.id.transferToCardFragment, bundleOf(Const.CARD_NUMBER to it.replace(" ", "")))
//                        dialogCard.dismiss()
//                    }
//                )
                        //  dialogCard.show(childFragmentManager, "")
                        operationDialog.dismiss()
                        fetchPhoneNumber()
                    },
                    walletClick = {
                        goto(R.id.transferByWalletFragment)
                        operationDialog.dismiss()
                    })
                operationDialog.show(childFragmentManager, "TAG")


            } else {
                fetchPhoneNumber()
                // goto(R.id.transferByPhoneFragment, bundleOf("contact" to "open", Const.CARD_NUMBER to ""))
            }
        }
        if (typeCurrent) {
            binding.imageType.setImageResource(R.drawable.ic_phone_28)
            binding.btnContact.setImageResource(R.drawable.ic_star_unselected)
            binding.title.setText(R.string.transfer)
            binding.phoneNumberLayout.setHint(R.string.card_or_phone_number)

        } else {
            binding.btnContact.setImageResource(R.drawable.ic_contact)
            binding.imageType.setImageResource(R.drawable.all_cards)
            binding.title.setText(R.string.mobile_network)

            binding.phoneNumberLayout.setHint(R.string.phone_number)
        }
        binding.phoneCard.setOnClickListener {
            if (typeCurrent) {
                typeCurrent = false
                binding.btnContact.setImageResource(R.drawable.ic_contact)
                binding.imageType.setImageResource(R.drawable.all_cards)
                binding.title.setText(R.string.mobile_network)
                binding.etPhoneNumber.setText("+998")
                binding.phoneNumberLayout.setHint(R.string.phone_number)
            } else {
                typeCurrent = true
                binding.title.setText(R.string.transfer)
                binding.imageType.setImageResource(R.drawable.ic_phone_28)
                binding.etPhoneNumber.setText("")
                binding.btnContact.setImageResource(R.drawable.ic_star_unselected)
                binding.phoneNumberLayout.setHint(R.string.card_or_phone_number)
            }
        }
        val maskTextWatcher = object : DoAfterTextWatcher() {

            private var isUpdating = false
            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                s?.let {
                    var text = it.toString()
                    if (typeCurrent) {
                        if (text.length == 3 && text.isNotEmpty()) {
                            if (text.startsWith("998") || text.startsWith("+99")) {
                                mask = "#### ## ### ## ##"
                                if (text.startsWith("998"))
                                    text = "+$text"
                            } else {
                                mask = "#### #### #### ####"
                            }
                        }
                    } else {
                        mask = "#### ## ### ## ##"
                        if (text.length == 3 && !text.contains("+"))
                            text = "+$text"
                    }
                    val cleanText = text.replace(Regex("[^+\\d]"), "")
                    val masked = applyMask(mask, cleanText)
                    isUpdating = true
                    binding.etPhoneNumber.removeTextChangedListener(this)
                    binding.etPhoneNumber.setText(masked)
                    val selectionIndex = if (masked.length > text.length) text.length else masked.length
                    binding.etPhoneNumber.setSelection(selectionIndex)
                    binding.etPhoneNumber.addTextChangedListener(this)
                    isUpdating = false
                    if (typeCurrent) {
                        if (text.startsWith("+998") && text.replace(" ", "").length == 13) {
                            goto(R.id.transferByPhoneFragment, bundleOf(Const.CARD_NUMBER to text.replace(" ", "")))
                            binding.etPhoneNumber.setText("")
                        } else if (text.replace(" ", "").length == 16) {
                            goto(R.id.transferToCardFragment, bundleOf(Const.CARD_NUMBER to text.replace(" ", "")))
                            binding.etPhoneNumber.setText("")
                        }
                    } else if (text.replace(" ", "").length == 13) {
                        mobilePayment(text)
                    }
                }
            }
        }
        binding.etPhoneNumber.addTextChangedListener(maskTextWatcher)
    }

    private fun mobilePayment(text: String) {
        val serviceCode = mobileServiceId(text.replace("+", "").replace(" ", ""))
        if (serviceCode == "error") {
            Toast.makeText(
                requireContext(),
                getString(R.string.wrong_format),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            gotoMobilePayments(serviceCode, text, binding.etPhoneNumber)
        }
    }

    private fun gotoMobilePayments(
        paymentServiceId: String,
        phoneNumber: String,
        editText: EditText
    ) {
        databaseHelper = DatabaseHelper(requireContext())
        val paymentService = databaseHelper.getServiceByContractId(paymentServiceId)
        val bundle = Bundle()
        if (paymentService != null) {
            bundle.putString(
                PaymentFragment.MOBILE_NUMBER,
                phoneNumber.replace(" ", "")
            )
            bundle.putSerializable(PaymentFragment.PAYMENT_SERVICE, paymentService)
            bundle.putInt(
                PaymentFragment.PAYMENT_OPERATION,
                PaymentFragment.PAYMENT_OPERATION_PAYMENT
            )
            bundle.putString("back_type", "payment")
            gotoWithSlide(R.id.paymentFragment, bundle)
            editText.setText("")
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.wrong_format),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initFastAccessLayout() {
        var fastAccessOperations = ArrayList<FastAccessOperation>()
        if (Paper.book()
                .read<ArrayList<FastAccessOperation>>(Const.FAST_ACCESS) == null || Paper.book()
                .read(Const.UPDATE_FAST_ACCESS, true) == true ||
            Paper.book().read(Const.UPDATE_MAIN_WIDGETS, false) == true
        ) {
            fastAccessOperations = getFastAccessOperationList(requireContext())
            Paper.book().write(Const.FAST_ACCESS, fastAccessOperations)
            Paper.book().write(Const.UPDATE_FAST_ACCESS, false)
            Paper.book().write(Const.UPDATE_MAIN_WIDGETS, false)
        } else {
            if (fastAccessOperations.size == 0) {
                val checkList = getFastAccessOperationList(requireContext())
                val list = Paper.book().read<ArrayList<FastAccessOperation>>(Const.FAST_ACCESS)
                list?.forEach {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val newItem = checkList.firstOrNull { item -> item.id == it.id }
                        it.name = newItem?.name ?: it.name
                    }
                    if (it.isVisible) {
                        fastAccessOperations.add(it)
                    }
                }
            }
        }
        binding.rvFastAccess.apply {
            val operationsAdapter = NewFastAccessOperationAdapter(
                requireContext(), this@BaseNewHomeFragment, fastAccessOperations
            )
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = operationsAdapter
        }
        binding.llFastAccess.setOnClickListener { goto(R.id.fastAccessControlFragment) }
    }

    private fun initHomeTemplates() {
        val paymentTemplatesAdapter = NewHomeTemplatesAdapter(this@BaseNewHomeFragment, ArrayList())
        if (getFromPaper(Const.HOME_TEMPLATES_EXPANDED, "N") == "Y") {
            binding.templateExpandable.isExpanded = true
            binding.templatesExpandableHandle.setImageResource(R.drawable.arrow_up_24dp)
        } else {
            binding.templateExpandable.isExpanded = false
            binding.templatesExpandableHandle.setImageResource(R.drawable.ic_arrow_down)
        }
        binding.rvTemplates.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = paymentTemplatesAdapter
        }
        utilsViewModel.templates.observe(viewLifecycleOwner) {
            paymentTemplatesAdapter.setList(it as ArrayList<Template>)
        }
        if (utilsViewModel.templates.value.isNullOrEmpty() || utilsViewModel.shouldTemplateUpdate) {
            fetchTemplateList()
        }
        binding.homeTemplatesParent.setOnClickListener { goto(R.id.templateListFragment) }
        binding.templatesExpandableHandle.setOnClickListener {
            if (binding.templateExpandable.isExpanded) {
                binding.templatesExpandableHandle.setImageResource(R.drawable.ic_arrow_down)
                binding.templateExpandable.collapse()
                saveToPaper(Const.HOME_TEMPLATES_EXPANDED, "N")
            } else {
                binding.templatesExpandableHandle.setImageResource(R.drawable.arrow_up_24dp)
                binding.templateExpandable.expand()
                saveToPaper(Const.HOME_TEMPLATES_EXPANDED, "Y")
            }
        }
    }

    private fun initMyHome() {
        val myHomeAdapter = MainMyHouseAdapter(arrayListOf(), { myHouseGroup ->
            gotoWithSlide(R.id.serviceFragment, bundleOf("home" to myHouseGroup))
        }, {
            goto(R.id.myHomeFragment)
        })
        if (utilsViewModel.myHouse.value.isNullOrEmpty()) {
            fetchMyHouseList()
        }
        utilsViewModel.myHouse.observe(viewLifecycleOwner) {
            myHomeAdapter.setList(it)
        }
        if (getFromPaper(Const.HOME_MY_HOUSE_EXPANDED, "N") == "Y") {
            binding.myHouseExpandable.isExpanded = true
            binding.myHouseExpandableHandle.setImageResource(R.drawable.arrow_up_24dp)
        } else {
            binding.myHouseExpandable.isExpanded = false
            binding.myHouseExpandableHandle.setImageResource(R.drawable.ic_arrow_down)
        }
        binding.homeMyHouseParent.setOnClickListener { goto(R.id.myHomeFragment) }
        binding.myHouseExpandableHandle.setOnClickListener {
            if (binding.myHouseExpandable.isExpanded) {
                binding.myHouseExpandableHandle.setImageResource(R.drawable.ic_arrow_down)
                binding.myHouseExpandable.collapse()
                saveToPaper(Const.HOME_MY_HOUSE_EXPANDED, "N")
            } else {
                binding.myHouseExpandableHandle.setImageResource(R.drawable.arrow_up_24dp)
                binding.myHouseExpandable.expand()
                saveToPaper(Const.HOME_MY_HOUSE_EXPANDED, "Y")
            }
        }
        binding.rvMyHouse.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = myHomeAdapter
        }
    }

    fun fetchMyHouseList() {
        utilsViewModel.getTemplateGroups(getClientToken()).observe(viewLifecycleOwner) { resource ->
            resource?.let { _ ->
                if (resource.status == Status.SUCCESS) {
                    val list = resource.data?.template_groups ?: arrayListOf()
                    val first = MyHouseGroup("0", getString(R.string.add_your_home), 0)
                    list.add(0, first)
                    utilsViewModel.updateMyHouse(list)
                }
            }
        }
    }

    private fun fetchTemplateList() {
        utilsViewModel.getTemplateList(getClientToken(), GetTemplateListRequest("1"))
            .observe(viewLifecycleOwner) { resource ->
                resource?.let { _ ->
                    if (resource.status == Status.SUCCESS) {
                        utilsViewModel.shouldTemplateUpdate = false
                        val list = resource.data?.templates ?: ArrayList()
                        list.add(0, Template())
                        utilsViewModel.updateTemplates(list)
                    }
                }
            }
    }

    override fun openHomeOperation(id: Int) {
        when (id) {
            10 -> {
                uz.fido.utils.log.Logger.writeLog("HomeFragment")
                goto(R.id.myCardsListFragment)
            }

            11 -> goto(R.id.transferToCardFragment)
            121 -> goto(R.id.myHomeFragment)
            13 -> goto(R.id.qrPaymentFragment)
            14 -> /*goto(R.id.conversionFragment)*/ {
                showSnackbar(
                    getString(R.string.service_under_development),
                    title = getString(R.string.info)
                )
            }

            15 -> goto(R.id.transferToAccountFragment)
            16 -> {
//                goto(R.id.swiftTransferFragment)
                showSnackbar(
                    getString(R.string.service_under_development),
                    title = getString(R.string.info)
                )
            }

            17 -> {
//                goto(R.id.swiftTransferFragment)
                showSnackbar(
                    getString(R.string.service_under_development),
                    title = getString(R.string.info)
                )
            }

            18 -> goto(R.id.clientDepositListFragment)
            19 -> goto(R.id.clientCreditListFragment)
            20 -> openPaymentByServiceId("788")
        }
    }

    override fun openTemplate(template: Template) {
        fetchTemplate(template)
    }

    override fun addTemplate() {
        goto(
            R.id.newPaymentGroupListFragment, bundleOf(
                PaymentFragment.PAYMENT_OPERATION to PaymentFragment.PAYMENT_OPERATION_SAVE_TEMPLATE
            )
        )
    }

    private fun fetchTemplate(template: Template) {
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

    private fun openCameraForCardRead() {
        val intent = ScanActivity.buildIntent(
            requireActivity(), true, null, R.string.card_scan_position_card, null, null
        )
        getActivityResult.launch(intent)
    }

    private val getActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val scanResult = ScanActivity.creditCardFromResult(it.data)
                val result = scanResult?.number
                if (result != null) {
                    goto(R.id.transferToCardFragment, bundleOf(Const.CARD_NUMBER to result.replace(" ", "")))
                }
            }
        }

    private fun fetchPhoneNumber() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            ContactsContract.Contacts.CONTENT_URI,
            ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        )
        activityForContacts.launch(intent)
    }

    private val activityForContacts =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { intent ->
                    val contactUri = intent.data as Uri
                    val contactQuery = requireActivity().contentResolver.query(
                        contactUri, null, null, null, null
                    ) as Cursor
                    pickPhoneNumberFromContact(contactQuery)
                }
            }
        }

    private fun pickPhoneNumberFromContact(contactQuery: Cursor?) {
        try {
            val phoneNumber: String
            if (contactQuery != null && contactQuery.moveToFirst()) {
                val numberIndex: Int =
                    contactQuery.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                phoneNumber = contactQuery.getString(numberIndex)
                if (getFormattedContact(phoneNumber).isNotEmpty()) {
                    if (typeCurrent) {
                        goto(R.id.transferByPhoneFragment, bundleOf(Const.CARD_NUMBER to phoneNumber))
                    } else {
                        mobilePayment(phoneNumber)
                    }
                } else {
                    wrongPhoneNumberFormat()
                }
            } else {
                wrongPhoneNumberFormat()
            }
        } catch (exception: Exception) {
            contactQuery?.close()
            wrongPhoneNumberFormat()
        } finally {
            contactQuery?.close()
        }
    }

    private fun wrongPhoneNumberFormat() {
        Toast.makeText(
            requireContext(), getString(uz.fido.utils.R.string.wrong_format), Toast.LENGTH_SHORT
        ).show()
    }

    private fun openPaymentByServiceId(serviceId: String) {
        val service = DatabaseHelper(requireContext()).getServiceByContractId(serviceId)
        val bundle = Bundle()
        bundle.putSerializable(PaymentFragment.PAYMENT_SERVICE, service)
        bundle.putInt(PaymentFragment.PAYMENT_OPERATION, PaymentFragment.PAYMENT_OPERATION_PAYMENT)
        goto(R.id.paymentFragment, bundle)
    }

}