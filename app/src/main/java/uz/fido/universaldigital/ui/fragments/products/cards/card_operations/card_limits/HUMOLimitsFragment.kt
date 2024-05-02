package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.card_limits

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.limits.gl.GlLimitDeleteRequest
import uz.fido.network.domain.model.limits.gl.GlLimitListRequest
import uz.fido.network.domain.model.limits.gl.GlLimitResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentHumoLimitsBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

class HUMOLimitsFragment : BaseFragment<FragmentHumoLimitsBinding, MenuProductsViewModel>(
    FragmentHumoLimitsBinding::inflate, MenuProductsViewModel::class.java
) {

    private lateinit var card: CardResponse
    private lateinit var limitType: String

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            card = it.serializable<CardResponse>(Const.CARD) as CardResponse
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
    }

    private fun init() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.addButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(Const.CARD, card)
            bundle.putString(Const.OPERATION, "set")
            gotoWithSlide(R.id.setCardLimitsFragment, bundle)
        }
        binding.deleteButton.setOnClickListener {
            deleteSvCardLimit(limitType)
        }
        getGlLimitList()
    }

    private fun getGlLimitList() {
        showProgress()
        menuProductsViewModel.getGlCardLimitList(
            getClientToken(), GlLimitListRequest(object_value = card.object_value)
        ).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { it1 -> initLimit(it1) }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun initLimit(response: GlLimitResponse) {
        binding.emptyView.isVisible = response.limit_type.isEmpty()
        binding.deleteButton.isVisible = response.limit_type.isNotEmpty()
        binding.limitAmount.text =
            Format.formatAmount(Format.formatAmountFromTiynToInteger(response.limit_value)) + " UZS"
        binding.limitStartDate.text = response.limit_date_from
        binding.limitEndDate.text = response.limit_date_to
        binding.limitType.text = response.limit_type
        limitType = response.limit_type
    }

    private fun deleteSvCardLimit(limitId: String) {
        showProgress()
        val request = GlLimitDeleteRequest(
            limit_type = limitId, object_value = card.object_value
        )
        menuProductsViewModel.deleteGlCardLimit(getClientToken(), request).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    showSnackbar(getString(R.string.limit_deletec))
                    getGlLimitList()
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }


}