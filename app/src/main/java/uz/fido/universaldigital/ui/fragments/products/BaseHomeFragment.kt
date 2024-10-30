package uz.fido.universaldigital.ui.fragments.products

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import io.paperdb.Paper
import kotlinx.coroutines.launch
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.network.domain.model.deposits.GetDepositListRequest
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.network.domain.model.popular_transfers.PopularTransfers
import uz.fido.network.domain.model.rates.CourseItem
import uz.fido.network.domain.model.rates.GetCurrencyRatesRequest
import uz.fido.network.domain.model.template.GetTemplateListRequest
import uz.fido.network.domain.model.template.GetTemplateRequest
import uz.fido.network.domain.model.template.Template
import uz.fido.network.domain.model.widget.MainWidget
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentMenuHomeBinding
import uz.fido.universaldigital.databinding.LayoutHomeBankProductsBinding
import uz.fido.universaldigital.databinding.LayoutHomeCurrencyRatesBinding
import uz.fido.universaldigital.databinding.LayoutHomeDepositsBinding
import uz.fido.universaldigital.databinding.LayoutHomeFastAccessBinding
import uz.fido.universaldigital.databinding.LayoutHomeTemplatesBinding
import uz.fido.universaldigital.databinding.LayoutHomeTransfersBinding
import uz.fido.universaldigital.databinding.LayoutPhoneCardBinding
import uz.fido.universaldigital.databinding.ViewHomeWidgetSettingsBinding
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.templates.TemplateTypes
import uz.fido.universaldigital.ui.fragments.products.adapter.BankProductsAdapter
import uz.fido.universaldigital.ui.fragments.products.adapter.FastAccessOperationAdapter
import uz.fido.universaldigital.ui.fragments.products.adapter.HomePopularTransferAdapter
import uz.fido.universaldigital.ui.fragments.products.adapter.HomeRatesAdapter
import uz.fido.universaldigital.ui.fragments.products.adapter.HomeTemplatesAdapter
import uz.fido.universaldigital.ui.fragments.products.model.FastAccessOperation
import uz.fido.universaldigital.ui.fragments.products.widgets.bank_products.ForYouOnBoarding
import uz.fido.universaldigital.ui.fragments.products.widgets.card_phone.CardNumberDialog
import uz.fido.universaldigital.ui.fragments.products.widgets.settings.MainWidgetSettingsDialog
import uz.fido.universaldigital.ui.fragments.services.deposit.adapter.DepositAdapter
import uz.fido.universaldigital.ui.fragments.services.deposit.client_deposit.ClientDepositFragment
import uz.fido.universaldigital.ui.fragments.services.loan.loan_client.ClientCreditFragment
import uz.fido.universaldigital.ui.fragments.transfers.swift_transfer.InitTransferDetailsFragment
import uz.fido.universaldigital.ui.utils.extensions.getBankProducts
import uz.fido.universaldigital.ui.utils.extensions.getFastAccessOperationList
import uz.fido.universaldigital.ui.utils.extensions.showSnackbar
import uz.fido.universaldigital.ui.utils.home_utils.DoAfterTextWatcher
import uz.fido.universaldigital.ui.utils.home_utils.applyMask
import uz.fido.universaldigital.ui.utils.home_utils.mobileServiceId
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Command
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.user.getClientToken
import java.text.DecimalFormat

abstract class BaseHomeFragment : Fragment(), BaseInterface, PermissionInterface {

    val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private val utilsViewModel: UtilsViewModel by activityViewModels()
    private lateinit var dialogCard: CardNumberDialog
    var mask = "#### #### #### ####"
    private lateinit var databaseHelper: DatabaseHelper
    var typeCurrent = true
    lateinit var binding: FragmentMenuHomeBinding

    var mainWidgetsList = ArrayList<MainWidget>()
    private var nextPage = false

    var container: ViewGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun initWidgets() {
        if (Const.MAIN_WIDGETS_VERSION > (Paper.book()
                .read<Int>(Const.MAIN_WIDGETS_VERSION_SAVED, 0) ?: 0) || Paper.book()
                .read<java.util.ArrayList<MainWidget>>(Const.MAIN_WIDGETS) == null || Paper.book()
                .read<Boolean>(Const.UPDATE_MAIN_WIDGETS, false) == true
        ) {
            mainWidgetsList = java.util.ArrayList()
            val names = resources.getStringArray(R.array.main_widgets)
            val ids = resources.getIntArray(R.array.main_widgets_ids)
            val visibleIds = resources.getIntArray(R.array.main_widget_visible)
            for (i in names.indices) {
                mainWidgetsList.add(
                    MainWidget(
                        id = ids[i], name = names[i], visibleIds.contains(ids[i]), order = i + 1
                    )
                )
            }
            Paper.book().write(Const.MAIN_WIDGETS, mainWidgetsList)
            Paper.book().write(Const.MAIN_WIDGETS_VERSION_SAVED, Const.MAIN_WIDGETS_VERSION)
        } else {
            if (mainWidgetsList.size == 0) {
                val list = Paper.book().read<java.util.ArrayList<MainWidget>>(Const.MAIN_WIDGETS)
                list?.forEach {
                    if (it.is_visible) {
                        mainWidgetsList.add(it)
                    }
                }
            }
        }
        binding.widgetsLayout.removeAllViews()
        mainWidgetsList.forEach {
            if (it.is_visible) {
                when (it.id) {
                    100 -> initFastAccessLayout()
                    200 -> initBankProductsLayout()
                    300 -> initPopularTransfers()
                    400 -> initHomeTemplates()
                    500 -> initCurrencyRates()
                    700 -> initHomeDeposits()
                    800 -> cardAndPhoneLayout()
                }
            }
        }
        initWidgetSettingsButton()
    }

