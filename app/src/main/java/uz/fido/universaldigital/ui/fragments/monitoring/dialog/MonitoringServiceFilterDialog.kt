package uz.fido.universaldigital.ui.fragments.monitoring.dialog

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.monitoring.filter.UserPayedService
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import uz.fido.network.domain.model.payment.local_history.LocalMonitoringRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogMonitoringFilterBinding
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.ServiceFilterAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.all_card.LocalMonitoringViewModel
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MonitoringServiceFilterDialog(
    private val serviceId: String, val userPayedService: UserPayedService,private val onClick: (ArrayList<LocalMonitoring>) -> Unit
) : DialogFragment(), BaseInterface, (LocalMonitoring) -> Unit {

    private lateinit var binding: DialogMonitoringFilterBinding
    private val serviceAdapter by lazy {
        ServiceFilterAdapter(
            requireContext(), arrayListOf(), this
        )
    }
    private val localMonitoringViewModel by activityViewModels<LocalMonitoringViewModel>()
    private var dateBegin: String? = null
    private var dateEnd: String? = null
    private var currentDate: String? = null
    private var localMonitoringList = arrayListOf<LocalMonitoring>()
    private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DialogMonitoringFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogThemetwo);

    }
    private fun getSearchList() {
        binding.search.addTextChangedListener {local->
            if (local!!.length>1){
                val arrayList= arrayListOf<LocalMonitoring>()
                val list= localMonitoringList.filter { it.partner_obj.toLowerCase().startsWith(local.toString().toLowerCase()) }
               arrayList.addAll(list)
                serviceAdapter.setList(arrayList)
            }else serviceAdapter.setList(localMonitoringList)
        }
    }
    private fun init() {
        val calendarStart = Calendar.getInstance()
        val calendarEnd = Calendar.getInstance()
        currentDate = df.format(calendarEnd.time)
        calendarStart[Calendar.DAY_OF_YEAR] = 1
        dateBegin = df.format(calendarStart.time)
        dateEnd = df.format(calendarEnd.time)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.setAdditionalBtnVisibility(true)
        init()
        serviceRecyclerView()
        checkBox()
        if (localMonitoringList.isEmpty()){
        fetchLocalMonitoring(1)
        }
        onClickView()
        getSearchList()
    }

    private fun checkBox() {
        binding.checkBox.setOnClickListener {
            if (binding.checkBox.isChecked){
                localMonitoringList.forEach {
                    it.isChecked=true
                }
                serviceAdapter.setList(localMonitoringList)
            }else{
                localMonitoringList.forEach {
                    it.isChecked=false
                }
                serviceAdapter.setList(localMonitoringList)
            }
        }
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { dismiss() }
        binding.btnNext.setOnClickListener {
            val newList = arrayListOf<LocalMonitoring>()
            localMonitoringList.forEach {
                if (it.isChecked) newList.add(it)
            }
            onClick.invoke(newList)
        }

    }

    private fun fetchLocalMonitoring(page: Int) {
        val model = LocalMonitoringRequest(
            page_item_size = "20",
            page_number = page.toString(),
            start_date = dateBegin!!,
            end_date = dateEnd!!,
            service_id = serviceId
        )
        val skeletonScreen =
            showSkeleton(binding.rec, serviceAdapter, R.layout.shimmer_item_monitoring, 1)
        localMonitoringViewModel.getLocalMonitoring(getClientToken(), model)
            .observe(viewLifecycleOwner) {
                Handler().postDelayed({ skeletonScreen.hide() }, 500)
                when (it.status) {
                    Status.SUCCESS -> {
                        val list=it.data?.local_transactions?: arrayListOf()
                        list.distinctBy { it.partner_obj }.forEach { localMonitoring ->
                            if (localMonitoring.tran_type == "debit") localMonitoringList.add(localMonitoring)
                        }
                        val keysOfB = userPayedService.list.map { it.request_id }
                        localMonitoringList.removeAll { it.request_id in keysOfB }
                        userPayedService.list.forEach {
                            it.isChecked=true
                        }
                        localMonitoringList.addAll(index = 0,userPayedService.list)

                        if (localMonitoringList.size == 0) {
                            Handler().postDelayed({ binding.layoutEmpty.visibility = View.VISIBLE }, 500) }
                        serviceAdapter.setList(localMonitoringList)
                        if (localMonitoringList.isEmpty()){
                            binding.checkBox.visibility=View.GONE
                        }

                    }

                    Status.ERROR -> {
                        binding.layoutEmpty.visibility = View.VISIBLE
                    }
                }
            }
    }


    private fun serviceRecyclerView() {
        binding.rec.apply {
            adapter = serviceAdapter
        }
    }

    override fun invoke(localMonitoring: LocalMonitoring) {
        localMonitoringList.forEach {
            if (it.request_id == localMonitoring.request_id) {
                it.isChecked = !it.isChecked
            }
        }
        serviceAdapter.setList(localMonitoringList)
    }


}