package uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.network.domain.model.deposits.constructor.BxmCodeAndName
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentDepositStepFirstBinding
import uz.fido.universaldigital.ui.dialogs.NearBranchDialog
import uz.fido.universaldigital.ui.fragments.services.deposit.MainDepositViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.dialog.CalculatorDialog
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class OpenDepositStepFirst : BaseFragment<FragmentDepositStepFirstBinding, MainDepositViewModel>(FragmentDepositStepFirstBinding::inflate, MainDepositViewModel::class.java), (String) -> Unit,
    BaseInterface {

    private lateinit var dialog: CalculatorDialog
    private lateinit var nearBranchDialog: NearBranchDialog
    private lateinit var deposit: Deposit
    private var operation: String? = null
    private var bxmList: ArrayList<BxmCodeAndName> = arrayListOf()
    private var selectedBranch: BxmCodeAndName? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            operation = it.getString("operation")
            deposit = it.serializable<Deposit>("deposit") as Deposit
        }
        init()
    }

    fun init() {
        getBxmList()
        setTextView()
        textWatchers()
        initSetOnClickListeners()
        percentCurrent()
    }

    private fun percentCurrent() {
        if (deposit.percent == "0") {
            binding.etAmount.setText("0")
            binding.amountLin.visibility = View.GONE
            binding.maxAmount.visibility = View.GONE
            binding.appBar.setAdditionalBtnVisibility(false)
        } else {
            binding.appBar.setAdditionalBtnVisibility(true)
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            deposit.pay_to_card = "Y"
            val amount = binding.etAmount.editableText.toString().replace(" ", "")
            val type = requireArguments().getBoolean("isSum")
            goto(
                R.id.openDepositStepTwoFragment, bundleOf(
                    "deposit" to deposit, "amount" to amount, "isSum" to type, "bxm_code" to selectedBranch?.bxm_code
                )
            )
        }
        binding.appBar.setOnAdditionalBtnClickListener {
            dialog = CalculatorDialog(
                this, deposit.min_sum.toString()
            )
            dialog.show(childFragmentManager, "")
        }
        binding.etBranch.setOnClickListener {
            if (bxmList.isNotEmpty()) {
                openNearBranchLayout()
            } else getBxmList()
        }
    }

    private fun textWatchers() {
        binding.etAmount.addTextChangedListener { text ->
            if (deposit.percent != "0") {
                try {
                    binding.btnContinue.isEnabled(text.toString().replace(" ", "").toDouble() >= Format.formatAmountFromTiynToInteger(deposit.min_sum.toString()).toDouble() && selectedBranch != null)
                } catch (e: Exception) {
                    binding.btnContinue.isEnabled(false)
                }
            } else binding.btnContinue.isEnabled(true)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextView() {
        binding.appBar.setTitle(deposit.dep_name)
        binding.percentText.text = "${getString(R.string.percent_text)} ${deposit.percent} %"
        binding.minAmount.text = getString(R.string.min_summa) + " " + Format.formatAmount(Format.formatAmountFromTiynToInteger(deposit.min_sum.toString())) + " " + Format().getCurrencyChar(
            deposit.currency_code
        )
        binding.tvTime.text = getString(R.string.time_deposit) + " " + Format().formattedDepositExpire(
            requireContext(), deposit.keeping_time
        )
    }

    override fun invoke(amount: String) {
        gotoWithSlide(
            R.id.depositCalculatorResultFragment, bundleOf(
                "dep_id" to deposit.dep_id, "amount" to amount
            )
        )
        dialog.dismiss()
    }

    private fun getBxmList() {
        viewModel.getBxmList().observe(viewLifecycleOwner) { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    bxmList.clear()
                    bxmList = (resources.data?.list ?: arrayListOf()) as ArrayList<BxmCodeAndName>
                }

                Status.ERROR -> {
                    showSnackbar(resources.message.toString())
                }
            }
        }
    }

    private fun openNearBranchLayout() {
        nearBranchDialog = NearBranchDialog(this, bxmList)
        nearBranchDialog.show(childFragmentManager, "")
    }

    override fun setToEditText(allServiceLists: BxmCodeAndName) {
        super<BaseFragment>.setToEditText(allServiceLists)
        selectedBranch = allServiceLists
        binding.etBranch.setText(allServiceLists.name)
        if (binding.etAmount.text.isNullOrEmpty()) {
            binding.btnContinue.isEnabled(false)
        } else
            binding.btnContinue.isEnabled(
                binding.etAmount.text.toString().replace(" ", "").toDouble() >= Format.formatAmountFromTiynToInteger(deposit.min_sum.toString()).toDouble() && selectedBranch != null
            )
    }

}