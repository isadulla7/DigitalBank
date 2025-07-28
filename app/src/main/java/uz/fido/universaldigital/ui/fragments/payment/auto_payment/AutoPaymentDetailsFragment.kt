package uz.fido.universaldigital.ui.fragments.payment.auto_payment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.subscriptions.AutoPayment
import uz.fido.network.domain.model.subscriptions.ChangeAutoPaymentStateRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAutoPaymentDetailBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.home_utils.saveUserCardsSecure
import uz.fido.utils.const.CardConst.STATE_ACTIVE
import uz.fido.utils.const.CardConst.STATE_PASSIVE
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class AutoPaymentDetailsFragment : BaseFragment<FragmentAutoPaymentDetailBinding, AutoPaymentViewModel>
    (FragmentAutoPaymentDetailBinding::inflate, AutoPaymentViewModel::class.java) {

    private var autoPayment: AutoPayment? = null
    private var days = ArrayList<String>()
    private var months = ArrayList<String>()
    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private  var card:CardResponse?=null
    private  var userCards:ArrayList<CardResponse> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            autoPayment = it.serializable<AutoPayment>("item")
        }

        getList()
        onClick()
        setTextItem()
    }

    private fun setTextItem() {

        binding.appBar.setTitle(autoPayment?.name.toString())
        binding.textAmountValue.text = Format.formatAmount(Format.convertFromTiynDivide(autoPayment?.amount.toString())) + " UZS"
        binding.textNameValue.text = autoPayment?.name.toString()
        binding.textDateValue.text = autoPayment?.modified_on.toString()
        binding.textServiceTypeValue.text = if (autoPayment?.type == "D") getString(R.string.daily) else getString(R.string.monthly_2)
        if (autoPayment?.state == STATE_ACTIVE) {
            binding.statusValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_auto_activ))
            binding.statusValue.text = getString(R.string.active)
            binding.statusValue.isChecked = true
        } else {
            binding.statusValue.isChecked = false
            binding.statusValue.text = getString(R.string.inactive)
            binding.statusValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_auto_no_activ))
        }

        if (autoPayment?.type == "D") {
            val daysList = ArrayList<String>()
            autoPayment?.days?.forEach { day ->
                for (i in days.indices) {
                    if (day == i + 1) {
                        daysList.add(days[i])
                    }
                }
            }
            val dayName = TextUtils.join(", ", daysList)
            binding.textDaysOfPaymentValue.text = dayName
        } else if (autoPayment?.type == "M") {
            binding.textDaysOfPaymentValue.text = autoPayment!!.days[0].toString()
            val monthsList = ArrayList<String>()
            autoPayment?.months?.forEach { day ->
                for (i in months.indices) {
                    if (day == i + 1) {
                        monthsList.add(months[i])
                    }
                }
            }
            val monthName = TextUtils.join(", ", monthsList)
            binding.layoutMonth.visibility = View.VISIBLE
            binding.layoutMonthIcon.visibility = View.VISIBLE
            binding.textMonthsOfPaymentValue.text = monthName
        } else {
            binding.textServiceTypeValue.text = getString(R.string.custom)
            binding.layoutMonth.visibility = View.GONE
            binding.layoutMonthIcon.visibility = View.GONE
            if (autoPayment?.selected_days != null) {
                binding.textDaysOfPaymentValue.text = TextUtils.join(", ", autoPayment?.selected_days!!)
            } else {
                binding.layoutDay.visibility = View.GONE
                binding.layoutDayIcon.visibility = View.GONE
            }
        }

    }

    private fun onClick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.statusValue.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked) {
                binding.statusValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_auto_activ))
                binding.statusValue.text = getString(R.string.active)
            } else {
                binding.statusValue.text = getString(R.string.inactive)
                binding.statusValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_auto_no_activ))
            }
        }

        binding.btnEnter.setOnClickListener {
            checkCard()

        }
    }

    private fun checkCard() {
        val state=binding.statusValue.isChecked
        if (state){
          getCardList()
        }else{
            updateState()
        }
    }

    private fun getCardList() {
        menuProductsViewModel.cards.observe(viewLifecycleOwner){ cardList ->
            userCards=cardList as ArrayList<CardResponse>
            card= cardList.firstOrNull { it.object_id == autoPayment?.object_id }
            if ( card?.safe_mode=="Y"){
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(R.string.warning)
                builder.setMessage(R.string.attached_card_will_be_nonsecure_mode)
                builder.setPositiveButton(getString(R.string.continue_text)) { dialog, _ ->
                    updateState()
                    dialog.dismiss()
                }
                builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    binding.statusValue.isChecked=false
                    dialog.dismiss()
                }
                builder.show()
            }else{
                updateState()
            }
        }
    }

    private fun updateState() {
        showProgress()
        val state = if (!binding.statusValue.isChecked) STATE_PASSIVE else STATE_ACTIVE
        viewModel.changeAutoPaymentState(getClientToken(), ChangeAutoPaymentStateRequest(state, autoPayment?.id.toString())).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    if (binding.statusValue.isChecked && card?.safe_mode=="Y"){
                        card?.safe_mode="N"
                        val index = userCards.indexOfFirst {item-> item.object_id == card?.object_id }
                        userCards.removeAt(index)
                        userCards.add(index,card!!)
                        saveUserCardsSecure(userCards.toList())
                        menuProductsViewModel.updateCards(userCards.toList())
                    }
                    Toast.makeText(requireContext(), getString(R.string.successfully), Toast.LENGTH_SHORT).show()
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }

        }
    }

    private fun getList() {
        days = arrayListOf(
            getString(R.string.monday), getString(R.string.tuesday),
            getString(R.string.thursday), getString(R.string.wednesday),
            getString(R.string.friday), getString(R.string.saturday), getString(R.string.sunday),
        )
        months = arrayListOf(
            getString(R.string.month_jan), getString(R.string.month_fev), getString(R.string.month_mar), getString(R.string.month_apr),
            getString(R.string.month_may), getString(R.string.month_june), getString(R.string.month_july), getString(R.string.month_aug),
            getString(R.string.month_sep), getString(R.string.month_oct), getString(R.string.month_nov), getString(R.string.month_dec)
        )
    }


}