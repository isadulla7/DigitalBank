package uz.fido.universaldigital.ui.fragments.services

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentMenuServicesBinding
import uz.fido.universaldigital.ui.fragments.services.adapter.MenuServicesAdapter
import uz.fido.universaldigital.ui.utils.extensions.checkIdentificationAndGoto
import uz.fido.universaldigital.ui.utils.extensions.getServiceList
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.utility.fragment.goto

@AndroidEntryPoint
class MenuServicesFragment :
    BaseSimpleFragment<FragmentMenuServicesBinding>(FragmentMenuServicesBinding::inflate),
    PermissionInterface {

    private lateinit var menuServicesAdapter: MenuServicesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initServicesList()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initServices()
        initSetOnClickListeners()
    }

    private fun initServices() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 6, RecyclerView.VERTICAL, false)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (menuServicesAdapter.getItemViewType(position)) {
                    MenuServicesAdapter.ITEM_TYPE_HEADER -> 6
                    MenuServicesAdapter.ITEM_TYPE_BANK_PRODUCT -> 3
                    MenuServicesAdapter.ITEM_TYPE_SERVICE -> 2
                    else -> 1
                }
            }
        }
        binding.services.apply {
            layoutManager = gridLayoutManager
            adapter = menuServicesAdapter
            itemAnimator?.changeDuration = 0
        }
    }

    private fun initServicesList() {
        menuServicesAdapter = MenuServicesAdapter { serviceId ->
            initServiceItemClickEvent(serviceId)
        }
        menuServicesAdapter.submitList(getServiceList())
    }

    private fun initSetOnClickListeners() {
        binding.search.setOnClickListener {
            goto(R.id.searchEveryWhereFragment)
        }
    }

    private fun initServiceItemClickEvent(serviceId: Int) {
        when (serviceId) {
            2 -> goto(R.id.orderCardListFragment)
            3 -> /*checkIdentificationAndGoto(R.id.loanGroupListFragment)*/ functionInProgress()
            4 -> checkIdentificationAndGoto(R.id.mainDepositFragment)
            5 -> goto(R.id.openWalletFragment)
            100 -> goto(R.id.conversionFragment)
            200 -> /*openPaymentInPlaces()*/functionInProgress()
            201 -> checkIdentificationAndGoto(R.id.goalListFragment)
            202 -> goto(R.id.mainApplicationListFragment)
            500 -> goto(R.id.transferToAccountFragment)
            700 -> checkIdentificationAndGoto(R.id.mibFragment)
            600 -> goto(R.id.connectSmsNotificationFragment)
            800 ->/* goto(R.id.moneyTransfersListFragment)*/functionInProgress()
            801 -> /*goto(R.id.swiftTransferFragment)*/functionInProgress()
            900 -> goto(R.id.mainBranchesFragment)
        }
    }

    private fun openPaymentInPlaces() {
        if (checkForLocationPermissions(this)) {
            goto(R.id.paymentBranchFragment)
        }
    }

    override fun locationPermissionGranted() {
        goto(R.id.paymentBranchFragment)
    }

}