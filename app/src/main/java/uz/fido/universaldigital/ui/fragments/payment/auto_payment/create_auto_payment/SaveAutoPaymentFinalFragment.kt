package uz.fido.universaldigital.ui.fragments.payment.auto_payment.create_auto_payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.subscriptions.SaveAutoPaymentModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSaveAutoPaymentFinalBinding
import uz.fido.universaldigital.databinding.ItemInfoMonitoringBinding
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.AutoPaymentViewModel
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class SaveAutoPaymentFinalFragment :
    BaseFragment<FragmentSaveAutoPaymentFinalBinding, AutoPaymentViewModel>
        (FragmentSaveAutoPaymentFinalBinding::inflate, AutoPaymentViewModel::class.java) {

    companion object {
        const val SAVE_AUTO_PAYMENT_MODEL = "save_payment"
        const val AUTO_PAYMENT_OPERATION = "operation"
    }


    private var saveAutoPaymentModel: SaveAutoPaymentModel? = null
    private var amount: String = "0.0"
    private var operation: String? = null
    private var senderCard: CardResponse? = null
    val menuProductsViewModel by activityViewModels<MenuProductsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            saveAutoPaymentModel = it.serializable<SaveAutoPaymentModel>(SAVE_AUTO_PAYMENT_MODEL)
            operation = it.getString("operation")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContinue.isEnabled(true)
        onClickView()
        addViewItem()
        initCards()

    }

    private fun addViewItem() {
        addView(getString(R.string.phone_number), getClientPhoneNumber())
        addView(getString(R.string.state_of_number), saveAutoPaymentModel?.check_status.toString())
        addView(getString(R.string.name_of_autopayment), saveAutoPaymentModel?.name.toString())
        if (saveAutoPaymentModel?.months!!.size != 0) {
            addView(
                getString(R.string.months_of_payment),
                saveAutoPaymentModel?.monthsName.toString()
            )
        }

        if (saveAutoPaymentModel?.selected_days!!.isNotEmpty()) {
            addView(
                getString(R.string.days_of_payment),
                saveAutoPaymentModel?.selected_days.toString()
            )
        } else addView(
            getString(R.string.days_of_payment),
            saveAutoPaymentModel?.daysName.toString()
        )
        amount = Format.convertFromTiynDivide(saveAutoPaymentModel?.amount!!)
        addView(getString(R.string.amount), Format.formatAmount(amount) + " UZS")
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            if (operation == "edit") {
                editAutoPayment()
            } else {
                saveAutoPayment()
            }
        }
    }

    private fun editAutoPayment() {
        binding.btnContinue.setProgress(true)
        saveAutoPaymentModel?.from_object_id = senderCard!!.object_id
        val model = saveAutoPaymentModel
        model?.monthsName = null
        model?.daysName = null
        model?.check_status = null
        model?.type = null
        viewModel.editAutoPayment(getClientToken(), model!!).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    gotoWithSlide(
                        R.id.basicSuccessFragment,
                        bundleOf(Const.OPERATION to BasicSuccessFragment.AUTO_PAYMENT_CREATED)
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun saveAutoPayment() {
        binding.btnContinue.setProgress(true)
        saveAutoPaymentModel?.from_object_id = senderCard!!.object_id
        val model = saveAutoPaymentModel
        model?.monthsName = null
        model?.daysName = null
        viewModel.createAutoPayment(getClientToken(), model!!).observe(viewLifecycleOwner) {
            binding.btnContinue.setProgress(false)
            when (it.status) {
                Status.SUCCESS -> {
                    gotoWithSlide(
                        R.id.basicSuccessFragment,
                        bundleOf(Const.OPERATION to BasicSuccessFragment.AUTO_PAYMENT_CREATED)
                    )

                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun initCards() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            binding.chooseCardLayout.initCards(
                it as ArrayList<CardResponse>, "0", CurrencyConst.CURRENCY_CHAR_UZS
            ) { cardResponse ->
                senderCard = cardResponse
                if (cardResponse?.state == "0") {
                    binding.btnContinue.isEnabled(true)
                } else binding.btnContinue.isEnabled(false)
            }
        }

    }


    private fun addView(name: String, value: String) {
        val viewDepositCreateBinding =
            ItemInfoMonitoringBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        viewDepositCreateBinding.name.text = name
        viewDepositCreateBinding.value.text = value
        binding.add.addView(viewDepositCreateBinding.root)
    }
}