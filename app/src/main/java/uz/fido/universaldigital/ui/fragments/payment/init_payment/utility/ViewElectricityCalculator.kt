package uz.fido.universaldigital.ui.fragments.payment.init_payment.utility

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ViewElectricityCalculatorBinding
import uz.fido.universaldigital.ui.dialogs.ReferenceDialog
import java.math.BigDecimal
import kotlin.math.roundToLong

class ViewElectricityCalculator(
    context: Context
) : LinearLayoutCompat(context), BaseInterface {

    private lateinit var binding: ViewElectricityCalculatorBinding
    private var indicatorArrayList = ArrayList<AllServiceLists>()
    private var childFragmentManager: FragmentManager? = null
    private var referenceDialog: ReferenceDialog? = null
    private var baseInterface: BaseInterface? = null
    private var counterIndicatorFrom = 0L
    private var counterIndicatorTo = 0L

    companion object {
        const val INDICATOR_HAVE = "1"
        const val INDICATOR_HAVE_NOT = "2"
    }

    fun setValues(
        childFragmentManager: FragmentManager,
        baseInterface: BaseInterface
    ) {
        this.childFragmentManager = childFragmentManager
        this.baseInterface = baseInterface
    }

    fun setCounterIndicators(
        counterIndicatorFrom: PaymentParams,
        counterIndicatorTo: PaymentParams,
    ) {
        if (counterIndicatorFrom.def_value.isNotBlank() && counterIndicatorFrom.def_value != null && counterIndicatorFrom.def_value != "null") {
            this.counterIndicatorFrom = counterIndicatorFrom.def_value.toDouble().roundToLong()
        }
        if (counterIndicatorTo.def_value.isNotBlank() && counterIndicatorTo.def_value != null && counterIndicatorTo.def_value != "null") {
            this.counterIndicatorTo = counterIndicatorTo.def_value.toDouble().roundToLong()
        }
        setCounters()
        binding.tiFrom.hint = counterIndicatorFrom.name
        binding.tiTo.hint = counterIndicatorTo.name
    }

    init {
        initViews()
        setToEditText(indicatorArrayList[1], "")
    }

    private fun initViews() {
        binding = ViewElectricityCalculatorBinding.inflate(LayoutInflater.from(context), this, false)
        collectIndicators()
        binding.etShowAccount.setOnClickListener {
            referenceDialog = ReferenceDialog(this, indicatorArrayList, "SELECT")
            referenceDialog?.show(childFragmentManager!!, "")
        }
        binding.etTo.addTextChangedListener {
            if (binding.etTo.hasFocus()) {
                if (binding.etFrom.text.toString().isEmpty() ||
                    binding.etTo.text.toString().isEmpty()
                ) {
                    return@addTextChangedListener
                }
                if (binding.etFrom.text.toString().toInt() > binding.etTo.text.toString().toInt()) {
                    return@addTextChangedListener
                }
                calculateAmount()
            }
        }
        binding.etFrom.addTextChangedListener {
            if (binding.etTo.text.toString().isEmpty() ||
                binding.etFrom.text.toString().isEmpty()
            ) {
                return@addTextChangedListener
            }
            if (binding.etFrom.text.toString().toInt() > binding.etTo.text.toString().toInt()) {
                return@addTextChangedListener
            }
            calculateAmount()
        }
        addView(binding.root)
    }

    private fun setCounters() {
        if (counterIndicatorFrom != 0L) {
            binding.etFrom.setText(counterIndicatorFrom.toString())
        }
        if (counterIndicatorTo != 0L) {
            binding.etTo.setText(counterIndicatorTo.toString())
        }
    }

    private fun calculateAmount() {
        val range = binding.etTo.text.toString().toLong() - binding.etFrom.text.toString().toLong()
        baseInterface?.sendCalculatorRange(
            range,
            binding.etFrom.text.toString(),
            binding.etTo.text.toString()
        )
    }

    private fun collectIndicators() {
        indicatorArrayList = ArrayList()
        var allServiceLists = AllServiceLists()
        allServiceLists.code = INDICATOR_HAVE
        allServiceLists.name = context.getString(R.string.have)
        indicatorArrayList.add(allServiceLists)
        allServiceLists = AllServiceLists()
        allServiceLists.code = INDICATOR_HAVE_NOT
        allServiceLists.name = context.getString(R.string.no)
        indicatorArrayList.add(allServiceLists)
    }

    private fun setViewsVisibility(visibility: Int) {
        binding.indicatorView.visibility = visibility
    }

    override fun setToEditText(allServiceLists: AllServiceLists, tag: String) {
        referenceDialog?.dismiss()
        binding.etShowAccount.setText(allServiceLists.name)
        when (allServiceLists.code) {
            INDICATOR_HAVE -> {
                setViewsVisibility(View.VISIBLE)
            }

            else -> setViewsVisibility(View.GONE)
        }
    }

    fun setIndicatorMax(indicator: BigDecimal?) {
        if (indicator == BigDecimal.ZERO) {
            binding.etTo.setText("")
        } else {
            if (binding.etFrom.text.toString().isNotEmpty()) {
                val minIndicator = binding.etFrom.text.toString().toBigDecimal()
                val maxIndicator = minIndicator.plus(indicator!!)
                binding.etTo.setText(String.format(maxIndicator.toString()))
            }
        }
    }


}