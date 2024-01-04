package uz.fido.universaldigital.ui.fragments.products.widgets.balance

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paperdb.Paper
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogMainBalanceSettingsBinding
import uz.fido.universaldigital.ui.fragments.products.adapter.PagerAdapter
import uz.fido.utils.const.Const

class MainBalanceDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogMainBalanceSettingsBinding
    private lateinit var adapter: PagerAdapter

    private var baseInterface: BaseInterface? = null
    private var cards: List<CardResponse>? = null

    companion object {
        var currencyCards = ArrayList<CardResponse>()
        var uzCards = ArrayList<CardResponse>()
        var rubleCards = ArrayList<CardResponse>()
        var euroCards = ArrayList<CardResponse>()
    }

    fun setListener(
        baseInterface: BaseInterface,
        cards: List<CardResponse>
    ) {
        this.baseInterface = baseInterface
        this.cards = cards
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onResume() {
        super.onResume()
        when (Paper.book().read(Const.TOTAL_BALANCE_TYPE, 0)) {
            0 -> {
                binding.viewpager.setCurrentItem(0, true)
            }

            1 -> {
                binding.viewpager.setCurrentItem(1, true)
            }

            2 -> {
                binding.viewpager.setCurrentItem(2, true)
            }

            3 -> {
                binding.viewpager.setCurrentItem(3, true)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMainBalanceSettingsBinding.inflate(inflater, container, false)
        uzCards.clear()
        currencyCards.clear()
        rubleCards.clear()
        euroCards.clear()

        cards?.forEach {
            when (it.currency_code) {
                "643" -> rubleCards.add(it)
                "978" -> euroCards.add(it)
                "840" -> currencyCards.add(it)
                else -> uzCards.add(it)
            }
        }
        initPages()
        return binding.root
    }

    private fun initPages() {
        adapter = PagerAdapter(requireContext(), childFragmentManager)
        adapter.addFragment(UzCardsFragment())
        adapter.addFragment(CurrencyCardsFragment())
        if (rubleCards.isNotEmpty()) {
            adapter.addFragment(RubleCardsFragment())
        }
        if (euroCards.isNotEmpty()) {
            adapter.addFragment(EuroCardsFragment())
        }
        binding.viewpager.adapter = adapter
        binding.tabLayout.setupWithViewPager(binding.viewpager)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        baseInterface?.dialogDismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        baseInterface?.dialogDismiss()
    }


}