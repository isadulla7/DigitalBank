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

    private val menuServicesAdapter by lazy {
        MenuServicesAdapter { serviceId -> initServiceItemClickEvent(serviceId) }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        binding.services.apply {
            layoutManager = createGridLayoutManager()
            adapter = menuServicesAdapter
            itemAnimator?.changeDuration = 0
        }
        menuServicesAdapter.submitList(getServiceList())
    }

    private fun createGridLayoutManager(): GridLayoutManager {
        return GridLayoutManager(requireContext(), 6, RecyclerView.VERTICAL, false).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (menuServicesAdapter.getItemViewType(position)) {
                        MenuServicesAdapter.ITEM_TYPE_HEADER -> 6
                        MenuServicesAdapter.ITEM_TYPE_BANK_PRODUCT -> 3
                        MenuServicesAdapter.ITEM_TYPE_SERVICE -> 2
                        else -> 1
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.search.setOnClickListener { goto(R.id.searchEveryWhereFragment) }
    }

    private fun initServiceItemClickEvent(serviceId: Int) {
        when (serviceId) {
            2 -> goto(R.id.orderCardListFragment)
            3 -> goto(R.id.loanGroupListFragment) /*functionInProgress()*/
            4 -> checkIdentificationAndGoto(R.id.mainDepositFragment)
            5 -> goto(R.id.openWalletFragment)
            100 -> goto(R.id.myCardsServiceFragment)
            101 -> checkIdentificationAndGoto(R.id.myDepositsServiceFragment)
            102 -> goto(R.id.myCreditsServiceFragment)
            200 -> /*openPaymentInPlaces()*/functionInProgress()
            201 -> /*checkIdentificationAndGoto(R.id.goalListFragment)*/functionInProgress()
            202 -> goto(R.id.mainApplicationListFragment)
            500 -> goto(R.id.transferToAccountFragment)
            700 -> /*checkIdentificationAndGoto(R.id.mibFragment)*/functionInProgress()
            600 -> goto(R.id.connectSmsNotificationFragment)
            800 ->/* goto(R.id.moneyTransfersListFragment)*/functionInProgress()
            801 -> /*goto(R.id.swiftTransferFragment)*/functionInProgress()
            900 -> goto(R.id.mainBranchesFragment)
        }
    }

    override fun locationPermissionGranted() {
        goto(R.id.paymentBranchFragment)
    }

}