package uz.fido.universaldigital.ui.fragments.monitoring.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.list_bottom_sheet.list
import uz.fido.network.domain.model.monitoring.filter.UserPayedService
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogMonitoringFilterBinding
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.ServiceAllChooseAdapter
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.ServiceAllMonitoringAdapter

class MonitoringFilterDialog(private val serviceList: ArrayList<UserPayedService>,
                             private val onClick:(ArrayList<UserPayedService>)->Unit): DialogFragment(), BaseInterface {

    private lateinit var binding:DialogMonitoringFilterBinding
    private var newList= arrayListOf<UserPayedService>()
    private val serviceAdapter by lazy { ServiceAllChooseAdapter(requireContext(), arrayListOf(),this) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DialogMonitoringFilterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogThemetwo);

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.setAdditionalBtnVisibility(true)
        newList=serviceList
        serviceRecyclerView()
        onClickView()

    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { dismiss() }
        binding.btnNext.setOnClickListener {
            onClick.invoke(newList)
        }
    }

    override fun monitoringPayed(userPayedService: UserPayedService) {
        super.monitoringPayed(userPayedService)
        newList.forEach {
            if (it.service_id==userPayedService.service_id){
                it.service_current = !it.service_current
            }
        }
        serviceAdapter.setList(newList)

    }
    private fun serviceRecyclerView() {
        binding.rec.apply {
            adapter=serviceAdapter
        }
        serviceAdapter.setList(serviceList)
    }


}