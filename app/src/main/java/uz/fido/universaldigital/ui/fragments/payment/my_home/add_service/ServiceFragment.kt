package uz.fido.universaldigital.ui.fragments.payment.my_home.add_service

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.my_house.MyHouseGroup
import uz.fido.network.domain.model.template.DeleteTemplateRequest
import uz.fido.network.domain.model.template.GetTemplateListRequest
import uz.fido.network.domain.model.template.GetTemplateRequest
import uz.fido.network.domain.model.template.GetTemplateResponse
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAddServiceBinding
import uz.fido.universaldigital.ui.fragments.payment.my_home.MyHomeViewModel
import uz.fido.universaldigital.ui.fragments.payment.my_home.adapter.ServiceAdapter
import uz.fido.universaldigital.ui.fragments.payment.my_home.dialog.MyHouseServiceOperationDialog
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ServiceFragment : BaseFragment<FragmentAddServiceBinding, MyHomeViewModel>
    (FragmentAddServiceBinding::inflate, MyHomeViewModel::class.java) {

    private val serviceAdapter by lazy { ServiceAdapter(requireContext(), arrayListOf(), this) }
    private lateinit var myHouseGroup: MyHouseGroup
    private var templateList = ArrayList<Template>()
    private lateinit var myHouseServiceOperationDialog: MyHouseServiceOperationDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            myHouseGroup = it.getSerializable("home") as MyHouseGroup
            binding.appBar.setTitle(myHouseGroup.name.toString())
        }
        binding.btnPaymentList.isEnabled(true)
        createRecyclerView()
        initList()
        onClickView()
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            templateList = ArrayList()
            val bundle = Bundle()
            bundle.putString("id", myHouseGroup.id.toString())
            bundle.putString("home_name", myHouseGroup.name)
            gotoWithSlide(R.id.newPaymentGroupListFragment, bundle)
        }
        binding.btnPaymentList.setOnClickListener {
            val newList = arrayListOf<Template>()
            templateList.forEach {
                if (it.isCurrent && it.service_state == "A") {
                    newList.add(it)
                }
            }
            goto(R.id.myHouseMultipleAmountFragment, bundleOf("list" to newList))
        }
    }

    private fun createRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
        }
    }

    private fun initList() {
        val skeletonScreen =
            showSkeleton(binding.recyclerView, serviceAdapter, R.layout.shimmer_item_history, 5)
        if (myHouseGroup.id.toString() != "0")
            viewModel.getTemplateList(
                getClientToken(),
                GetTemplateListRequest(myHouseGroup.id.toString())
            )
                .observe(viewLifecycleOwner) { resources ->
                    skeletonScreen.hide()
                    when (resources.status) {
                        Status.SUCCESS -> {
                            templateList = resources.data!!.templates
                            templateList.sortBy { item -> item.ord }
                            serviceAdapter.setList(templateList)
                            toCheckList()
                        }

                        Status.ERROR -> {
                            showSnackbar(resources.message.toString())
                            toCheckList()
                        }
                    }
                }
    }

    override fun myHouseService(template: Template, position: Int) {
        super.myHouseService(template, position)
        getTemplate(template, "details", position)
    }

    override fun myHomeListAdd(position: Int) {
        super.myHomeListAdd(position)
        val item = templateList[position]
        item.isCurrent = !item.isCurrent
        btnVisibility()
        serviceAdapter.notifyDataSetChanged()
    }

    private fun btnVisibility() {
        var current = false
        templateList.forEach {
            if (it.isCurrent) {
                current = true
            }
        }
        binding.btnPaymentList.isVisible = current
        binding.btnContinue.isVisible = !current
    }

    override fun listOperation(position: Int) {
        super.listOperation(position)
        myHouseServiceOperationDialog = MyHouseServiceOperationDialog(templateList[position]) { it ->
            myHouseServiceOperationDialog.dismiss()
            when (it) {
                "payment" -> {
                    getTemplate(templateList[position], "payment", position)
                }

                "monitoring" -> {
                    gotoWithSlide(R.id.myHomeHistory, bundleOf("account" to templateList[position]))
                }

                "delete" -> {
                    showProgress()
                    viewModel.deleteTemplate(
                        getClientToken(),
                        DeleteTemplateRequest(templateList[position].template_id)
                    ).observe(viewLifecycleOwner) {
                        hideProgress()
                        when (it.status) {
                            Status.SUCCESS -> {
                                templateList.removeAt(position)
                                serviceAdapter.setList(templateList)
                            }

                            Status.ERROR -> {
                                showSnackbar(it.message.toString())
                            }
                        }
                    }
                }
            }
        }
        myHouseServiceOperationDialog.show(childFragmentManager, "")
    }

    private fun getTemplate(template: Template, operation: String, position: Int) {
        // showProgress()
        viewModel.getTemplate(
            getClientToken(),
            GetTemplateRequest(
                template.template_id,
                "N"
            )
        ).observe(viewLifecycleOwner) {
            ///   hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    if (operation == "payment") {
                        gotoWithSlide(
                            R.id.myHouseSinglePaymentFragment, bundleOf(
                                MyHouseSinglePaymentFragment.MY_HOUSE_TEMPLATE to template,
                                MyHouseSinglePaymentFragment.MY_HOUSE_TEMPLATE_RESPONSE to it.data,
                                MyHouseSinglePaymentFragment.MY_HOUSE_ITEM to myHouseGroup
                            )
                        )
                    } else if (operation == "details") {
                        val response = it.data as GetTemplateResponse
                        var count = 0
                        for (i in 0 until response.template_details.size) {
                            if (response.template_details[i].is_visible != "N" &&
                                response.template_details[i].name!!.isNotEmpty()
                            ) {
                                count++
                            }
                        }
                        if (count != 0) {
                            gotoWithSlide(
                                R.id.myHouseServicesDetailsFragment, bundleOf(
                                    MyHouseServicesDetailsFragment.ITEM_TEMPLATE to template,
                                    MyHouseServicesDetailsFragment.ITEM_TEMPLATE_RESPONSE to it.data,
                                    MyHouseServicesDetailsFragment.MY_HOUSE_ITEM to myHouseGroup
                                )
                            )
                        } else {
                            listOperation(position)
                        }
                    }


                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun toCheckList() {
        if (templateList.isEmpty()) {
            binding.layoutEmpty.visibility = View.VISIBLE
        }
    }
}