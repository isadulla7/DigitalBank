package uz.fido.universaldigital.ui.utils.choose_card

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.databinding.ChooseCardLayoutBinding
import uz.fido.universaldigital.ui.utils.choose_card.BaseCardUtils.isUniversalCard
import uz.fido.utils.R
import uz.fido.utils.device.vibrateTick


/**
 * Created by Husniddin Muhammad Amin on 19.01.2023
 * Tashkent, Uzbekistan.
 */

class ChooseCardLayout(context: Context, attr: AttributeSet) : ConstraintLayout(context, attr) {

    private val binding: ChooseCardLayoutBinding
    init {
        inflate(context, uz.fido.universaldigital.R.layout.choose_card_layout, this)
        binding = ChooseCardLayoutBinding.bind(this)
        val attributes = context.obtainStyledAttributes(attr, R.styleable.ChooseCardLayout)
        attributes.recycle()
    }


    fun initCards(
        cards: ArrayList<CardResponse>,
        minAmount: String? = null,
        currencyChar: String? = null,
        addCard:()->Unit = {},
        scrollListener: (CardResponse?) -> Unit
    ) {
        val sortedCardList = filterCardsByCurrency(currencyChar, cards)
        if (sortedCardList.isNotEmpty()) {
            binding.noCards.visibility = View.GONE
            val snapHelper: SnapHelper = PagerSnapHelper()
            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.cards.apply {
                adapter = ChooseCardAdapter(sortedCardList, minAmount)
                layoutManager = linearLayoutManager
                onFlingListener = null
                snapHelper.attachToRecyclerView(this)
                scrollListener.invoke(sortedCardList[0])

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            try {
                                snapHelper.findSnapView(linearLayoutManager)?.let { view ->
                                    linearLayoutManager.getPosition(view).let { position ->
                                        vibrateTick(context)
                                        scrollListener.invoke(sortedCardList[position])
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                })
            }
        } else {
            binding.noCards.visibility = View.VISIBLE
            scrollListener.invoke(null)
        }
        binding.noCards.setOnClickListener {
            addCard()
        }
    }

    fun getUniversalCards(
        cards: ArrayList<CardResponse>,
        minAmount: String? = null,
        currencyChar: String? = null,
        scrollListener: (CardResponse?) -> Unit
    ) {
        val sortedCardList = getUniversalCards(currencyChar, cards)
        if (sortedCardList.isNotEmpty()) {
            binding.noCards.visibility = View.GONE
            val snapHelper: SnapHelper = PagerSnapHelper()
            val linearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.cards.apply {
                adapter = ChooseCardAdapter(sortedCardList, minAmount)
                layoutManager = linearLayoutManager
                onFlingListener = null
                snapHelper.attachToRecyclerView(this)
                scrollListener.invoke(sortedCardList[0])

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            try {
                                snapHelper.findSnapView(linearLayoutManager)?.let { view ->
                                    linearLayoutManager.getPosition(view).let { position ->
                                        vibrateTick(context)
                                        scrollListener.invoke(sortedCardList[position])
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                })
            }
        } else {
            binding.noCards.visibility = View.VISIBLE
            scrollListener.invoke(null)
        }
    }

    private fun filterCardsByCurrency(
        currencyChar: String? = null,
        cardList: ArrayList<CardResponse>
    ): ArrayList<CardResponse> {
        val sortedList = ArrayList<CardResponse>()
        if (currencyChar == null) return cardList
        cardList.forEach {
            if (it.currency_char == currencyChar) {
                sortedList.add(it)
            }
        }
        return sortedList
    }

    private fun getUniversalCards(
        currencyChar: String? = null,
        cardList: ArrayList<CardResponse>
    ): ArrayList<CardResponse> {
        val sortedList = ArrayList<CardResponse>()
        if (currencyChar == null) return cardList
        cardList.forEach {
            if (it.currency_char == currencyChar && it.isUniversalCard()) {
                sortedList.add(it)
            }
        }
        return sortedList
    }

}