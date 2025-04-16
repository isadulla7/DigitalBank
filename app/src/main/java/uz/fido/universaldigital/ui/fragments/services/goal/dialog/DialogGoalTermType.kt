package uz.fido.universaldigital.ui.fragments.services.goal.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogGoalTermTypeBinding

class DialogGoalTermType(private val onClick:(Int,String)->Unit): BottomSheetDialogFragment() {

      private lateinit var binding:DialogGoalTermTypeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DialogGoalTermTypeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.month6.setOnClickListener {
            dismiss()
            onClick.invoke(1, getString(R.string.month_6))
        }
        binding.month12.setOnClickListener {
            dismiss()
            onClick.invoke(2, getString(R.string.month_12))
        }
        binding.month18.setOnClickListener {
            dismiss()
            onClick.invoke(3, getString(R.string.month_18))
        }
        binding.month24.setOnClickListener {
            dismiss()
            onClick.invoke(4, getString(R.string.month_24))
        }
        binding.otherTerm.setOnClickListener {
            dismiss()
            onClick.invoke(5, getString(R.string.custom_term))
        }
        binding.unlimited.setOnClickListener {
            dismiss()
            onClick.invoke(6, getString(R.string.unlimited))
        }
    }

}