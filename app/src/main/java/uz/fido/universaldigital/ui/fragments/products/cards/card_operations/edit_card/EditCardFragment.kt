package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.edit_card

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.cards.EditCardRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentEditCardBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.adapter.CardBgAdapter
import uz.fido.universaldigital.ui.utils.extensions.getCardBackgroundList
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

class EditCardFragment : BaseFragment<FragmentEditCardBinding, MenuProductsViewModel>(
    FragmentEditCardBinding::inflate, MenuProductsViewModel::class.java
) {

    private lateinit var card: CardResponse
    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private var cardBgAdapter: CardBgAdapter? = null
    private var cardBgNames = ArrayList<String>()
    private var selectedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        card = requireArguments().serializable<CardResponse>(Const.CARD) as CardResponse
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setText()
    }

    private fun setText() {
        if (card.object_type=="TET"){
        binding.switchMakeMain.visibility=View.GONE
        binding.tvMakeMainDesc.visibility=View.GONE
        binding.tvMakeMain.visibility=View.GONE
        }

        if(card.object_type=="KL" || card.object_type.startsWith("D")){
            binding.tvMakeMain.text = getString(R.string.set_this_wallet_main)
            binding.cardNameLayout.hint=getString(R.string.wallet_name)
            binding.appBar.setTitle(getString(R.string.edit_wallet_title))
        }else{
            binding.tvMakeMain.text = getString(R.string.set_this_card_main)
            binding.cardNameLayout.hint=getString(R.string.card_name)
            binding.appBar.setTitle(getString(R.string.edit_card_title))
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
        initCardList()
        checkEditTexts()
    }

    private fun init() {
        binding.cardName.setText(card.object_name)
        binding.switchMakeMain.isChecked = card.is_main == "Y"
        binding.switchMakeMain.isClickable = card.is_main == "N"
        binding.saveButton.setOnClickListener { editCardRequest() }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun initCardList() {
        cardBgNames = getCardBackgroundList()
        cardBgAdapter = CardBgAdapter(requireContext(), cardBgNames)
        binding.viewpager.adapter = cardBgAdapter
        binding.dotsIndicator.setViewPager(binding.viewpager)
        binding.viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                selectedPosition = position
            }
        })
    }

    private fun editCardRequest() {
        showProgress()
        menuProductsViewModel.editCardRequest(
            getClientToken(), EditCardRequest(
                binding.cardName.editableText.toString(),
                if (binding.switchMakeMain.isChecked) "Y" else "N",
                card.object_id,
                cardBgNames[selectedPosition],
                card.safe_mode
            )
        ).observe(viewLifecycleOwner) { it ->
            it?.let { resource ->
                hideProgress()
                when (resource.status) {
                    Status.SUCCESS -> {
                        menuProductsViewModel.shouldUpdate = true
                        menuProductsViewModel.cards.observe(viewLifecycleOwner) { list ->
                            list.forEach {
                                if (it.object_id == card.object_id) {
                                    it.bg_icon_name = cardBgNames[selectedPosition]
                                    if (binding.switchMakeMain.isChecked) it.is_main = "Y"
                                    it.object_name = binding.cardName.editableText.toString()
                                } else it.is_main = "N"
                            }
                        }
                        pop()
                    }

                    Status.ERROR -> {
                        showSnackbar(resource.message.toString())
                    }
                }
            }
        }
    }

    private fun checkEditTexts() {
        binding.cardName.addTextChangedListener {
            val cardName = binding.cardName.text.toString().trim().replace(" ", "")
            binding.saveButton.isEnabled = cardName.isNotEmpty()
        }
    }

    private fun findCurrentBackground(
        cardResponse: CardResponse, cardBgList: ArrayList<String>
    ): Int {
        return cardBgList.indexOf(cardResponse.bg_icon_name)
    }

    override fun onResume() {
        super.onResume()
        binding.viewpager.currentItem = findCurrentBackground(card, cardBgNames)
    }

}