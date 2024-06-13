package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.card_limits

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ethanhua.skeleton.Skeleton
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.limits.CardLimitRequest
import uz.fido.network.domain.model.limits.SvLimit
import uz.fido.network.domain.model.limits.SvSetMainCardRequest
import uz.fido.network.domain.model.limits.gl.GlLimitListRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentMainLimitsBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.products.cards.card_operations.card_limits.adapters.LimitAdapter
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MainLimitsFragment : BaseFragment<FragmentMainLimitsBinding, MenuProductsViewModel>(
    FragmentMainLimitsBinding::inflate, MenuProductsViewModel::class.java
), View.OnClickListener, BaseInterface {

    private lateinit var card: CardResponse

    private var svLimitAdapter: LimitAdapter? = null
    private var limitList = ArrayList<SvLimit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        card = requireArguments().serializable<CardResponse>(Const.CARD) as CardResponse
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initList()
        init()
    }

    private fun init() {
        binding.addButton.setOnClickListener(this)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.swipeRefresh.setOnRefreshListener {
            fetchLimit()
        }
        fetchLimit()
    }

    private fun initList() {
        binding.limitList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            limitList = ArrayList()
            svLimitAdapter = LimitAdapter(limitList, this@MainLimitsFragment)
            adapter = svLimitAdapter
        }
    }

    private fun fetchLimit() {
        if (card.object_type == "SV") {
            getSvLimitList()
        } else {
            getGlLimitList()
        }
        binding.swipeRefresh.isRefreshing = false
    }

    private fun svSetMainCard() {
        viewModel.svSetMainCard(getClientToken(), SvSetMainCardRequest(card.object_value))
            .observe(viewLifecycleOwner) {
                getSvLimitList()
            }
    }

    private fun getSvLimitList() {
        val skeletonView = Skeleton.bind(binding.limitList).adapter(svLimitAdapter)
            .load(R.layout.shimmer_item_device).color(R.color.shimmer_color).show()
        viewModel.getSvCardLimitList(
            getClientToken(), CardLimitRequest(object_id = card.object_id)
        ).observe(viewLifecycleOwner) {
            skeletonView.hide()
            when (it.status) {
                Status.SUCCESS -> {
                    limitList.clear()
                    limitList.addAll(it.data?.limitInfos!!)
                    svLimitAdapter?.notifyDataSetChanged()
                    initLimitList()
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun getGlLimitList() {
        val skeletonView = Skeleton.bind(binding.limitList).adapter(svLimitAdapter)
            .load(R.layout.shimmer_item_device).color(R.color.shimmer_color).show()
        viewModel.getGlCardLimitList(
            getClientToken(), GlLimitListRequest(object_id = card.object_value)
        ).observe(viewLifecycleOwner) {
            skeletonView.hide()
            when (it.status) {
                Status.SUCCESS -> {

                }

                Status.ERROR -> {

                }
            }
        }
    }

    private fun initLimitList() {
        if (limitList.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.addButton.visibility = View.VISIBLE
            binding.limitList.visibility = View.GONE
        } else {
            binding.limitList.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
            binding.addButton.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_button -> {
                val bundle = Bundle()
                bundle.putSerializable(Const.CARD, card)
                bundle.putString(Const.OPERATION, "set")
                gotoWithSlide(R.id.setCardLimitsFragment, bundle)
            }

            R.id.back -> {
                pop()
            }

            R.id.additional -> {
                val bundle = Bundle()
                bundle.putSerializable(Const.CARD, card)
                bundle.putString(Const.OPERATION, "set")
                gotoWithSlide(R.id.setCardLimitsFragment, bundle)
            }
        }
    }

    override fun openLimitItem(item: SvLimit) {
        val bundle = Bundle()
        bundle.putSerializable(Const.CARD, card)
        bundle.putString(Const.OPERATION, "edit")
        bundle.putSerializable("model", item)
        gotoWithSlide(R.id.setCardLimitsFragment, bundle)
    }
}