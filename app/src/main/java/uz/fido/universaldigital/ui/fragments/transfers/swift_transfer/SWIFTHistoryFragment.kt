package uz.fido.universaldigital.ui.fragments.transfers.swift_transfer

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.ui.fragments.transfers.swift_transfer.adapter.BankTransferListAdapter
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.swift.SWIFTList
import uz.fido.network.domain.model.swift.SwiftTransferListRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSwiftHistoryBinding
import uz.fido.universaldigital.ui.utils.extensions.limitRange
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class SWIFTHistoryFragment : BaseFragment<FragmentSwiftHistoryBinding, SwiftTransferViewModel>(
    FragmentSwiftHistoryBinding::inflate, SwiftTransferViewModel::class.java
) {
    private var list = ArrayList<SWIFTList>()
    private val df = SimpleDateFormat("dd.MM.yyyy", Locale.US)
    private var dateBegin = ""
    private var currentDate = ""


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
    }

    private fun init() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()
        calendarStart.add(Calendar.YEAR, -1)
        dateBegin = df.format(calendarStart.time)
        currentDate = df.format(calendarEnd.time)
        fetchBankTransferList(dateBegin, currentDate)
        binding.appBar.setOnAdditionalBtnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            builder.setCalendarConstraints(limitRange().build())
            builder.setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
            val picker = builder.build()
            picker.show(childFragmentManager, picker.toString())
            picker.addOnPositiveButtonClickListener {
                dateBegin = Format.getDateFromMilliseconds(it.first!!, "dd.MM.yyyy")
                currentDate = Format.getDateFromMilliseconds(it.second!!, "dd.MM.yyyy")
                fetchBankTransferList(dateBegin, currentDate)
            }
        }

    }

    private fun initList() {
        binding.transferList.apply {
            setHasFixedSize(true)
            val linearLayoutManager = LinearLayoutManager(requireContext())
            layoutManager = linearLayoutManager
            adapter = BankTransferListAdapter(list)
        }
        if (list.isEmpty()) binding.emptyView.visibility = View.VISIBLE
    }


    private fun fetchBankTransferList(dateBegin: String, currentDate: String) {
        val request = SwiftTransferListRequest(
            date_begin = dateBegin, date_end = currentDate
        )
        viewModel.getTransferList(getClientToken(), request).observe(viewLifecycleOwner) { it ->
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    list = java.util.ArrayList()
                    it.data!!.responseBody.data.let {
                        it.let { swiftList ->
                            list.addAll(swiftList)
                        }
                    }
                    initList()
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }

    }

    override fun openNewPage(position: Int) {
        val bundle = Bundle()
        bundle.putInt(
            InitTransferDetailsFragment.BANK_TRANSFER_OPERATION,
            InitTransferDetailsFragment.BANK_OPERATION_CHANGE
        )
        bundle.putSerializable("model", list[position])
        goto(R.id.initTransferDetailsFragment, bundle)
    }


}