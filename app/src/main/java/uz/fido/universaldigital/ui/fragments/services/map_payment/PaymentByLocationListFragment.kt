package uz.fido.universaldigital.ui.fragments.services.map_payment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.network.domain.model.payment.location.LocalPaymentType
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentPaymentByLocationBinding
import uz.fido.universaldigital.ui.fragments.services.map_payment.adapter.LocalPaymentAdapter
import uz.fido.utils.utility.fragment.goto

@AndroidEntryPoint
class PaymentByLocationListFragment :
    BaseFragment<FragmentPaymentByLocationBinding, PaymentBranchViewModel>(
        FragmentPaymentByLocationBinding::inflate, PaymentBranchViewModel::class.java
    ) {

    private lateinit var newLocalPayment: ArrayList<LocalPayment>
    private val viewModels by activityViewModels<PaymentBranchViewModel>()
    private val localPaymentAdapter by lazy {
        LocalPaymentAdapter(
            ArrayList(), requireContext(), this
        )
    }
    private var localPaymentType: LocalPaymentType? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLocalType()
        initRecyclerView()
    }

    private fun getLocalType() {
        viewModels.localPaymentType.observe(viewLifecycleOwner) {
            localPaymentType = it
            getListLocalPayment(it)
        }
    }

    private fun getListLocalPayment(type: LocalPaymentType) {
        viewModels.localPayment.observe(viewLifecycleOwner) { it ->
            newLocalPayment = arrayListOf()
            it.forEach {
                if (type.id == "-1") {
                    newLocalPayment.add(it)
                } else if (type.id == it.type_id) {
                    newLocalPayment.add(it)
                }
            }
            localPaymentAdapter.setList(newLocalPayment)
            emptyListVisibility()
        }

    }

    private fun emptyListVisibility() {
        binding.layoutEmpty.isVisible = newLocalPayment.isEmpty()
    }

    override fun openLocalPayment(localPayment: LocalPayment) {
        super.openLocalPayment(localPayment)
        goto(
            R.id.localPaymentFragment,
            bundleOf("local_payment" to localPayment, "type" to localPaymentType)
        )

    }

    private fun initRecyclerView() {
        binding.recLocalPayment.apply {
            adapter = localPaymentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}