package uz.fido.universaldigital.ui.fragments.services.loan.requisites

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.fido.network.domain.model.loans.loan_products.CreditProduct
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.FragmentCreditRequisitesBinding
import uz.fido.utils.const.Const
import uz.fido.utils.security.getFromSecureStore

class CreditRequisitesFragment(private val clientProduct: CreditProduct) : DialogFragment() {

    private lateinit var binding: FragmentCreditRequisitesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreditRequisitesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogThemetwo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.setOnBackButtonClickListener { dismiss() }
        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.value.text = getFromSecureStore(Const.LAST_NAME) + " " + getFromSecureStore(Const.FIRST_NAME)
        binding.valueNumber.text = clientProduct.codeFilial
        binding.address.text = clientProduct.filialName
    }
}