package uz.fido.universaldigital.ui.fragments.payment.my_home.payment_list

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.fido.network.domain.model.template.Template
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMyHouseMutipleAmountBinding
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.my_home.MyHomeViewModel
import uz.fido.universaldigital.ui.fragments.payment.my_home.adapter.MyHouseAmountAdapter
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import java.math.BigDecimal

@AndroidEntryPoint
class MyHouseMultipleAmountFragment : BaseFragment<FragmentMyHouseMutipleAmountBinding, MyHomeViewModel>(
    FragmentMyHouseMutipleAmountBinding::inflate, MyHomeViewModel::class.java
) {

    private var list = ArrayList<Template>()
    private lateinit var dbHelper: DatabaseHelper
    private var count = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            list = it.serializable<ArrayList<Template>>("list") as ArrayList<Template>
        }
        dbHelper = DatabaseHelper(requireContext())
        initView()
        onClick()
    }

    private fun onClick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnPaymentList.setOnClickListener {
            val newList = ArrayList<Template>()
            list.forEach { item ->
                if (item.amount.toString().isNotEmpty() && item.amount.toString().toBigDecimal() > BigDecimal(499)) {
                    newList.add(item)
                }
            }
            if (newList.size != 0) {
                goto(R.id.myHouseMultipleSelectionFragment, bundleOf("list" to newList))
            }
        }
    }

    private fun initView() {
        binding.rec.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            list.forEach {
                val paymentService = dbHelper.getServiceByContractId(it.service_id.toString())
                if (paymentService != null) {
                    it.payment_service = paymentService
                    it.min_amount = paymentService.min_amount
                    it.max_amount = paymentService.max_amount
                }
            }

            withContext(Dispatchers.Main) {
                binding.rec.adapter = MyHouseAmountAdapter(requireContext(), list) { it, postion ->
                    try {
                        list[postion].check_amount = it.toBigDecimal() > BigDecimal("499")
                        checkBottom()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun checkBottom() {
        count = 0
        list.forEach {
            if (it.check_amount) {
                count += 1
            }
        }
        Log.d("TAG", "checkBottom:${count} ")
        Log.d("TAG", "checkBottom:${list.size} ")
        if (count == list.size) {
            binding.btnPaymentList.isEnabled(true)
        } else binding.btnPaymentList.isEnabled(false)
    }
}