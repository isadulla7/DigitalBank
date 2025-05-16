package uz.fido.universaldigital.ui.fragments.services.applications.app_types

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.applications.OrderCardApp
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAllAppBinding
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.ui.fragments.services.applications.AppDetailsDialog
import uz.fido.universaldigital.ui.fragments.services.applications.MainApplicationListFragment
import uz.fido.universaldigital.ui.fragments.services.applications.adapter.AppListAdapter
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AllAppFragment : BaseFragment<FragmentAllAppBinding, UtilsViewModel>(
    FragmentAllAppBinding::inflate, UtilsViewModel::class.java
) {

    private lateinit var applicationAdapter: AppListAdapter

    private var applicationList = ArrayList<OrderCardApp>()
    private var operationType = ""

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
            applicationAdapter = AppListAdapter(applicationList, this@AllAppFragment, requireContext())
            adapter = applicationAdapter
        }
        applicationAdapter.notifyDataSetChanged()
    }

    private fun getMyApplications() {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        showProgress()
        viewModel.getUserAppList(getClientToken()).observe(viewLifecycleOwner) { resource ->
            hideProgress()
            when (resource.status) {
                Status.SUCCESS -> {
                    applicationList.clear()
                    if (resource.data?.product_list != null) {
                        val response = resource.data?.product_list
                        response?.sortByDescending { simpleDateFormat.parse(it.create_date) }
                        response?.forEach { item -> applicationList.add(item) }
                        initList()
                    }
                    binding.emptyView.visibility = if (applicationList.isEmpty()) View.VISIBLE else View.GONE
                }

                Status.ERROR -> {
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