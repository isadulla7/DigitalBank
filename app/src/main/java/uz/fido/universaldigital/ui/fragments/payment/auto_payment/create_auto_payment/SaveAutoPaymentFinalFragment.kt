package uz.fido.universaldigital.ui.fragments.payment.auto_payment.create_auto_payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
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
import uz.fido.universaldigital.ui.utils.extensions.showSnackbar
import uz.fido.universaldigital.ui.utils.home_utils.saveUserCardsSecure
import uz.fido.utils.const.Const
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientPhoneNumber
import uz.fido.utils.utility.user.getClientToken
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

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
    private var userCards: ArrayList<CardResponse> = arrayListOf()
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
        initCards()
        onClickView()
        addViewItem()
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
        if (saveAutoPaymentModel?.percent != null) {
            val amount = saveAutoPaymentModel?.amount?.toBigDecimalOrNull()?.divide(BigDecimal(100)) ?: BigDecimal(BigInteger.ZERO)
            val percent = saveAutoPaymentModel?.percent?.toBigDecimalOrNull()?.divide(BigDecimal(100)) ?: BigDecimal(BigInteger.ZERO)
            val result = (amount * percent) + amount
            addView(getString(R.string.total_amount), Format.formatAmount(result.toString()) + " UZS")
        }
        amount = Format.convertFromTiynDivide(saveAutoPaymentModel?.amount!!)
        addView(getString(R.string.amount), Format.formatAmount(amount) + " UZS")
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            when {
                senderCard?.pay_with_sms == "Y" -> {
                    showSnackbar("avto payment yaratib bo'lmaydi")
                }

                saveAutoPaymentModel?.sms_control_limit == "-1" -> {
                    showSnackbar(getString(R.string.warning), getString(R.string.unable_create_payment))
                }

                senderCard?.safe_mode == "Y" -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(getString(R.string.warning))
                    builder.setMessage(getString(R.string.attached_card_will_be_nonsecure_mode))
                    builder.setPositiveButton(getString(R.string.continue_text)) { dialog, _ ->
                        checkCard()
                        dialog.dismiss()
                    }
                    builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }

                else -> {
                    checkCard()
                }
            }
        }
    }

    private fun checkCard() {
        if (operation == "edit") {
            editAutoPayment()
        } else {
            saveAutoPayment()
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
                    if (senderCard?.safe_mode == "Y") {
                        senderCard?.safe_mode = "N"
                        val index = userCards.indexOfFirst { item -> item.object_id == senderCard?.object_id }
                        userCards.removeAt(index)
                        userCards.add(index, senderCard!!)
                        saveUserCardsSecure(userCards.toList())
                        menuProductsViewModel.updateCards(userCards.toList())
                    }
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
                    if (senderCard?.safe_mode == "Y") {
                        senderCard?.safe_mode = "N"
                        val index = userCards.indexOfFirst { item -> item.object_id == senderCard?.object_id }
                        userCards.removeAt(index)
                        userCards.add(index, senderCard!!)
                        saveUserCardsSecure(userCards.toList())
                        menuProductsViewModel.updateCards(userCards.toList())
                    }
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
            userCards = it as ArrayList<CardResponse>
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