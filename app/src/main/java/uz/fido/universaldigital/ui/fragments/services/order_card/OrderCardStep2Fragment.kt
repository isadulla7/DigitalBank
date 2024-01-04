package uz.fido.universaldigital.ui.fragments.services.order_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.ocnyang.pagetransformerhelp.cardtransformer.AlphaAndScalePageTransformer
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.branches.Branches
import uz.fido.network.domain.model.branches.GetBranchListRequest
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.OrderCardRequest
import uz.fido.network.domain.model.cards.OrderCardTypeResponse
import uz.fido.network.domain.model.cards.PriceItem
import uz.fido.network.domain.model.cards.ProductType
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentOrderCardStep2Binding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.order_card.adapters.OrderCardBgAdapter
import uz.fido.universaldigital.ui.fragments.services.order_card.dialogs.ChooseBranchDialog
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.getCardByType
import uz.fido.universaldigital.ui.utils.extensions.yearText
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken
import java.io.Serializable

@AndroidEntryPoint
class OrderCardStep2Fragment : BaseFragment<FragmentOrderCardStep2Binding, OrderCardViewModel>(
    FragmentOrderCardStep2Binding::inflate, OrderCardViewModel::class.java
) {

    private lateinit var cardOrderResponse: OrderCardTypeResponse
    private lateinit var chooseBranchDialog: ChooseBranchDialog
    private lateinit var priceItem: ProductType

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private var orderType: OrderType = OrderType.GET_FROM_BANK
    private var cardBgAdapter: OrderCardBgAdapter? = null
    private var userCardList = ArrayList<CardResponse>()
    private var designPriceList = ArrayList<PriceItem>()
    private var productTypes = ArrayList<ProductType>()
    private var deliveryPrice: Int = 0
    private var designPrice: Int = 0
    private var cardPrice: Int = 0
    private var cardType: Int = 0
    private var productCode = ""
    private var filialCode = "0"
    private var mainCard = ""
    private var design = ""

    private var district = ""
    private var pinCode = ""
    private var street = ""
    private var house = ""
    private var city = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            priceItem = Gson().fromJson(
                it.getString(Constants.PRICE_ITEM.text), ProductType::class.java
            )
            cardOrderResponse = Gson().fromJson(
                it.getString(Constants.RESPONSE.text), OrderCardTypeResponse::class.java
            )
            productCode = requireArguments().getString(Constants.CODE.text, "")
            cardType = requireArguments().getInt(Constants.TYPE.text, 0)
            productTypes = cardOrderResponse.product_types
            designPriceList = priceItem.design_price_list
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        getBranches()
        init()
        gettingDetails()
        if (designPriceList.size != 0) initCardList()
        binding.smsPhoneLayout.isVisible = cardType != 1

    }

    @SuppressLint("SetTextI18n")
    private fun init() {
        initCards(cardType)
        binding.appBar.setTitle(priceItem.name)
        binding.phoneNumber.setText(Paper.book().read(Const.PAPER_CLIENT_PHONE, ""))
        binding.smsPhone.setText(Paper.book().read(Const.PAPER_CLIENT_PHONE, ""))
        cardPrice = priceItem.price
        binding.dotsIndicator.isVisible = productTypes.size > 1

        if (cardType == CardType.VISA.id || cardType == CardType.MASTERCARD.id) {
            binding.secretWordLayout.visibility = View.VISIBLE
            binding.secretWordDesc.visibility = View.VISIBLE
            binding.etSecretWord.addTextChangedListener { binding.btnContinue.isEnabled(checkFields()) }
        }

        if (priceItem.code == Constants.HUMO_NEW_CARD.text) {
            binding.pinCodeLayoutMain.visibility = View.VISIBLE
            pinCode = binding.pinCode.editableText.toString().trim()
        }
        binding.pinCode.doAfterTextChanged {
            val pinCount = binding.pinCode.editableText.toString().trim().length
            if (pinCount == 4) {
                binding.pinCodeLayout.isErrorEnabled = false
            }
        }
        binding.checkBox.setOnCheckedChangeListener { _, _ ->
            binding.btnContinue.isEnabled(checkFields())
        }
    }

    private fun initCards(cardType: Int) {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) {
            userCardList.clear()
            userCardList = getCardByType(cardType, it as ArrayList<CardResponse>)
        }
    }

    private fun gettingDetails() {
        binding.btnContinue.setOnClickListener {
            pinCode = binding.pinCode.editableText.toString().trim()
            if (pinCode.length != 4 && binding.pinCodeLayoutMain.isVisible) {
                binding.pinCodeLayout.isErrorEnabled = true
//                    binding.pinCodeLayout.error = getString(R.string.pin_for_card)
            } else {
                val request = OrderCardRequest(
                    command = "card",
                    userId = getClientId(),
                    design = design,
                    orderType = productCode,
                    from_object_id = "",
                    cardType = cardType.toString().replace("4", "3"),
                    amount = (designPrice + cardPrice + deliveryPrice).toString(),
                    payPurpose = "",
                    contact = binding.phoneNumber.editableText.toString().replace(" ", "")
                        .replace("+", ""),
                    address = "",
                    mainCardNumber = if (priceItem.code == "SV_NEW_PHIS_CARD" || priceItem.code == "GL_NEW_PHIS_CARD") "" else mainCard,
                    service_id = "",
                    order_filial_code = filialCode,
                    smsMobilePhone = binding.smsPhone.editableText.toString().replace(" ", "")
                        .replace("+", ""),
                    city = city,
                    district = district,
                    street = street,
                    house = house,
                    pin_code = pinCode,
                    secretWord = binding.etSecretWord.editableText.toString()
                )
                Log.d("===", request.toString())
                val bundle = Bundle()
                bundle.putSerializable("deliveryType", orderType)
                bundle.putSerializable("request", request)
                bundle.putInt("amount", designPrice + cardPrice + deliveryPrice)
                bundle.putInt("cardType", cardType)
                bundle.putString("productName", priceItem.name)
                bundle.putString("address", binding.address.editableText.toString())
                bundle.putString("filial", binding.branches.editableText.toString())
                bundle.putString(
                    "expire", priceItem.card_validity_period + " ${
                        yearText(
                            requireContext(), priceItem.card_validity_period.toInt()
                        )
                    }"
                )
                if (checkFields()) {
                    gotoWithSlide(R.id.confirmOrderCardFragment, bundle)
                } else {
                    showSnackbar(getString(R.string.fill_the_gaps))
                }
            }
        }
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.branches.setOnClickListener {
            if (viewModel.branches.value == null || viewModel.branches.value!!.isEmpty() || viewModel.shouldBranchListUpdate) {
                getBranches()
            } else {
                viewModel.branches.observe(viewLifecycleOwner) {
                    chooseBranchDialog = ChooseBranchDialog(this, it)
                    chooseBranchDialog.show(childFragmentManager, "TAG")
                }
            }
        }
    }

    private fun getBranches() {
        viewModel.getBranches(getClientToken(), GetBranchListRequest("info", "F"))
            .observe(viewLifecycleOwner) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            val data = resource.data?.filials
                            viewModel.shouldBranchListUpdate = false
                            viewModel.updateBranches(data!!)
                        }

                        Status.ERROR -> {
                            hideProgress()
                            showSnackbar(resource.message.toString())
                        }
                    }
                }
            }
    }

    private fun initCardList() {
        if (designPriceList.size == 1) {
            binding.dotsIndicator.visibility = View.GONE
//            binding.designPriceLayout.visibility = View.GONE
        }

        design =
            if (designPriceList.size > 0) designPriceList[0].image_name else "ic_card_visa_classic"
        designPrice = designPriceList[0].design_price
//        binding.designPrice.text = Format.formatAmount((designPriceList[0].design_price / 100).toString(), 0) + " UZS"

        cardBgAdapter = OrderCardBgAdapter(requireContext(), designPriceList, this)
        binding.viewPager.adapter = cardBgAdapter
        binding.dotsIndicator.setViewPager(binding.viewPager)
        binding.viewPager.setPageTransformer(true, AlphaAndScalePageTransformer())
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                designPrice = if (designPriceList[position].design_price == 0) {
                    //                    binding.designPrice.text = Format.formatAmount(
                    //                        (designPriceList[position].design_price / 100).toString(),
                    //                        0
                    //                    ) + " UZS"
                    designPriceList[position].design_price
                } else {
                    designPriceList[position].design_price
                    //                    binding.designPrice.text = Format.formatAmount(
                    //                        (designPriceList[position].design_price / 100).toString(),
                    //                        0
                    //                    ) + " UZS"
                }
                design = designPriceList[position].image_name
            }
        })
    }

    private fun checkFields(): Boolean {
        var result = true
        when (cardType) {
            CardType.UZCARD.id -> {
                result = result && binding.branches.editableText.toString().trim()
                    .isNotEmpty() && binding.phoneNumber.editableText.toString().trim()
                    .isNotEmpty() && /*if (orderType == OrderType.DELIVERY) binding.address.editableText.toString()
                    .trim().isNotEmpty() else */true
            }

            CardType.HUMO.id -> {
                result = result && binding.branches.editableText.toString().trim()
                    .isNotEmpty() && binding.phoneNumber.editableText.toString().trim()
                    .isNotEmpty() && binding.smsPhone.editableText.toString().trim()
                    .isNotEmpty() && binding.pinCode.editableText.toString().trim().length == 4 && /*if (orderType == OrderType.DELIVERY) binding.address.editableText.toString()
                    .trim().isNotEmpty() else*/ true
            }

            CardType.VISA.id, CardType.MASTERCARD.id -> {
                result = result && binding.branches.editableText.toString().trim()
                    .isNotEmpty() && binding.phoneNumber.editableText.toString().trim()
                    .isNotEmpty() && binding.smsPhone.editableText.toString().trim()
                    .isNotEmpty() && binding.etSecretWord.editableText.toString().trim()
                    .isNotEmpty()
            }

            else -> {
                result = if (orderType == OrderType.GET_FROM_BANK) {
                    result && binding.branches.editableText.toString().trim()
                        .isNotEmpty() && binding.phoneNumber.editableText.toString().trim()
                        .isNotEmpty() && binding.smsPhone.editableText.toString().trim()
                        .isNotEmpty()
                } else {
                    result && binding.address.editableText.toString().trim()
                        .isNotEmpty() && binding.phoneNumber.editableText.toString().trim()
                        .isNotEmpty() && binding.smsPhone.editableText.toString().trim()
                        .isNotEmpty()
                }
            }
        }
        return result && binding.checkBox.isChecked
    }

    override fun selectedBranch(branch: Branches) {
        super.selectedBranch(branch)
        chooseBranchDialog.dismiss()
        binding.branchLayout.visibility = View.VISIBLE
        binding.branches.setText(branch.name)
        filialCode = branch.filial_code.toString()
        binding.btnContinue.isEnabled(checkFields())
    }

    enum class OrderType : Serializable {
        GET_FROM_BANK, DELIVERY
    }

}