    private fun cardAndPhoneLayout() {
        val layoutBinding = LayoutPhoneCardBinding.inflate(
            LayoutInflater.from(requireContext()), container, false
        )
        layoutBinding.btnContact.setOnClickListener {
            if (typeCurrent) {
                dialogCard = CardNumberDialog(onClick = {
                    goto(R.id.transferToCardFragment, bundleOf(Const.CARD_NUMBER to it.replace(" ", "")))
                    dialogCard.dismiss()
                })
                dialogCard.show(childFragmentManager, "")
            } else {
                goto(R.id.transferByPhoneFragment, bundleOf("contact" to "open", Const.CARD_NUMBER to ""))
            }
        }

        if (typeCurrent) {
            layoutBinding.imageType.setImageResource(R.drawable.ic_phone_28)
            layoutBinding.btnContact.setImageResource(R.drawable.ic_star_unselected)
            layoutBinding.title.setText(R.string.payments)
            layoutBinding.phoneNumberLayout.setHint(R.string.card_or_phone_number)

        } else {
            layoutBinding.btnContact.setImageResource(R.drawable.ic_contact)
            layoutBinding.imageType.setImageResource(R.drawable.all_cards)
            layoutBinding.title.setText(R.string.mobile_network)
            layoutBinding.phoneNumberLayout.setHint(R.string.phone_number)
        }
        layoutBinding.phoneCard.setOnClickListener {
            if (typeCurrent) {
                typeCurrent = false
                layoutBinding.btnContact.setImageResource(R.drawable.ic_contact)
                layoutBinding.imageType.setImageResource(R.drawable.all_cards)
                layoutBinding.etPhoneNumber.setText("")
                layoutBinding.title.setText(R.string.mobile_network)
                layoutBinding.phoneNumberLayout.setHint(R.string.phone_number)
            } else {
                typeCurrent = true
                layoutBinding.title.setText(R.string.payments)
                layoutBinding.imageType.setImageResource(R.drawable.ic_phone_28)
                layoutBinding.etPhoneNumber.setText("")
                layoutBinding.btnContact.setImageResource(R.drawable.ic_star_unselected)
                layoutBinding.phoneNumberLayout.setHint(R.string.card_or_phone_number)
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
                    layoutBinding.etPhoneNumber.removeTextChangedListener(this)
                    layoutBinding.etPhoneNumber.setText(masked)
                    val selectionIndex = if (masked.length > text.length) text.length else masked.length
                    layoutBinding.etPhoneNumber.setSelection(selectionIndex)
                    layoutBinding.etPhoneNumber.addTextChangedListener(this)
                    isUpdating = false

                    if (typeCurrent) {
                        if (text.startsWith("+998") && text.length == 17) {
                            goto(R.id.transferByPhoneFragment, bundleOf(Const.CARD_NUMBER to text.replace(" ", "")))
                            layoutBinding.etPhoneNumber.setText("")
                        } else if (text.length == 19) {
                            goto(R.id.transferToCardFragment, bundleOf(Const.CARD_NUMBER to text.replace(" ", "")))
                            layoutBinding.etPhoneNumber.setText("")
                        }
                    } else if (text.length == 17) {
                        val serviceCode = mobileServiceId(text.replace("+", "").replace(" ", ""))
                        if (serviceCode == "error") {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.wrong_format),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            gotoMobilePayments(serviceCode, text, layoutBinding.etPhoneNumber)

                        }


                    }
                }
            }
        }

        layoutBinding.etPhoneNumber.addTextChangedListener(maskTextWatcher)
        binding.widgetsLayout.addView(layoutBinding.root)
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
        val layoutBinding = LayoutHomeFastAccessBinding.inflate(
            LayoutInflater.from(requireContext()), container, false
        )
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
        layoutBinding.rvFastAccess.apply {
            val operationsAdapter = FastAccessOperationAdapter(
                requireContext(), this@BaseHomeFragment, fastAccessOperations
            )
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = operationsAdapter
        }
        layoutBinding.llFastAccess.setOnClickListener { goto(R.id.fastAccessControlFragment) }
        binding.widgetsLayout.addView(layoutBinding.root)
    }

    private fun initBankProductsLayout() {
        val layoutBinding = LayoutHomeBankProductsBinding.inflate(
            LayoutInflater.from(requireContext()), container, false
        )
        layoutBinding.rvBankProducts.apply {
            val operationsAdapter = BankProductsAdapter(
                requireContext(), this@BaseHomeFragment, getBankProducts(requireContext())
            )
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = operationsAdapter
        }
        binding.widgetsLayout.addView(layoutBinding.root)
    }

    private fun initCurrencyRates() {
        val layoutBinding = LayoutHomeCurrencyRatesBinding.inflate(
            LayoutInflater.from(requireContext()), container, false
        )
        val currencyRatesAdapter = HomeRatesAdapter(ArrayList())
        layoutBinding.rvCurrencyRates.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = currencyRatesAdapter
        }
        utilsViewModel.currencyRates.observe(viewLifecycleOwner) {
            val homeCurrencyRates = it as ArrayList<CourseItem>
            val filteredList = ArrayList<CourseItem>()
            homeCurrencyRates.forEach { courseItem ->
                if (courseItem.currencyCode == "840") {
                    MenuHomeFragment.sellingRate = courseItem.sellingRate
                    MenuHomeFragment.buyingRate = courseItem.buyingRate
                }
                if (courseItem.quoteCurrency == "000") {
                    if (courseItem.currencyCode == "840" || courseItem.currencyCode == "978" || courseItem.currencyCode == "643") {
                        filteredList.add(courseItem)
                    }
                }
                when (courseItem.currencyCode) {
                    "840" -> courseItem.order = 1
                    "978" -> courseItem.order = 2
                    "643" -> courseItem.order = 3
                }
            }
            filteredList.sortWith { o1, o2 ->
                val or1: Int = o1.order
                val or2: Int = o2.order
                or1.compareTo(or2)
            }
            currencyRatesAdapter.setList(filteredList)
        }
        if (utilsViewModel.currencyRates.value.isNullOrEmpty()) {
            fetchCurrencyRates()
        }
        layoutBinding.fatherCurrencyRates.setOnClickListener { goto(R.id.ratesFragment) }
        binding.widgetsLayout.addView(layoutBinding.root)
    }

    private fun initHomeTemplates() {
        val layoutTemplatesBinding = LayoutHomeTemplatesBinding.inflate(
            LayoutInflater.from(requireContext()), container, false
        )
        val paymentTemplatesAdapter =
            HomeTemplatesAdapter(this@BaseHomeFragment, ArrayList())
        layoutTemplatesBinding.rvTemplates.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = paymentTemplatesAdapter
        }
        utilsViewModel.templates.observe(viewLifecycleOwner) {
            paymentTemplatesAdapter.setList(it as ArrayList<Template>)
        }
        if (utilsViewModel.templates.value.isNullOrEmpty() || utilsViewModel.shouldTemplateUpdate) {
            fetchTemplateList()
        }
        layoutTemplatesBinding.header.setOnClickListener { goto(R.id.templateListFragment) }
        binding.widgetsLayout.addView(layoutTemplatesBinding.root)
    }

    private fun initPopularTransfers() {
        val layoutBinding = LayoutHomeTransfersBinding.inflate(
            LayoutInflater.from(requireContext()), container, false
        )
        val homePopularTransactions = ArrayList<PopularTransfers>()
        val popularTransferAdapter =
            HomePopularTransferAdapter(this@BaseHomeFragment, homePopularTransactions)
        layoutBinding.rvTransfers.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = popularTransferAdapter
        }
        utilsViewModel.popularTransfers.observe(viewLifecycleOwner) {
            popularTransferAdapter.setList(it as ArrayList<PopularTransfers>)
            layoutBinding.llEmptyViewTransfers.isVisible = it.isEmpty()
        }
        if (utilsViewModel.popularTransfers.value.isNullOrEmpty()) {
            fetchPopularTransferList()
        }
        binding.widgetsLayout.addView(layoutBinding.root)
        layoutBinding.allTransfers.setOnClickListener {
            goto(R.id.fragmentPopularTransfers, bundleOf("path" to "home"))
        }
    }

    private fun fetchPopularTransferList() {
        utilsViewModel.getPopularTransferList(getClientToken())
            .observe(viewLifecycleOwner) { resource ->
                resource?.let { _ ->
                    if (resource.status == Status.SUCCESS) {
                        val list = ArrayList<PopularTransfers>()
                        resource.data?.popular_transfers?.let { arrayList ->
                            arrayList.forEach {
                                if (!it.empbossed_name.isNullOrEmpty() && !it.card_number.isNullOrEmpty() && list.size < 12) {
                                    list.add(it)
                                }
                            }
                        }
                        utilsViewModel.updatePopularTransfers(list)
                    }
                }
            }
    }

    private fun initWidgetSettingsButton() {
        val layoutBinding = ViewHomeWidgetSettingsBinding.inflate(
            LayoutInflater.from(requireContext()), container, false
        )
        layoutBinding.widgetSettings.setOnClickListener { openWidgetSettings() }
        binding.widgetsLayout.addView(layoutBinding.root)
    }

    private fun openWidgetSettings() {
        val widgetSettingDialog = MainWidgetSettingsDialog(this)
        widgetSettingDialog.show(childFragmentManager, "")
    }

    private fun initHomeDeposits() {
        val layoutBinding = LayoutHomeDepositsBinding.inflate(
            LayoutInflater.from(requireContext()), container, false
        )
        val snapHelper: SnapHelper = PagerSnapHelper()
        val homeDepositsAdapter = DepositAdapter { deposit ->
            if (deposit.dep_id != 853) {
                gotoWithSlide(
                    R.id.openDepositOferta, bundleOf(
                        "deposit" to deposit,
                        "operation" to "deposit",
                        "isSum" to true
                    )
                )
            } else {
                gotoWithSlide(
                    R.id.openDepositStepFirst, bundleOf(
                        "deposit" to deposit,
                        "operation" to "deposit",
                        "isSum" to true
                    )
                )
            }
        }
        layoutBinding.rvDeposits.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = homeDepositsAdapter
            snapHelper.attachToRecyclerView(this)
        }
        menuProductsViewModel.depositProducts.observe(viewLifecycleOwner) {
            homeDepositsAdapter.submitList(it as ArrayList<Deposit>)
            layoutBinding.llEmptyViewDeposit.isVisible = it.isEmpty()
        }
        if (menuProductsViewModel.depositProducts.value.isNullOrEmpty()) {
            getDeposits()
        }
        layoutBinding.llClientDeposits.setOnClickListener { goto(R.id.uzsDepositFragment) }
        layoutBinding.llEmptyViewDeposit.setOnClickListener { goto(R.id.uzsDepositFragment) }
        binding.widgetsLayout.addView(layoutBinding.root)
    }

    private fun getDeposits() {
        menuProductsViewModel.getDeposits(getClientToken(), GetDepositListRequest("dep"))
            .observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        menuProductsViewModel.updateDepositProducts(
                            it.data?.deposit_types ?: ArrayList()
                        )
                        binding.refreshLayout.finishRefresh()
                    }

                    Status.ERROR -> {
                        binding.refreshLayout.finishRefresh()
                    }
                }
            }
    }

    fun fetchCurrencyRates() {
        utilsViewModel.getCurrencyRates(
            getClientToken(), GetCurrencyRatesRequest(Command.INFO, "all")
        ).observe(viewLifecycleOwner) { resource ->
            resource?.let { it ->
                if (it.status == Status.SUCCESS) {
                    val rates = ArrayList<CourseItem>()
                    (it.data?.currency_rates ?: ArrayList()).forEach {
                        if (it.quoteCurrency == "000") {
                            rates.add(it)
                        }
                    }
                    for (i in 0 until rates.size) {
                        when (rates[i].currencyCode) {
                            "840" -> rates[i].order = 1
                            "978" -> rates[i].order = 2
                            "643" -> rates[i].order = 3
                            "826" -> rates[i].order = 4
                            "756" -> rates[i].order = 5
                            "392" -> rates[i].order = 6
                            else -> rates[i].order = 7
                        }
                    }
                    rates.sortWith { o1, o2 ->
                        val or1: Int = o1.order
                        val or2: Int = o2.order
                        or1.compareTo(or2)
                    }
                    utilsViewModel.updateRates(rates)
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

    override fun openCreditDetails(item: CreditProduct) {
        super.openCreditDetails(item)
        goto(R.id.clientCreditFragment, bundleOf(ClientCreditFragment.CLIENT_CREDIT_MODEL to item))
    }

    override fun openDepositDetails(item: ClientDeposit) {
        super.openDepositDetails(item)
        goto(
            R.id.clientDepositFragment,
            bundleOf(ClientDepositFragment.CLIENT_DEPOSIT_MODEL to item)
        )
    }

    override fun openBankProduct(id: Int) {
        super.openBankProduct(id)
        ForYouOnBoarding(
            id
        ).show(childFragmentManager, "")
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

    override fun openTransferList(item: PopularTransfers) {
        goto(
            if (item.object_type == WALLET) R.id.transferByWalletFragment else R.id.transferToCardFragment,
            bundleOf(
                Const.CARD_NUMBER to item.card_number
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

}