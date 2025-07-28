package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.safe_mode

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import coil.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardInfoRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.EditCardRequest
import uz.fido.network.domain.model.subscriptions.AutoPaymentRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSafeModeBinding
import uz.fido.universaldigital.ui.fragments.payment.auto_payment.AutoPaymentViewModel
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.extensions.openPlayMarket
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.home_utils.saveUserCardsSecure
import uz.fido.utils.const.Const
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class SafeModeFragment : BaseFragment<FragmentSafeModeBinding, MenuProductsViewModel>(
    FragmentSafeModeBinding::inflate, MenuProductsViewModel::class.java
) {

    private lateinit var card: CardResponse
    private val autoPaymentViewModel: AutoPaymentViewModel by activityViewModels()
    private val productViewModel: MenuProductsViewModel by activityViewModels()
    private lateinit var userCard:ArrayList<CardResponse>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            card = this.serializable<CardResponse>(Const.CARD) as CardResponse
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productViewModel.cards.observe(viewLifecycleOwner){ cardList->
            userCard= cardList as ArrayList<CardResponse>
        }
    }


    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()

    }

    private fun init() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.switchSafeMode.isChecked = card.safe_mode == "Y"
        binding.switchSafeMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) card.safe_mode = "Y" else card.safe_mode = "N"
            checksAutoPayment(isChecked)
        }
        binding.saveButton.setOnClickListener {
            pop()
        }
    }

    private fun checksAutoPayment(checked: Boolean) {
        if (checked) {
            getAutoPaymentList(checked)
        } else {
            editCard(isMain = checked)
        }
    }

    private fun getAutoPaymentList(checked: Boolean) {
        showProgress()
        autoPaymentViewModel.getAutoPaymentList(getClientToken(),
            AutoPaymentRequest(getFromSecureStore(Const.PAPER_CLIENT_PHONE, ""))).observe(viewLifecycleOwner) {
            when(it.status){
                Status.SUCCESS->{
                    val cardAutoPaymentList= it.data?.auto_payment_list?.filter { auto->auto.object_id==card.object_id  && auto.state=="A"}
                    if (cardAutoPaymentList.isNullOrEmpty()){
                        editCard(checked)
                    }else{
                        hideProgress()
                       infoDialog(checked)
                    }
                }
                Status.ERROR->{
                    showSnackbar("Auto Paymentni yuklashda xatolik")
                }
            }
        }
    }

    private fun infoDialog(checked: Boolean) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.warning))
            builder.setMessage(getString(R.string.all_avtopayments_will_be_disabled))
            builder.setPositiveButton(getString(R.string.continue_text)) { dialog, _ ->
                editCard(checked)
                dialog.dismiss()
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                binding.switchSafeMode.isChecked=false
                dialog.dismiss()
            }
            builder.show()

    }

    private fun editCard(isMain: Boolean) {
        showProgress()
        viewModel.editCardRequest(
            getClientToken(), EditCardRequest(
                card.object_name,
                card.is_main,
                card.object_id,
                card.bg_icon_name,
                if (isMain) "Y" else "N"
            )
        ).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    Status.SUCCESS -> {
                       getCardListItem(isMain)
                    }

                    Status.ERROR -> {
                        hideProgress()
                        showSnackbar(it.message.toString())
                    }
                }
            }
        }
    }

    private fun getCardListItem(isMain: Boolean) {
        card.safe_mode=if (isMain) "Y" else "N"
        val index = userCard.indexOfFirst {item-> item.object_id == card.object_id }
        userCard.removeAt(index)
        userCard.add(index,card)
        saveUserCardsSecure(userCard.toList())
        productViewModel.updateCards(userCard.toList())
        toast(getString(R.string.successfully))
        hideProgress()
    }

}