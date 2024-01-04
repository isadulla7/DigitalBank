package uz.fido.universaldigital.ui.fragments.services.deposit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogCapitalConstructorBinding

class ConstructorCapitalDialog(private val onClickView: (Boolean, Boolean, Boolean, Boolean) -> Unit) :
    BottomSheetDialogFragment() {

    private lateinit var binding: DialogCapitalConstructorBinding

    var option1 = false
    var option2 = false
    var option3 = false
    var option4 = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCapitalConstructorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOption1()
        setOption2()
        setOption3()
        setOption4()
        setOnclickView()

    }

    private fun setOnclickView() {
        binding.btnContinue.isEnabled(true)
        binding.btnContinue.setOnClickListener {
            onClickView.invoke(option1, option2, option3, option4)
        }
    }

    private fun setOption1() {
        binding.replanishment.setOnClickListener {
            if (!option1) {
                option1 = true
                binding.option1.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.check_construktor
                    )
                )
            } else {
                option1 = false
                binding.option1.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.check_box_color
                    )
                )
            }
        }

    }

    private fun setOption2() {
        binding.interestPayment.setOnClickListener {
            if (!option2) {
                option2 = true
                binding.option2.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.check_construktor
                    )
                )
            } else {
                option2 = false
                binding.option2.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.check_box_color
                    )
                )
            }
        }

    }

    private fun setOption3() {
        binding.capitalization.setOnClickListener {
            if (!option3) {
                option3 = true
                binding.option3.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.check_construktor
                    )
                )
            } else {
                option3 = false
                binding.option3.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.check_box_color
                    )
                )
            }
        }

    }

    private fun setOption4() {
        binding.partialWithdrawal.setOnClickListener {
            if (!option4) {
                option4 = true
                binding.option4.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.check_construktor
                    )
                )
            } else {
                option4 = false
                binding.option4.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.check_box_color
                    )
                )
            }
        }

    }
}