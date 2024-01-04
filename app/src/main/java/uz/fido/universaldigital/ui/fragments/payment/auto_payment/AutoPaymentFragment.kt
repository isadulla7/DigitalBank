package uz.fido.universaldigital.ui.fragments.payment.auto_payment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.subscriptions.AutoPayment
import uz.fido.network.domain.model.subscriptions.AutoPaymentRequest
import uz.fido.network.domain.model.subscriptions.DeleteAutoPaymentRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAutoPaymentBinding
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.adapter.AutoPaymentsAdapter
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.dialog.AutoPaymentOperationDialog
import uz.fido.utils.const.Const
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class AutoPaymentFragment : BaseFragment<FragmentAutoPaymentBinding, AutoPaymentViewModel>
    (FragmentAutoPaymentBinding::inflate, AutoPaymentViewModel::class.java) {

    private lateinit var autoPaymentAdapter: AutoPaymentsAdapter
    private lateinit var dialog: AutoPaymentOperationDialog
    private var list = ArrayList<AutoPayment>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        onClickView()
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

    fun init(){
        autoPaymentAdapter= AutoPaymentsAdapter(list,requireContext()){postion,type->
          if(type=="more"){
           dialog=AutoPaymentOperationDialog(list[postion]){
               if (it=="delete"){
                   dialog.dismiss()
                   showProgress()
                   viewModel.deleteAutoPayment(getClientToken(),
                       DeleteAutoPaymentRequest(list[postion].id.toString())).observe(viewLifecycleOwner){
                       hideProgress()
                       when(it.status){
                           Status.SUCCESS->{
                               list.removeAt(postion)
                               autoPaymentAdapter.setList(list)
                               emptyView()
                           }
                           Status.ERROR->{showSnackbar(it.message.toString())}
                       }
                   }
               }else{
                   dialog.dismiss()
                   editPayment(list[postion])
               }
           }
              dialog.show(childFragmentManager,"")
          }else{
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
        when(autoPayment.type){
            "D"->{gotoWithSlide(R.id.saveAutoPaymentDayFragment, bundleOf("item" to autoPayment))}
            "S" ->gotoWithSlide(R.id.saveAutoPaymentSpecialFragment, bundleOf("item" to autoPayment))
            else->{gotoWithSlide(R.id.saveAutoPaymentMonthFragment, bundleOf("item" to autoPayment))}
        }
    }

    private fun getListItem() {
        val skeletonScreen = showSkeleton(binding.recyclerView, autoPaymentAdapter, R.layout.shimmer_item_history)
        viewModel.getAutoPaymentList(getClientToken(),
            AutoPaymentRequest(Paper.book().read(Const.PAPER_CLIENT_PHONE, ""))).observe(viewLifecycleOwner){
              skeletonScreen.hide()
            when(it.status){
                Status.SUCCESS->{
                    val response=it.data?.auto_payment_list?: arrayListOf()
                    list=response
                    emptyView()
                    autoPaymentAdapter.setList(list)
                }
                Status.ERROR->{
                    list= arrayListOf()
                    emptyView()
                    showSnackbar(it.message.toString())
                }
            }
        }
    }
    fun emptyView(){
        if (list.isEmpty()){
            binding.layoutEmpty.visibility=View.VISIBLE
        }
    }
}