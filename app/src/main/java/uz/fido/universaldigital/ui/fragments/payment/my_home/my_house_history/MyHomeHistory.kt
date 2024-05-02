package uz.fido.universaldigital.ui.fragments.payment.my_home.my_house_history

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.monitoring.ListItem
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.monitoring.DateItem
import uz.fido.network.domain.model.monitoring.home.HomeGeneralItem
import uz.fido.network.domain.model.monitoring.home.HomeHistoryRequest
import uz.fido.network.domain.model.monitoring.home.ItemHomeHistory
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMyHouseHistoryBinding
import uz.fido.universaldigital.ui.fragments.payment.my_home.MyHomeViewModel
import uz.fido.universaldigital.ui.fragments.payment.my_home.adapter.HouseHistoryAdapter
import uz.fido.universaldigital.ui.fragments.payment.my_home.dialog.MyHomeHistoryDialog
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format.newDateFormat
import uz.fido.utils.sticky.StickyHeaderDecoration
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MyHomeHistory : BaseFragment<FragmentMyHouseHistoryBinding, MyHomeViewModel>(
    FragmentMyHouseHistoryBinding::inflate,
    MyHomeViewModel::class.java
) {

    private lateinit var myHomeHistoryDialog: MyHomeHistoryDialog
    private lateinit var houseHistoryAdapter: HouseHistoryAdapter
    private lateinit var template: Template

    private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH)
    private var list: ArrayList<ItemHomeHistory> = ArrayList()
    private var totalList: ArrayList<ListItem> = ArrayList()
    private var dateBegin: String = ""
    private var dateEnd: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        template = requireArguments().serializable<Template>("account") as Template
        houseHistoryAdapter = HouseHistoryAdapter(requireContext(), arrayListOf(), template) {
            myHomeHistoryDialog = MyHomeHistoryDialog(it)
            myHomeHistoryDialog.show(childFragmentManager, "")
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
        val calendarEnd = Calendar.getInstance()
        dateEnd = df.format(calendarEnd.time)
        dateBegin = ""
        createMonitoringAdapter()
        getItemHistoryList()
    }

    private fun getItemHistoryList() {
        val skeletonScreen = showSkeleton(
            binding.shimmerView,
            MibDetailsAdapter(requireContext(), this),
            R.layout.shimmer_item_monitoring,
            1
        )
        totalList = arrayListOf()
        viewModel.getHistoryByAcc(
            getClientToken(), HomeHistoryRequest(
                page_number = "1",
                start_date = dateBegin,
                end_date = dateEnd,
                receiver_acc = template.account.toString()
            )
        ).observe(viewLifecycleOwner) { resource ->
            skeletonScreen.hide()
            binding.shimmerView.visibility = View.GONE
            when (resource.status) {
                Status.SUCCESS -> {
                    val response = resource.data!!.transactions
                    list.addAll(response)

                    val groupedHashMap: HashMap<String, MutableList<ItemHomeHistory>> =
                        groupDataIntoHashMap(response)
                    val sortedMap = groupedHashMap.toSortedMap(compareByDescending { it })
                    for (date in sortedMap.keys) {
                        val dateItem = DateItem()
                        dateItem.date = date
                        if (totalList.isEmpty()) {
                            totalList.add(dateItem)
                        } else {
                            Log.d(
                                "TAG", "getItemHistoryList:${
                                    newDateFormat((totalList.last() as HomeGeneralItem).itemHomeHistory!!.create_date.toString()).substring(
                                        0, 10
                                    )
                                } "
                            )
                            Log.d("TAG", "getItemHistoryList:${dateItem.date} ")
                            if (dateItem.date != newDateFormat((totalList.last() as HomeGeneralItem).itemHomeHistory!!.create_date.toString()).substring(
                                    0, 10
                                )
                            ) totalList.add(dateItem)
                        }
                        for (svMonitoringItem in groupedHashMap[date]!!) {
                            val generalItem = HomeGeneralItem()
                            generalItem.itemHomeHistory = svMonitoringItem
                            totalList.add(generalItem)
                        }
                    }

                    houseHistoryAdapter.setListAdapter(totalList)
                    isEmpty(totalList)

                }

                Status.ERROR -> {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    showSnackbar(resource.message.toString())
                }
            }
        }

    }

    private fun isEmpty(totalList: java.util.ArrayList<ListItem>) {
        if (totalList.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
        }
    }

    private fun groupDataIntoHashMap(list: List<ItemHomeHistory>): HashMap<String, MutableList<ItemHomeHistory>> {
        val groupedHashMap: HashMap<String, MutableList<ItemHomeHistory>> = HashMap()
        for (item in list) {
            val hashMapKey: String = newDateFormat(item.create_date!!.substring(0, 10))
            if (groupedHashMap.containsKey(hashMapKey)) {
                groupedHashMap[hashMapKey]!!.add(item)
            } else {
                val list: MutableList<ItemHomeHistory> = java.util.ArrayList()
                list.add(item)
                groupedHashMap[hashMapKey] = list
            }
        }
        return groupedHashMap
    }

    private fun createMonitoringAdapter() {
        binding.rec.apply {
            adapter = houseHistoryAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(StickyHeaderDecoration(houseHistoryAdapter))
        }
    }

}