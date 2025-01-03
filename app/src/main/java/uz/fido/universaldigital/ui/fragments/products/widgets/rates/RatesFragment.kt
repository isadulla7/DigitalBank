package uz.fido.universaldigital.ui.fragments.products.widgets.rates

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.rates.CourseItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentCurrencyRatesBinding
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.ui.fragments.products.adapter.HomeRatesAdapter
import uz.fido.universaldigital.ui.fragments.products.adapter.RatesAdapter
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class RatesFragment : BaseSimpleFragment<FragmentCurrencyRatesBinding>(
    FragmentCurrencyRatesBinding::inflate
), BaseInterface {

    private lateinit var currencyRatesAdapter: RatesAdapter

    private val utilsViewModel: UtilsViewModel by activityViewModels()
    private var homeCurrencyRates = ArrayList<CourseItem>()

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initRatesRv()
        getRates()
        setOnClickListener()
    }

    private fun initRatesRv() {
        binding.rvCurrencyRates.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            currencyRatesAdapter = RatesAdapter(ArrayList())
            adapter = currencyRatesAdapter
        }
    }

    private fun getRates() {
        utilsViewModel.currencyRates.observe(viewLifecycleOwner) {
            homeCurrencyRates = it as ArrayList<CourseItem>
            if (homeCurrencyRates.isNotEmpty()) {
                binding.lastUpdateDate.text = getString(R.string.currency_rate_date) + " " + homeCurrencyRates[0].beginDate.take(10)
                for (i in 0 until homeCurrencyRates.size) {
                    when (homeCurrencyRates[i].currencyCode) {
                        "840" -> homeCurrencyRates[i].order = 1
                        "978" -> homeCurrencyRates[i].order = 2
                        "643" -> homeCurrencyRates[i].order = 3
                        "826" -> homeCurrencyRates[i].order = 4
                        "756" -> homeCurrencyRates[i].order = 5
                        "392" -> homeCurrencyRates[i].order = 6
                        else -> homeCurrencyRates[i].order = 7
                    }
                }
                homeCurrencyRates.sortWith { o1, o2 ->
                    val or1: Int = o1.order
                    val or2: Int = o2.order
                    or1.compareTo(or2)
                }
                val newList = ArrayList<CourseItem>()
                homeCurrencyRates.forEach { courseItem ->
                    if (courseItem.quoteCurrency == "000") {
                        newList.add(courseItem)
                        newList.add(courseItem)
                    }
                }
                currencyRatesAdapter.setList(newList)
            }
        }
    }

    private fun setOnClickListener() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

}