package uz.fido.universaldigital.ui.fragments.services.deposit.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.fido.network.domain.model.deposits.my_deposit.ClientDeposit
import uz.fido.network.domain.model.monitoring.AccountHistory
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogInfoDepositMonitoringBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.utils.const.Const
import uz.fido.utils.format.Format
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class DialogInfoMonitoring(
    private val item: AccountHistory?,
    private val clientDeposit: ClientDeposit,
    private val type: String
) : DialogFragment() {

    private lateinit var binding: DialogInfoDepositMonitoringBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogThemetwo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogInfoDepositMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (type == Const.TYPE_DEPOSIT) {
            initViews()
        } else {
            binding.appBar.setTitle(getString(R.string.information_about_the_deposit))
            infoDeposit()
        }


        onClick()
    }

    private fun infoDeposit() {
        addView(
            getString(R.string.name),
            clientDeposit.depName.orEmpty()
        )
        addView(
            getString(R.string.initial_amount),
            clientDeposit.amount.orEmpty()
        )

        addView(
            getString(R.string.whole_amount),
            clientDeposit.sumDep.orEmpty()
        )

        addView(
            getString(R.string.percents),
            "${clientDeposit.percent} %"
        )
        addView(
            getString(R.string.date_time),
            clientDeposit.openDate.orEmpty()
        )



        addView(
            getString(R.string.deposit_balance),
            clientDeposit.sumDep.formatTiynAmount(clientDeposit.currencyChar)
        )
        addView(
            getString(R.string.interest_payable_percents),
            clientDeposit.interestPayable.formatTiynAmount(clientDeposit.currencyChar)
        )
        addView(
            getString(R.string.current_deposit_percents),
            clientDeposit.persSum.formatTiynAmount(clientDeposit.currencyChar)
        )

        addView(getString(R.string.deposit_deadline_until), clientDeposit.closingDate.orEmpty())
        addView(
            getString(R.string.deposit_left_day),
            calculateDays(clientDeposit.closingDate.orEmpty()).toString() + ""
        )

        addView(
            getString(R.string.deposit_replenishment),
            if (clientDeposit.replenishment == "Y") getString(R.string.yes) else getString(R.string.no)
        )
        addView(
            getString(R.string.partial_withdrawal),
            if (clientDeposit.partialWrite == "Y") getString(R.string.yes) else getString(R.string.no)
        )
        addView(
            getString(R.string.interest_withdrawal),
            if (clientDeposit.withdrawInterest == "Y") getString(R.string.yes) else getString(R.string.no)
        )

    }

    private fun String?.formatTiynAmount(currency: String? = null): String {
        this?.let {
            var amount = Format.formatAmount(Format.convertFromTiynDivide(it))
            if (currency != null) {
                amount = "$amount $currency"
            }
            return amount
        }
        return "0"
    }


    private fun calculateDays(stringDate: String): Int {
        return try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            val millionSeconds = sdf.parse(stringDate).time - Calendar.getInstance().timeInMillis
            TimeUnit.MILLISECONDS.toDays(millionSeconds).toInt()
        } catch (e: Exception) {
            0
        }
    }


    private fun onClick() {
        binding.appBar.setOnBackButtonClickListener { dismiss() }
    }

    private fun initViews() {
        addView(
            getString(R.string.purpose),
            (if (type == Const.TYPE_LOAN) setCreditPurpose(
                requireContext(),
                item!!.lnType
            ) else item!!.purpose).toString()
        )
        addView(getString(R.string.transaction_number), item.NumberTrans)
        addView(getString(R.string.date_time), item.dateExecute.toString())
        addView(
            getString(R.string.amount),
            if (item.debit == "0") Format.formatAmount((item.credit!!.toDouble() / 100).toString()) + " UZS" else Format.formatAmount(
                (item.debit.toDouble() / 100).toString()
            ) + " UZS"
        )
    }

    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding =
            ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.linAdd.addView(viewDepositCreateBinding.root)
    }

    private fun setCreditPurpose(context: Context, lnType: String): String {
        return when (lnType) {
            "1" -> context.getString(R.string.purpose_1)
            "2", "" -> context.getString(R.string.top_up_account_for_loan)
            "3" -> context.getString(R.string.purpose_3)
            "5" -> context.getString(R.string.purpose_5)
            "7" -> context.getString(R.string.purpose_7)
            "22" -> context.getString(R.string.purpose_22)
            "46" -> context.getString(R.string.purpose_46)
            else -> ""
        }
    }
}