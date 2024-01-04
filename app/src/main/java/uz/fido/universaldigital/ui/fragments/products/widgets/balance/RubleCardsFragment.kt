package uz.fido.universaldigital.ui.fragments.products.widgets.balance

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robinhood.ticker.TickerUtils
import io.paperdb.Paper
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentBalaceByCardBinding
import uz.fido.universaldigital.ui.fragments.products.adapter.CardBalanceAdapter
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format

class RubleCardsFragment : BaseSimpleFragment<FragmentBalaceByCardBinding>(
    FragmentBalaceByCardBinding::inflate
), BaseInterface {

    private lateinit var currencyCards: ArrayList<CardResponse>

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        currencyCards = MainBalanceDialog.rubleCards
        initTickerView()
        initCardList(currencyCards)
        setTotalBalance()
        if (currencyCards.isEmpty()) binding.emptyText.visibility = View.VISIBLE
        binding.switchMainBalance.isClickable = currencyCards.isNotEmpty()
        binding.switchMainBalance.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Paper.book().write(Const.TOTAL_BALANCE_TYPE, 2)
                binding.switchMainBalance.isClickable = false
            } else {
                binding.switchMainBalance.isClickable = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.switchMainBalance.isChecked = Paper.book().read(Const.TOTAL_BALANCE_TYPE, 0) == 2
    }

    private fun initTickerView() {
        binding.tvTotalBalance.setCharacterLists(TickerUtils.provideNumberList())
        binding.tvTotalBalance.typeface =
            ResourcesCompat.getFont(requireContext(), uz.fido.utils.R.font.inter_bold)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initCardList(cards: ArrayList<CardResponse>) {
        val adapter = CardBalanceAdapter(requireContext(), cards) {
            setTotalBalance()
        }
        binding.cardList.adapter = adapter
        binding.cardList.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adapter.notifyDataSetChanged()
    }

    private fun setTotalBalance() {
        var totalBalance = 0.0
        currencyCards.forEach {
            if (it.balance_visibility) {
                totalBalance += it.balance.toDouble()
            }
        }
        binding.tvTotalBalance.text =
            Format.formatAmount(Format.convertFromTiynDivide(totalBalance.toString())) + " " + "RUB"
    }
}