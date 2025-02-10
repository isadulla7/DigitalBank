package uz.fido.universaldigital.ui.fragments.monitoring.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.functions.BiFunction
import uz.fido.universaldigital.databinding.DialogMonitoringAmountBinding
import java.math.BigDecimal


class MonitoringAmountDialog(private val onClick:(String,String)->Unit): BottomSheetDialogFragment(){

  private lateinit var binding:DialogMonitoringAmountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DialogMonitoringAmountBinding.inflate(inflater,container,false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rxBinding()

        binding.btnContinue.setOnClickListener {
           onClick.invoke(binding.etAmountMin.text.toString(),binding.etMaxAmount.text.toString())
        }
    }

    private fun rxBinding() {
        io.reactivex.rxjava3.core.Observable.combineLatest(
            binding.etAmountMin.textChanges(),
            binding.etMaxAmount.textChanges(),
            BiFunction(this::isCorrent)
        ).doOnNext {
            binding.btnContinue.isEnabled(it)
        }
            .subscribe()
    }

    private fun isCorrent(min: CharSequence, max: CharSequence): Boolean {

        if (min.isNotEmpty() && max.isNotEmpty()){
            val minAmount=min.toString().replace(" ","").toBigDecimal()
            val maxAmount=max.toString().replace(" ","").toBigDecimal()
        if (minAmount>= BigDecimal(0) && maxAmount< BigDecimal(5000000000) && minAmount<maxAmount){
            return true
           }
        }
        return false
    }


}