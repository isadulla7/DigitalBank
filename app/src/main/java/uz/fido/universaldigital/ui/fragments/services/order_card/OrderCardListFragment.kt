package uz.fido.universaldigital.ui.fragments.services.order_card

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import kotlinx.android.synthetic.main.fragment_order_card_list.uzcard
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.OrderCardTypeRequest
import uz.fido.network.domain.model.cards.OrderCardTypeResponse
import uz.fido.network.domain.model.cards.ProductType
import uz.fido.network.domain.model.sign_in.SignInResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentOrderCardListBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.order_card.adapters.ChooseOperationAdapter
import uz.fido.universaldigital.ui.fragments.services.order_card.dialogs.IdentifyDialog
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class OrderCardListFragment : BaseFragment<FragmentOrderCardListBinding, OrderCardViewModel>(
    FragmentOrderCardListBinding::inflate, OrderCardViewModel::class.java
), View.OnClickListener {

    private var response: OrderCardTypeResponse? = null
    private var masterCardList = ArrayList<ProductType>()
    private var humoCardList = ArrayList<ProductType>()
    private var productTypes = ArrayList<ProductType>()
    private var visaCardList = ArrayList<ProductType>()
    private var uzCardList = ArrayList<ProductType>()

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
    }

    private fun init() {
        binding.appBar.setOnAdditionalBtnClickListener(this)
        binding.appBar.setOnBackButtonClickListener(this)
        binding.mastercard.setOnClickListener(this)
        binding.uzcard.setOnClickListener(this)
        binding.humo.setOnClickListener(this)
        binding.visa.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.uzcard -> {
                if (!binding.uzcardExpandable.isExpanded) {
                    initCardList(CardType.UZCARD.id)
                } else {
                    binding.uzcardExpandable.isExpanded = false
                    binding.chevronUzcard.setImageResource(R.drawable.ic_arrow_down_ios)
                }
            }

            R.id.humo -> {
                if (!binding.humoExpandable.isExpanded) {
                    initCardList(CardType.HUMO.id)
                } else {
                    binding.humoExpandable.isExpanded = false
                    binding.chevronHumo.setImageResource(R.drawable.ic_arrow_down_ios)
                }
            }

            R.id.mastercard -> {
                if (!binding.mastercardExpandable.isExpanded) {
                    initCardList(CardType.MASTERCARD.id)
                } else {
                    binding.mastercardExpandable.isExpanded = false
                    binding.chevronMastercard.setImageResource(R.drawable.ic_arrow_down_ios)
                }
            }

            R.id.visa -> {
                if (!binding.visaExpandable.isExpanded) {
                    initCardList(CardType.VISA.id, true)
                } else {
                    binding.visaExpandable.isExpanded = false
                    binding.chevronVisa.setImageResource(R.drawable.ic_arrow_down_ios)
                }
            }

            R.id.back -> pop()

            R.id.additional -> {
                gotoWithSlide(
                    R.id.mainApplicationListFragment, bundleOf(Const.OPERATION to Const.ORDER_CARD)
                )
            }
        }
    }

    private fun initCardList(cardType: Int, isVisa: Boolean = false) {
        when (cardType) {
            CardType.UZCARD.id -> {
                if (uzCardList.isEmpty()) {
                    getCardOrderTypes(cardType, isVisa)
                } else {
                    initProducts(cardType, uzCardList)
                }
            }

            CardType.HUMO.id -> {
                if (humoCardList.isEmpty()) {
                    getCardOrderTypes(cardType, isVisa)
                } else {
                    initProducts(cardType, humoCardList)
                }
            }

            CardType.MASTERCARD.id -> {
                if (masterCardList.isEmpty()) {
                    getCardOrderTypes(cardType, isVisa)
                } else {
                    initProducts(cardType, masterCardList)
                }
            }

            CardType.VISA.id -> {
                if (visaCardList.isEmpty() && isVisa) {
                    getCardOrderTypes(cardType, isVisa)
                } else {
                    initProducts(cardType, visaCardList)
                }
            }

        }
    }

    private fun getCardOrderTypes(cardType: Int, isVisa: Boolean = false) {
        showHideProgress(cardType, ProgressState.SHOW)
        viewModel.getProductTypes(
            getClientToken(), OrderCardTypeRequest(if (isVisa) CardType.MASTERCARD.id else cardType)
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    showHideProgress(cardType, ProgressState.HIDE)
                    response = it.data
                    productTypes = response?.product_types!!
                    val cardList = ArrayList<ProductType>()
                    if (isVisa) {
                        productTypes.forEach { types ->
                            if (types.code.startsWith(Constants.VISA.text)) {
                                cardList.add(types)
                            }
                        }
                        initProducts(CardType.VISA.id, cardList)
                    } else {
                        productTypes.forEach { types ->
                            if (!types.code.startsWith(Constants.VISA.text)) {
                                cardList.add(types)
                            }
                        }
                        initProducts(cardType, cardList)
                    }
                }

                Status.ERROR -> {
                    showHideProgress(cardType, ProgressState.HIDE)
                    hideProgress()
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    override fun selectedCardWithOperation(priceItem: ProductType, operation: String) {
        super.selectedCardWithOperation(priceItem, operation)
        val signInResponse = Paper.book().read<SignInResponse>(Const.PAPER_CLIENT_INFO)
        if (priceItem.is_allowed_user_types.contains(signInResponse?.user_type_id)) {
            val bundle = bundleOf(
                Constants.PRICE_ITEM.text to Gson().toJson(priceItem),
                Constants.CODE.text to priceItem.code,
                Constants.RESPONSE.text to Gson().toJson(response),
                Constants.TYPE.text to operation.toInt()
            )
            if (priceItem.code == Constants.VISA_VIRTUAL_CARD.text || priceItem.code == Constants.HUMO_VIRTUAL_CARD.text || priceItem.code == Constants.MASTER_VIRTUAL_CARD.text) {
                gotoWithSlide(R.id.orderVirtualCard, bundle)
            } else {
                gotoWithSlide(R.id.orderCardStep2Fragment, bundle)
            }
        } else {
            val dialog = IdentifyDialog {
                gotoWithSlide(R.id.mainIdentificationFragment2)
            }
            dialog.show(childFragmentManager, IdentifyDialog.TAG)
        }
    }

    private fun initProducts(cardType: Int, productTypes: ArrayList<ProductType>) {
        when (cardType) {
            CardType.UZCARD.id -> {
                uzCardList = productTypes
                binding.emptyUzcardProducts.isVisible = uzCardList.isEmpty()
                binding.uzcardExpandable.isExpanded = true
                binding.uzcardProductList.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = ChooseOperationAdapter(
                        requireContext(),
                        this@OrderCardListFragment,
                        uzCardList,
                        cardType.toString()
                    )
                }
                binding.chevronUzcard.setImageResource(R.drawable.arrow_up_ios)
            }

            CardType.HUMO.id -> {
                humoCardList = productTypes
                binding.emptyHumoProducts.isVisible = humoCardList.isEmpty()
                binding.humoExpandable.isExpanded = true
                binding.humoProductList.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = ChooseOperationAdapter(
                        requireContext(),
                        this@OrderCardListFragment,
                        humoCardList,
                        cardType.toString()
                    )
                }
                binding.chevronHumo.setImageResource(R.drawable.arrow_up_ios)
            }

            CardType.MASTERCARD.id -> {
                masterCardList = productTypes
                binding.emptyMastercardProducts.isVisible = masterCardList.isEmpty()
                binding.mastercardExpandable.isExpanded = true
                binding.mastercardProductList.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = ChooseOperationAdapter(
                        requireContext(),
                        this@OrderCardListFragment,
                        masterCardList,
                        cardType.toString()
                    )
                }
                binding.chevronMastercard.setImageResource(R.drawable.arrow_up_ios)
            }

            CardType.VISA.id -> {
                visaCardList = productTypes
                binding.emptyVisaProducts.isVisible = visaCardList.isEmpty()
                binding.visaExpandable.isExpanded = true
                binding.visaProductList.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    adapter = ChooseOperationAdapter(
                        requireContext(),
                        this@OrderCardListFragment,
                        visaCardList,
                        cardType.toString()
                    )
                }
                binding.chevronVisa.setImageResource(R.drawable.arrow_up_ios)
            }
        }
    }

    private fun showHideProgress(cardType: Int, state: ProgressState) {
        when (cardType) {
            CardType.UZCARD.id -> {
                binding.uzcardProgress.visibility =
                    if (state == ProgressState.SHOW) View.VISIBLE else View.GONE
            }

            CardType.HUMO.id -> {
                binding.humoProgress.visibility =
                    if (state == ProgressState.SHOW) View.VISIBLE else View.GONE
            }

            CardType.MASTERCARD.id -> {
                binding.mastercardProgress.visibility =
                    if (state == ProgressState.SHOW) View.VISIBLE else View.GONE
            }

            CardType.VISA.id -> {
                binding.visaProgress.visibility =
                    if (state == ProgressState.SHOW) View.VISIBLE else View.GONE
            }
        }
    }
}

enum class ProgressState {
    SHOW, HIDE
}

enum class CardType(var id: Int) {
    UZCARD(1), HUMO(2), MASTERCARD(3), VISA(4)
}

enum class Constants(var text: String) {
    _UZS(" UZS"), UZS("UZS"), VISA("VISA"), MASTER_VIRTUAL_CARD("TET_VIRTUAL_CARD"), VISA_VIRTUAL_CARD(
        "VISA_VIRTUAL_CARD"
    ),
    HUMO_VIRTUAL_CARD("GL_VIRTUAL_CARD"), HUMO_NEW_CARD("GL_NEW_PHIS_CARD"), CODE("code"), TYPE("type"), RESPONSE(
        "response"
    ),
    PRICE_ITEM("priceItem"),
}
