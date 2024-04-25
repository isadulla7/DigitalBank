package uz.fido.universaldigital.ui.fragments.services.deposit.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.databinding.DialogDepositCalculatorBinding
import uz.fido.utils.utility.format.Format

class CalculatorDialog(val onClick: (String) -> Unit, val minAmount: String) :
    BottomSheetDialogFragment() {

    private lateinit var binding: DialogDepositCalculatorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDepositCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textWatchers()
        onClickView()
    }

    private fun onClickView() {
        binding.btnContinue.setOnClickListener {
            onClick.invoke(binding.etAmount.text.toString().replace(" ", ""))
            closeKeyboard()
        }
    }

    private fun textWatchers() {
        binding.etAmount.addTextChangedListener { text ->
           try {
               if (text!!.isEmpty()) {
                   binding.btnContinue.isEnabled(false)
               } else if (text.toString().replace(" ", "")
                       .toDouble() >= Format.formatAmountFromTiynToInteger(minAmount).toDouble()
               ) {
                   binding.btnContinue.isEnabled(true)
               } else binding.btnContinue.isEnabled(false)
           }catch (e:Exception){
               binding.btnContinue.isEnabled(false)
           }
        }
    }

    override fun dismiss() {
        super.dismiss()
        closeKeyboard()
    }


    fun closeKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }


}