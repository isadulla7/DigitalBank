package uz.fido.universaldigital.ui.fragments.services.loan.create_loan

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentCreditSuccessBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreditSuccessFragment : BaseSimpleFragment<FragmentCreditSuccessBinding>(
    FragmentCreditSuccessBinding::inflate
) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTextTo()
        setOperationTime()
        setOnClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setOnClick() {
        binding.gotoMainPage.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun setTextTo() {
        /*   binding.amount.text =
               "${Format.formatAmount(requireArguments().getString("amount"))} UZS"*/
        binding.successTitle.text = getString(R.string.been_processed)
    }

    private fun setOperationTime() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy • HH:mm", Locale.US)
        val now = Calendar.getInstance().time
        binding.date.text = dateFormat.format(now)
    }
}