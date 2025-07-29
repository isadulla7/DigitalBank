package uz.fido.universaldigital.ui.fragments.payment.credit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.loans.CreditPasswordDetails
import uz.fido.network.domain.model.loans.LnSearchLoanRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogCreditPassportInfoBinding
import uz.fido.universaldigital.ui.fragments.services.loan.LoanViewModel
import uz.fido.universaldigital.ui.utils.extensions.hideProgress
import uz.fido.universaldigital.ui.utils.extensions.showProgress
import uz.fido.universaldigital.ui.utils.extensions.showSnackbar
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal

class CreditInfoFragment(val onClick:(String)->Unit) : DialogFragment() {

    private lateinit var binding: DialogCreditPassportInfoBinding
    private var loanId:String=""
    private val viewModel: LoanViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogThemetwo)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogCreditPassportInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textWatcher()
        binding.btnContinue.isEnabled(true)
        binding.appBar.setOnBackButtonClickListener { dismiss() }
        binding.appBar.setTitle(getString(R.string.credit_passpord))
        binding.btnContinue.setOnClickListener {
            onClick(loanId)
            dismiss()
        }

    }

    private fun textWatcher() {
        Observable.combineLatest(
            binding.etPassportSerial.textChanges(),
            binding.etPassportNumber.textChanges(),
            BiFunction(this::isValid)
        ).doOnNext {
            checkEditText(it)
        }
            .subscribe()
    }

    private fun checkEditText(it: Boolean) {
        if (it) {
              showProgress(requireActivity())
            viewModel.getLnSearchLoan(getClientToken(),
                LnSearchLoanRequest("6",
                    binding.etPassportNumber.text.toString(),
                    binding.etPassportSerial.text.toString())).observe(viewLifecycleOwner) {
                        hideProgress(requireActivity())
                when(it.status){
                    Status.SUCCESS->{
                        val item=it.data?.data as ArrayList<CreditPasswordDetails>
                        if (item.isNotEmpty()){
                            loanId=item[0].loanContractId?:"0"
                       binding.layoutInfo.visibility=View.VISIBLE
                        binding.btnContinue.visibility=View.VISIBLE
                        binding.productName.text=item[0].productName
                        binding.productFillial.text=item[0].filialName
                        binding.idCreditPaynet.text=item[0].loanId
                        binding.amount.text= Format.formatAmount(item[0].amount?.toBigDecimalOrNull()?.divide(BigDecimal(100)).toString())
                        }else{
                            showSnackbar(getString(R.string.credit_not_found),getString(R.string.credit_passpord))
                        }

                    }
                    Status.ERROR->{
                        showSnackbar(getString(R.string.credit_not_found))
                    }
                }
            }
        }
    }

    private fun isValid(
        passportSerial: CharSequence,
        passportNumber: CharSequence
    ) =
        (passportSerial.toString().length == 2
                && passportNumber.toString().length == 7)

}