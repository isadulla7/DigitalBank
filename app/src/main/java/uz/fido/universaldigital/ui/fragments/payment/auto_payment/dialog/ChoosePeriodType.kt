package uz.fido.universaldigital.ui.fragments.payment.auto_payment.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogAutoPaymentTypeBinding

class ChoosePeriodType(
private val onClickView:(Int)->Unit
):BottomSheetDialogFragment() {

    private lateinit var binding:DialogAutoPaymentTypeBinding
    var option1=false
    var option2=false
    var option3=false
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog
                .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogAutoPaymentTypeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.month.setOnClickListener {
            if (!option1){
                binding.btnContinue.isEnabled(true)
                option1=true
                option2=false
                option3=false
                binding.option1.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_construktor))
                binding.option2.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_box_color))
                binding.option3.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_box_color))
            }else{
                binding.btnContinue.isEnabled(false)
                option1=false
                binding.option1.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_box_color))
            }
        }
        binding.day.setOnClickListener {
            if (!option2){
                binding.btnContinue.isEnabled(true)
                option1=false
                option2=true
                option3=false
                binding.option1.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_box_color))
                binding.option2.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_construktor))
                binding.option3.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_box_color))
            }else{
                binding.btnContinue.isEnabled(false)
                option2=false
                binding.option2.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_box_color))
            }
        }
        binding.createDate.setOnClickListener {
            if (!option3){
                option1=false
                option2=false
                option3=true
                binding.btnContinue.isEnabled(true)
                binding.option1.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_box_color))
                binding.option2.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_box_color))
                binding.option3.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_construktor))
            }else{
                binding.btnContinue.isEnabled(false)
                option3=false
                binding.option3.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.check_box_color))
            }
        }
        binding.btnContinue.setOnClickListener {
            if (option1){onClickView.invoke(2)}
            if (option2){onClickView.invoke(1)}
            if (option3){onClickView.invoke(3)}
        }
    }
}