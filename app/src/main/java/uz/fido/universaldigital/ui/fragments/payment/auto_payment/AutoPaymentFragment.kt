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
import uz.fido.universaldigital.ui.utils.calendar_view.EventObjects
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.utils.const.Const
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AutoPaymentFragment : BaseFragment<FragmentApWithCalendarBinding, AutoPaymentViewModel>
    (FragmentApWithCalendarBinding::inflate, AutoPaymentViewModel::class.java) {

    private lateinit var autoPaymentAdapter: AutoPaymentsAdapter
    private lateinit var dialog: AutoPaymentOperationDialog
    private var filteredList = ArrayList<AutoPayment>()
    private var list = ArrayList<AutoPayment>()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.US)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initOnClickListeners()
        initCalendarEvents()
    }

    private fun initOnClickListeners() {
        binding.back.setOnClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            addAutoPayment()
        }
        binding.todayDate.setOnClickListener {
            autoPaymentAdapter.setList(list)
            binding.layoutEmpty.isVisible = list.isEmpty()
        }
    }

    private fun addAutoPayment() {
        gotoWithSlide(R.id.newPaymentGroupListFragment)
    }

    private fun init() {
        val calendar = Calendar.getInstance()
        binding.todayDate.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        autoPaymentAdapter = AutoPaymentsAdapter(filteredList, requireContext()) { postion, type ->
            if (type == "more") {
                dialog = AutoPaymentOperationDialog(filteredList[postion]) { s ->
                    if (s == "delete") {
                        dialog.dismiss()
                        showProgress()
                        viewModel.deleteAutoPayment(
                            getClientToken(),
                            DeleteAutoPaymentRequest(filteredList[postion].id.toString())
                        ).observe(viewLifecycleOwner) {
                            hideProgress()
                            when (it.status) {
                                Status.SUCCESS -> {
                                    filteredList.removeAt(postion)
                                    autoPaymentAdapter.setList(filteredList)
                                    binding.layoutEmpty.isVisible = filteredList.isEmpty()
                                    getListItem()
                                }

                                Status.ERROR -> {
                                    showSnackbar(it.message.toString())
                                }
                            }
                        }
                    } else {
                        dialog.dismiss()
                        editPayment(filteredList[postion])
                    }
                }
                dialog.show(childFragmentManager, "")
            } else {
                gotoWithSlide(R.id.autoPaymentDetailsFragment, bundleOf("item" to filteredList[postion]))
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
        val skeletonScreen = showSkeleton(binding.recyclerView, autoPaymentAdapter, R.layout.shimmer_item_history)
        viewModel.getAutoPaymentList(getClientToken(), AutoPaymentRequest(getFromSecureStore(Const.PAPER_CLIENT_PHONE, ""))).observe(viewLifecycleOwner) {
            skeletonScreen.hide()
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data?.auto_payment_list ?: arrayListOf()
                    list = response
                    filteredList = response
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
        binding.calendar.clearEvents()
        val calendar = Calendar.getInstance()
        val events: MutableList<EventObjects> = java.util.ArrayList<EventObjects>()
        val today = Calendar.getInstance().time
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = calendar.time
        if (list.isNotEmpty()) {
            list.forEach { autoPayment ->
                when (autoPayment.type) {
                    "S" -> {
                        if (!autoPayment.selected_days.isNullOrEmpty()) {
                            autoPayment.selected_days?.forEach { days ->
                                if (days.isNotEmpty() && days != "0" && days.length == 10) {
                                    val date = dateFormat.parse(days)
                                    if (date != null) {
                                        if (date >= yesterday) {
                                            events.add(EventObjects(autoPayment.name, date))
                                            binding.calendar.addEvents(events)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "M" -> {
                        if (autoPayment.months.isNotEmpty()) {
                            autoPayment.months.forEach { month ->
                                if (month != 0 && autoPayment.days.isNotEmpty()) {
                                    val calendar = Calendar.getInstance()
                                    calendar.set(Calendar.MONTH, month - 1)
                                    calendar.set(Calendar.DAY_OF_MONTH, autoPayment.days.first())
                                    if (calendar.time >= today) {
                                        events.add(EventObjects(autoPayment.name, calendar.time))
                                        binding.calendar.addEvents(events)
                                    }
                                }
                            }
                            autoPayment.months.forEach { month ->
                                if (month != 0 && autoPayment.days.isNotEmpty()) {
                                    val calendar = Calendar.getInstance()
                                    calendar.add(Calendar.YEAR, 1)
                                    calendar.set(Calendar.MONTH, month - 1)
                                    calendar.set(Calendar.DAY_OF_MONTH, autoPayment.days.first())
                                    if (calendar.time >= today) {
                                        events.add(EventObjects(autoPayment.name, calendar.time))
                                        binding.calendar.addEvents(events)
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initCalendarEvents() {
        binding.calendar.setDateSelector { selectedDate ->
            val stringDate = dateFormat.format(selectedDate)
            val calendar = Calendar.getInstance()
            calendar.time = selectedDate
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH) + 1
            if (list.isNotEmpty()) {
                val specificDays = list.filter { it.selected_days?.contains(stringDate) == true }
                val months = list.filter {
                    it.months.contains(month) && it.days.contains(day)
                }
                val total = specificDays + months
                autoPaymentAdapter.setList(total as ArrayList<AutoPayment>)
                binding.layoutEmpty.isVisible = total.isEmpty()
                filteredList = total
            }
        }
    }

}