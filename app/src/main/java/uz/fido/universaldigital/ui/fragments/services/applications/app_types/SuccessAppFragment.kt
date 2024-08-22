package uz.fido.universaldigital.ui.fragments.services.applications.app_types

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.applications.OrderCardApp
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAllAppBinding
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.ui.fragments.services.applications.AppDetailsDialog
import uz.fido.universaldigital.ui.fragments.services.applications.MainApplicationListFragment
import uz.fido.universaldigital.ui.fragments.services.applications.adapter.AppListAdapter
import uz.fido.utils.const.Const
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class SuccessAppFragment : BaseFragment<FragmentAllAppBinding, UtilsViewModel>(
    FragmentAllAppBinding::inflate, UtilsViewModel::class.java
) {

    private var applicationList = ArrayList<OrderCardApp>()
    val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    private var operationType = ""
    private lateinit var applicationAdapter: AppListAdapter

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        operationType = MainApplicationListFragment.OPERATION
        init()
    }

    private fun init() {
        initList()
        if (applicationList.size == 0) getMyApplications()
    }

    private fun initList() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            applicationAdapter = AppListAdapter(applicationList, this@SuccessAppFragment, requireContext())
            adapter = applicationAdapter
        }
        applicationAdapter.notifyDataSetChanged()
    }

    private fun getMyApplications() {
        val skeletonScreen = showSkeleton(
            binding.recyclerView, applicationAdapter, R.layout.shimmer_item_applications, 5
        )
        viewModel.getUserAppList(getClientToken()).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    skeletonScreen.hide()
                    applicationList.clear()
                    val response = resource.data!!.product_list
                    response?.sortByDescending { df.parse(it.create_date) }
                    response?.forEach { item ->
                        if (item.state_id == 5) if (operationType == Const.ORDER_CARD) {
                            if (item.product == "CARD") {
                                applicationList.add(item)
                            }
                        } else {
                            applicationList.add(item)
                        }

                    }
                    applicationList.forEach { application ->
                        if (application.module_product_code != null) {
                            val str: String = when (application.module_product_code) {
                                "TET_VIRTUAL_CARD" -> getString(R.string.application_for_card)
                                "GL_VIRTUAL_CARD" -> getString(R.string.application_for_card)
                                "IBS_CRM_CREDIT" -> getString(R.string.application_for_credit)
                                else -> {
                                    application.module_product
                                }
                            }
                            application.module_product = str
                        }
                    }
                    initList()
                    binding.emptyView.visibility = if (applicationList.isEmpty()) View.VISIBLE else View.GONE
                }

                Status.ERROR -> {
                    skeletonScreen.hide()
                    binding.emptyView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getApplicationDetails(item: OrderCardApp) {
        val dialog = AppDetailsDialog(item)
        dialog.show(parentFragmentManager, "")
    }

}