package uz.fido.utils.view.amount

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import uz.fido.utils.R
import uz.fido.utils.databinding.AmountSuggestionViewBinding

/**
 * Created by Husniddin Muhammad Amin on 19.01.2023
 * Tashkent, Uzbekistan.
 */

class AmountSuggestionView(context: Context, attr: AttributeSet) :
    LinearLayoutCompat(context, attr) {

    private lateinit var amountSuggestionAdapter: AmountSuggestionAdapter
    private val binding: AmountSuggestionViewBinding

    init {
        inflate(context, R.layout.amount_suggestion_view, this)
        binding = AmountSuggestionViewBinding.bind(this)
    }

    fun initAmountSuggestions(
        amountType: AmountType, editText: TextInputEditText, swiftCurrency: String = ""
    ) {
        amountSuggestionAdapter = AmountSuggestionAdapter(swiftCurrency) {
            editText.setText(it)
        }
        when (amountType) {
            AmountType.AMOUNT_TYPE_P2P -> {
                amountSuggestionAdapter.submitList(getP2PAmounts())
            }

            AmountType.AMOUNT_TYPE_PAYMENT -> {
                amountSuggestionAdapter.submitList(getPaymentAmounts())
            }
        }
        binding.suggestions.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = amountSuggestionAdapter
        }
    }

    private fun getP2PAmounts(): ArrayList<String> {
        val amounts = ArrayList<String>()
        amounts.add("50 000")
        amounts.add("100 000")
        amounts.add("200 000")
        amounts.add("500 000")
        amounts.add("1 000 000")
        return amounts
    }

    private fun getPaymentAmounts(): ArrayList<String> {
        val amounts = ArrayList<String>()
        amounts.add("5 000")
        amounts.add("10 000")
        amounts.add("20 000")
        amounts.add("50 000")
        amounts.add("100 000")
        return amounts
    }

    enum class AmountType {
        AMOUNT_TYPE_P2P,
        AMOUNT_TYPE_PAYMENT,
    }

}