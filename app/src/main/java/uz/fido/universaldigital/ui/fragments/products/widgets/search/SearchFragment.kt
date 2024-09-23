package uz.fido.universaldigital.ui.fragments.products.widgets.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentSearchBinding
import uz.fido.universaldigital.ui.fragments.login.pin.PinCodeFragment
import uz.fido.universaldigital.ui.fragments.login.restore_profile.ChangePasswordFragment
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_APP_FUNCTIONALITY
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_PAYMENT
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_PAYMENT_GROUP
import uz.fido.universaldigital.ui.fragments.products.widgets.search.adapter.SearchAdapter
import uz.fido.universaldigital.ui.fragments.products.widgets.search.model.SearchItem
import uz.fido.universaldigital.ui.fragments.transfers.swift_transfer.InitTransferDetailsFragment
import uz.fido.universaldigital.ui.utils.extensions.hideSoftKeyboard
import uz.fido.universaldigital.ui.utils.extensions.showSoftKeyboard
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import java.util.Locale

@AndroidEntryPoint
@SuppressLint("ClickableViewAccessibility")
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>(
    FragmentSearchBinding::inflate, SearchViewModel::class.java
), BaseInterface {

    private lateinit var searchAdapter: SearchAdapter
    private var operationsList = ArrayList<SearchItem>()
    private var list = ArrayList<SearchItem>()

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)

        showKeyBoard()
        initSearchRv()
        initTextChangeListener()
        initClearButtonClickListener()
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch {
            if (list.isEmpty()){
                viewModel.setItemList(requireContext())
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect{
                if (list.isEmpty()){
                    list.addAll(it)
                }


            }
        }

    }


    private fun showKeyBoard() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.etSearch.requestFocus()
            binding.etSearch.showSoftKeyboard()
        }, 100)
    }

    private fun initSearchRv() {
        searchAdapter = SearchAdapter(ArrayList(), this@SearchFragment, "")
        binding.searchList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
    }

    private fun initTextChangeListener() {
        binding.etSearch.doAfterTextChanged {
            it?.let {
                setClearDrawable(it.toString())
                searchByQuery(it.toString())
            }
        }
    }

    private fun initClearButtonClickListener() {
        binding.etSearch.setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (binding.etSearch.text.toString().isNotEmpty()) {
                    if (event.rawX >= binding.etSearch.right - binding.etSearch.compoundDrawables[2].bounds.width()) {
                        binding.etSearch.setText("")
                        return@OnTouchListener true
                    }
                }
            }
            false
        })
        binding.btnCancel.setOnClickListener { pop() }
    }

    private fun setClearDrawable(query: String) {
        if (query.isNotEmpty()) {
            binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_search,
                0,
                R.drawable.ic_clear,
                0
            )
        } else {
            binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0)
        }
    }

    private fun searchByQuery(query: String) {
        var filteredList = ArrayList<SearchItem>()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                withContext(Dispatchers.Main) {
                    searchAdapter = SearchAdapter(ArrayList(), this@SearchFragment, "")
                    binding.searchList.adapter = searchAdapter
                    binding.searchList.apply {
                        binding.emptyView.visibility = View.GONE
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = searchAdapter
                    }
                }
            } else {
               // list = SearchList.searchList
                filteredList = list.filter {
                    it.name.orEmpty().lowercase(Locale.getDefault())
                        .contains(query.lowercase(Locale.getDefault()))
                } as ArrayList<SearchItem>
                withContext(Dispatchers.Main) {
                    binding.emptyView.isVisible = filteredList.isEmpty()
                    operationsList = filteredList
                    searchAdapter = SearchAdapter(filteredList, this@SearchFragment, query)
                    binding.searchList.adapter = searchAdapter
                }
            }
        }
    }

    override fun openSearchItem(searchItem: SearchItem) {
        super<BaseFragment>.openSearchItem(searchItem)
        hideSoftKeyboard()
        when (searchItem.groupName) {
            GROUP_NAME_PAYMENT -> {
                val bundle = Bundle()
                bundle.putSerializable(PaymentFragment.PAYMENT_SERVICE, searchItem.paymentService)
                bundle.putInt(
                    PaymentFragment.PAYMENT_OPERATION,
                    PaymentFragment.PAYMENT_OPERATION_PAYMENT
                )
                goto(R.id.paymentFragment, bundle)
            }

            GROUP_NAME_PAYMENT_GROUP -> {
                val bundle = Bundle()
                bundle.putSerializable("payment_group", searchItem.paymentGroup)
                goto(R.id.paymentListFragment, bundle)
            }

            GROUP_NAME_APP_FUNCTIONALITY -> {
                when (searchItem.id) {
                    "001" -> goto(R.id.addCardFragment)
                    "002" -> goto(R.id.notificationsFragment)
                    "003" -> goto(R.id.menuChatFragment)
                    "004" -> goto(R.id.myCardsListFragment)
                    "005" -> goto(R.id.transferToCardFragment)
                    "006" -> goto(R.id.clientDepositListFragment)
                    "007" -> goto(R.id.clientCreditListFragment)
                    "008" -> {//humo pay
                    }

                    "009" -> goto(R.id.myHomeFragment)
                    "010" -> goto(R.id.qrPaymentFragment)
                    "011" -> goto(R.id.conversionFragment)
                    "012" -> goto(R.id.transferToAccountFragment)
                    "013" -> goto(R.id.swiftTransferFragment)
                    "014" -> goto(R.id.overMyCardsFragment)
                    "015" -> goto(R.id.transferByPhoneFragment)
                    "016" -> goto(R.id.transferByWalletFragment)
                    "018" -> goto(R.id.myDetailsFragment)
                    "019" -> goto(R.id.settingsFragment)
                    "020" -> goto(R.id.securityFragment)
                    "021" -> goto(R.id.aboutBankFragment)
                    "022" -> goto(R.id.mainBranchesFragment)
                    "023" -> goto(R.id.connectWithBankFragment)
                    "024" -> goto(R.id.publicOfferFragment)
                    "025" -> {
                        goto(
                            R.id.pinCodeFragment2,
                            bundleOf(PinCodeFragment.PIN_OPERATION to PinCodeFragment.PIN_OPERATION_CHANGE_PIN)
                        )
                    }

                    "026" -> {
                        goto(
                            R.id.changePasswordFragment,
                            bundleOf(ChangePasswordFragment.CHANGE_PASSWORD_OPERATION to ChangePasswordFragment.CHANGE_PASSWORD)
                        )
                    }

                    "027" -> goto(R.id.myDevicesFragment)
                    "028" -> goto(R.id.changeLanguageFragmentSettings)
                    "029" -> goto(R.id.appThemeFragment)
                    "030" -> goto(R.id.transferToUzsAccountFragment)
                    "031" -> goto(R.id.transferToUsdAccountFragment)
                    "032" -> goto(R.id.transferToBudgetFragment)
                    "033" -> {
                        goto(
                            R.id.initTransferDetailsFragment, bundleOf(
                                InitTransferDetailsFragment.BANK_TRANSFER_OPERATION to InitTransferDetailsFragment.BANK_OPERATION_CREATE,
                                InitTransferDetailsFragment.TRANSFER_CURRENCY to InitTransferDetailsFragment.TRANSFER_DOLLAR
                            )
                        )
                    }

                    "034" -> {
                        goto(
                            R.id.initTransferDetailsFragment, bundleOf(
                                InitTransferDetailsFragment.BANK_TRANSFER_OPERATION to InitTransferDetailsFragment.BANK_OPERATION_CREATE,
                                InitTransferDetailsFragment.TRANSFER_CURRENCY to InitTransferDetailsFragment.TRANSFER_EURO
                            )
                        )
                    }

                    "035" -> goto(R.id.orderCardListFragment)
                    "036" -> goto(R.id.loanGroupListFragment)
                    "037" -> goto(R.id.uzsDepositFragment)
                    "038" -> goto(R.id.openWalletFragment)
                    "039" -> goto(R.id.paymentBranchFragment)
                    "040" -> goto(R.id.mainApplicationListFragment)
                    "041" -> goto(R.id.connectSmsNotificationFragment)
                    "042" -> goto(R.id.mibFragment)
                    "043" -> goto(R.id.moneyTransfersListFragment)
                    "044" -> goto(R.id.autoPaymentFragment)
                }
            }
        }
    }
}