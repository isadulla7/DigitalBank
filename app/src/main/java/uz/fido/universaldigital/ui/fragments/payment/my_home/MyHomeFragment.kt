package uz.fido.universaldigital.ui.fragments.payment.my_home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.my_house.MyHouseGroup
import uz.fido.network.domain.model.template.CreateTemplateGroupRequest
import uz.fido.network.domain.model.template.EditTemplateGroupRequest
import uz.fido.network.domain.model.template.GetTemplateListRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentMyHomeBinding
import uz.fido.universaldigital.ui.fragments.payment.download_payment.DownloadPayment
import uz.fido.universaldigital.ui.fragments.payment.my_home.adapter.MyHouseAdapter
import uz.fido.universaldigital.ui.fragments.payment.my_home.dialog.MyHouseAddDialog
import uz.fido.universaldigital.ui.fragments.payment.my_home.dialog.MyHouseOperationDialog
import uz.fido.universaldigital.ui.utils.extensions.hideProgress
import uz.fido.universaldigital.ui.utils.extensions.showProgress
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MyHomeFragment : DownloadPayment(), BaseInterface {

    private lateinit var myHouseAddOperationDialog: MyHouseOperationDialog
    private lateinit var binding: FragmentMyHomeBinding

    private val viewModel by activityViewModels<MyHomeViewModel>()
    private var list = arrayListOf<MyHouseGroup>()

    private val adapterGroup by lazy { MyHouseAdapter(arrayListOf(), requireContext(), this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMyHomeRv()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.btnContinue.setOnClickListener {
            MyHouseAddDialog { name ->
                createMyHome(name)
            }.show(childFragmentManager, "")
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun initMyHomeRv() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterGroup
        }
        getMyHouseList()
    }

    private fun getMyHouseList() {
        val skeletonScreen =
            showSkeleton(binding.recyclerView, adapterGroup, R.layout.shimmer_item_my_home, 4)
        viewModel.getTemplateGroups(getClientToken()).observe(viewLifecycleOwner) { resource ->
            skeletonScreen.hide()
            when (resource.status) {
                Status.SUCCESS -> {
                    val response = resource.data?.template_groups as ArrayList<MyHouseGroup>
                    response.sortBy { it.order }
                    list = response
                    setList(list)

                }

                Status.ERROR -> {
                    ///showSnackbar(it.message.toString())
                    initEmptyView()

                }
            }
        }
    }

    private fun setList(list: java.util.ArrayList<MyHouseGroup>) {
        initEmptyView()
        adapterGroup.setList(list)

    }

    private fun initEmptyView() {
        binding.layoutEmpty.isVisible = list.isEmpty()
    }

    override fun myHouseMoreIcon(myHouseGroup: MyHouseGroup, type: String) {
        super.myHouseMoreIcon(myHouseGroup, type)
        when (type) {
            "more" -> {
                myHouseAddOperationDialog = MyHouseOperationDialog(myHouseGroup) {
                    toCheckOperation(myHouseGroup, it)
                }
                myHouseAddOperationDialog.show(childFragmentManager, "")
            }

            "service" -> {
                gotoWithSlide(R.id.serviceFragment, bundleOf("home" to myHouseGroup))
            }
        }
    }

    private fun toCheckOperation(myHouseGroup: MyHouseGroup, it: String) {
        myHouseAddOperationDialog.dismiss()
        when (it) {
            "edit" -> {
                MyHouseAddDialog(myHouseGroup.name.toString()) {
                    myHouseEdit(myHouseGroup, it)
                }.show(childFragmentManager, "")
            }

            "delete" -> {
                deleteHome(myHouseGroup)
            }
        }
    }

    private fun myHouseEdit(myHouseGroup: MyHouseGroup, newName: String) {
        showProgress(requireActivity())
        viewModel.editTemplateGroup(
            getClientToken(), EditTemplateGroupRequest(
                myHouseGroup.id.toString(), newName, myHouseGroup.order.toString()
            )
        ).observe(viewLifecycleOwner) {
            hideProgress(requireActivity())
            when (it.status) {
                Status.SUCCESS -> {
                    myHouseGroup.name = newName
                    adapterGroup.notifyDataSetChanged()
                }

                Status.ERROR -> {
                    //showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun deleteHome(myHouseGroup: MyHouseGroup) {
        showProgress(requireActivity())
        viewModel.deleteTemplateGroup(
            getClientToken(), GetTemplateListRequest(myHouseGroup.id.toString())
        ).observe(viewLifecycleOwner) {
            hideProgress(requireActivity())
            when (it.status) {
                Status.SUCCESS -> {
                    list.remove(myHouseGroup)
                    setList(list)
                }

                Status.ERROR -> {
                    //   showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun createMyHome(name: String) {
        showProgress(requireActivity())
        viewModel.createTemplateGroup(
            getClientToken(), CreateTemplateGroupRequest(name)
        ).observe(viewLifecycleOwner) {
            hideProgress(requireActivity())
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data!!
                    val myGroup = MyHouseGroup(response.template_group_id, name)
                    gotoWithSlide(R.id.serviceFragment, bundle = bundleOf("home" to myGroup))
                }

                Status.ERROR -> {
                    ///    showSnackbar(it.message.toString())
                }
            }
        }
    }

}