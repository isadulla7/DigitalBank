package uz.fido.universaldigital.ui.fragments.payment.auto_payment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.subscriptions.AutoPayment
import uz.fido.network.domain.model.subscriptions.AutoPaymentRequest
import uz.fido.network.domain.model.subscriptions.DeleteAutoPaymentRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentApWithCalendarBinding
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter.AutoPaymentsAdapter
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.dialog.AutoPaymentOperationDialog
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.utils.const.Const
import uz.fido.utils.libs.calendar_view.EventObjects
import uz.fido.utils.log.Log
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AutoPaymentFragment : BaseFragment<FragmentApWithCalendarBinding, AutoPaymentViewModel>
    (FragmentApWithCalendarBinding::inflate, AutoPaymentViewModel::class.java) {

    private lateinit var autoPaymentAdapter: AutoPaymentsAdapter
    private lateinit var dialog: AutoPaymentOperationDialog
    private var list = ArrayList<AutoPayment>()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClickView()
        initCalendarEvents()
//        initCustomDateEvent()
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            addAutoPayment()
        }
    }

    private fun addAutoPayment() {
        gotoWithSlide(R.id.newPaymentGroupListFragment)
    }

    fun init() {
        autoPaymentAdapter = AutoPaymentsAdapter(list, requireContext()) { postion, type ->
            if (type == "more") {
                dialog = AutoPaymentOperationDialog(list[postion]) { s ->
                    if (s == "delete") {
                        dialog.dismiss()
                        showProgress()
                        viewModel.deleteAutoPayment(
                            getClientToken(),
                            DeleteAutoPaymentRequest(list[postion].id.toString())
                        ).observe(viewLifecycleOwner) {
                            hideProgress()
                            when (it.status) {
                                Status.SUCCESS -> {
                                    list.removeAt(postion)
                                    autoPaymentAdapter.setList(list)
                                    binding.layoutEmpty.isVisible = list.isEmpty()
                                }

                                Status.ERROR -> {
                                    showSnackbar(it.message.toString())
                                }
                            }
                        }
                    } else {
                        dialog.dismiss()
                        editPayment(list[postion])
                    }
                }
                dialog.show(childFragmentManager, "")
            } else {
                gotoWithSlide(R.id.autoPaymentDetailsFragment, bundleOf("item" to list[postion]))
            }
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = autoPaymentAdapter
        }
        getListItem()
    }

    private fun editPayment(autoPayment: AutoPayment) {
        when (autoPayment.type) {
            "D" -> {
                gotoWithSlide(R.id.saveAutoPaymentDayFragment, bundleOf("item" to autoPayment))
            }

            "S" -> gotoWithSlide(
                R.id.saveAutoPaymentSpecialFragment,
                bundleOf("item" to autoPayment)
            )

            else -> {
                gotoWithSlide(R.id.saveAutoPaymentMonthFragment, bundleOf("item" to autoPayment))
            }
        }
    }

    private fun getListItem() {
        val skeletonScreen =
            showSkeleton(binding.recyclerView, autoPaymentAdapter, R.layout.shimmer_item_history)
        viewModel.getAutoPaymentList(
            getClientToken(),
            AutoPaymentRequest(getFromPaper(Const.PAPER_CLIENT_PHONE, ""))
        ).observe(viewLifecycleOwner) {
            skeletonScreen.hide()
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data?.auto_payment_list ?: arrayListOf()
                    list = response
                    binding.layoutEmpty.isVisible = list.isEmpty()
                    autoPaymentAdapter.setList(list)
                    initCustomDateEvent()
                }

                Status.ERROR -> {
                    list = arrayListOf()
                    binding.layoutEmpty.isVisible = list.isEmpty()
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun initCustomDateEvent() {
        val events: MutableList<EventObjects> = java.util.ArrayList<EventObjects>()

        if (list.isNotEmpty()) {
            list.forEach {
                if (!it.selected_days.isNullOrEmpty()) {
                    it.selected_days?.forEach { days ->
                        if (days.isNotEmpty() && days != "0" && days.length == 10) {
                            val date = dateFormat.parse(days)
                            events.add(EventObjects(it.name, date))
                            binding.calendar.addEvents(events)
                        }
                    }
                }
            }
        }
    }

    private fun initCalendarEvents() {
        binding.calendar.setDateSelector { selectedDate ->
            val stringDate = dateFormat.format(selectedDate)
            if (list.isNotEmpty()) {
                val filteredList = list.filter { it.selected_days?.contains(stringDate) == true }
                autoPaymentAdapter.setList(filteredList as ArrayList<AutoPayment>)
            }
        }
        binding.calendar.setMonthChanger { changedMonth -> Log.d("Changed", "month changed $changedMonth") }
    }

}