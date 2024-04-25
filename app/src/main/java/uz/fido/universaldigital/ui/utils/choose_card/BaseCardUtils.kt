package uz.fido.universaldigital.ui.utils.choose_card

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import io.paperdb.Paper
import uz.fido.network.domain.model.cards.CardInfoDto
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.app.UniversalApplication
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.utils.const.CardConst.HUMO_CARD
import uz.fido.utils.const.CardConst.UZCARD
import uz.fido.utils.const.CardConst.WALLET
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format

object BaseCardUtils {

    fun Fragment.hasUserCard(): Boolean {
        val menuProductsViewModel =
            ViewModelProvider(requireActivity())[MenuProductsViewModel::class.java]
        return (menuProductsViewModel.cards.value?.size ?: 0) != 0
    }

    fun Fragment.hasUserCard(cardType: String): Boolean {
        val menuProductsViewModel =
            ViewModelProvider(requireActivity())[MenuProductsViewModel::class.java]
        val cardList = menuProductsViewModel.cards.value as ArrayList<CardResponse>
        cardList.forEach {
            if (it.object_type == cardType) {
                return true
            }
        }
        return false
    }

    @SuppressLint("SetTextI18n")
    fun TextView.setCardNameAndNumber(card: CardResponse) {
        this.text =
            Format.formatCardName(card.object_name) + " " + if (card.object_type != WALLET) Format.formatCardNumberNew(
                card.object_value
            ) else Format.formatWalletNumber(card.object_value)
    }

    fun TextView.setCardNumberFormatted(card: CardResponse) {
        this.text = if (card.object_type != WALLET) Format.formatCardNumberNew(
            card.object_value
        )
        else Format.formatWalletNumber(card.object_value)
    }

    fun TextView.setCardNumberFormatted(card: CardInfoDto) {
        this.text = if (card.card_type != WALLET) Format.formatCardNumberNew(
            card.card_number!!
        )
        else Format.formatWalletNumber(card.card_number!!)
    }

    fun TextView.setCardBalance(cardResponse: CardResponse) {
        val visibility = Paper.book().read<Boolean>(Const.BALANCE_VISIBILITY) ?: true
        if (visibility) {
            this.text = Format.formatAmountWithAppend(
                cardResponse.balance, cardResponse.currency_char
            )
        } else {
            this.text = "••• ••• ••• •••"
        }
    }

    fun ImageView.setCardTypeImage(card: CardResponse) {
        this.load(
            when (card.object_type) {
                UZCARD -> R.drawable.ic_uzcard_white_24
                HUMO_CARD -> R.drawable.ic_humo_white_24
                WALLET -> R.drawable.ic_card_type_wallet
                else -> {
                    if (card.object_value.startsWith("4")) {
                        R.drawable.ic_visa_white_24
                    } else {
                        R.drawable.ic_master_card_white_24
                    }
                }
            }
        )
    }

    fun compareWithBalance(amount: String, card: CardResponse): Boolean {
        return card.balance.toBigDecimal().divide(100.toBigDecimal())
            .compareTo(amount.toBigDecimal()) == -1
    }

    fun compareWithBalanceNd(amount: String, card: CardResponse): Boolean {
        return card.balance.toBigDecimal().divide(100.toBigDecimal())
            .compareTo(amount.toBigDecimal().divide(100.toBigDecimal())) == -1
    }

    fun getCardByType(
        cardType: Int, cardList: java.util.ArrayList<CardResponse>
    ): java.util.ArrayList<CardResponse> {
        val userCardList = java.util.ArrayList<CardResponse>()
        when (cardType) {
            1 -> {
                cardList.forEach {
                    if (it.object_type == "SV") {
                        if (it.object_value.startsWith("860055")) {
                            userCardList.add(it)
                        }
                    }
                }
            }

            2 -> {
                cardList.forEach {
                    if (it.object_type == "GL") {
                        if (it.object_value.startsWith("986009")) {
                            userCardList.add(it)
                        }
                    }
                }
            }

            3 -> {
                cardList.forEach {
                    if (it.object_type == "TET") {
                        userCardList.add(it)
                    }
                }
            }
        }
        return userCardList
    }

    private fun getBankLogo(cardNumber: String): Int {
        if (cardNumber.isNotEmpty() && cardNumber.length > 6) {
            return when (cardNumber.substring(0, 6)) {
                "860055", "986009", "626272" -> R.drawable.ic_bank_aab
                "860002", "986012" -> R.drawable.ic_bank_nbu
                "860006", "626291" -> R.drawable.ic_bank_xalq
                "860013","986004" -> R.drawable.ic_bank_asaka
                "860033","986001" -> R.drawable.ic_bank_ipoteka
                "860003","986002" -> R.drawable.ic_bank_sqb
                "860004","986003" -> R.drawable.ic_bank_agrobank
                "860009","986006" -> R.drawable.ic_bank_qqb
                "860011", "986015" -> R.drawable.ic_bank_turon
                "860005" ,"986013"-> R.drawable.ic_bank_mikrokredit
                "860031", "626247", "986019" -> R.drawable.ic_bank_aloqa
                "860014" ,"986017"-> R.drawable.ic_bank_ipakyoli
                "860049","986010" -> R.drawable.ic_bank_kapital
                "860053","986026" -> R.drawable.ic_bank_infin
                "860030","986018" -> R.drawable.ic_bank_trast
                "860038" -> R.drawable.ic_bank_turkiston
                "860051","986025" -> R.drawable.ic_bank_davr
                "860048", "986023" -> R.drawable.ic_bank_universal
                "860050","986024" -> R.drawable.ic_bank_ravnaq
                "860057","986027" -> R.drawable.ic_bank_ofb
                "860008","986014" -> R.drawable.ic_bank_savdogar
                "860012" ,"986016"-> R.drawable.ic_bank_hamkor
                "860034" ,"986020"-> R.drawable.ic_bank_kdb
                "986060" -> R.drawable.ic_bank_anor
                "986035" -> R.drawable.ic_bank_tbc

                else -> {
                    if (cardNumber.startsWith("AUZ") || cardNumber.startsWith("DV")) R.drawable.ic_bank_universal else 0
                }
            }
        } else {
            return 0
        }
    }

    private fun setObjectState(context: Context, walletState: String): String {
        return when (walletState) {
            "009" -> context.getString(R.string.special_bank_restrictions)
            "010" -> context.getString(R.string.account_closed_temporarily)
            "011" -> context.getString(R.string.account_permanently_closed)
            else -> ""
        }
    }

    fun isBankCard(card: CardResponse): Boolean {
        return card.object_value.startsWith("860048") || card.object_value.startsWith("626272") || card.object_value.startsWith(
            "986023"
        ) || card.object_value.startsWith("4685") || card.object_value.startsWith("4787") || card.object_value.startsWith(
            "511662"
        ) || card.object_value.startsWith("5130")
    }

    fun getCommand(senderCard: CardResponse, receiverCard: CardResponse): String {
        return when {
            senderCard.object_type == "KL" && receiverCard.object_type != "KL" -> "purse&card"
            senderCard.object_type != "KL" && receiverCard.object_type == "KL" -> "card&purse"
            senderCard.object_type == "KL" && receiverCard.object_type == "KL" -> "purse&purse"
            else -> "card&card"
        }
    }

    fun isMainCard(card: CardResponse): Boolean = card.is_main == "Y"

    fun TextView.setCardNumber(card: CardResponse) {
        this.text = if (card.object_type != "KL") Format.formatCardNumberNew(card.object_value)
        else Format.formatWalletNumber(card.object_value)
    }

    fun ImageView.setBankLogo(card: CardResponse) {
        if (getBankLogo(card.object_value) != 0) {
            this.visibility = View.VISIBLE
            this.load(getBankLogo(card.object_value))
        } else this.visibility = View.GONE
    }

    fun ImageView.setBankLogoForHumoPay(object_value: String) {
        if (getBankLogo(object_value) != 0) {
            this.visibility = View.VISIBLE
            this.load(getBankLogo(object_value))
        } else this.visibility = View.GONE
    }

    fun setCardState(
        card: CardResponse, context: Context, textView: TextView
    ) {
        when (card.processing_server_status) {
            "0" -> {
                textView.visibility = View.GONE
                return
            }

            "-100" -> {
                textView.visibility = View.VISIBLE
                textView.text = context.getString(R.string.can_not_receive_balance)
                return
            }

            null -> {
                textView.visibility = View.GONE
                return
            }

            else -> {
                textView.visibility = View.VISIBLE
                textView.text = card.stateName
            }
        }
    }


    fun CardResponse.isActive(): Boolean {
        return state == "0"
    }

    fun CardResponse.isNotActive(): Boolean {
        return state != "0" && state != "A"
    }

    fun CardResponse.isUniversalCard(): Boolean =
        this.object_value.startsWith("860048") ||
                this.object_value.startsWith("626283") ||
                this.object_value.startsWith("986023")

    fun CardResponse.isValidSumCard(): Boolean =
        currency_code == "000" && balance_visibility && object_type != "TET" && state == "0" && processing_server_status != "-100"

    fun CardResponse.isValidVisaCard(): Boolean =
        currency_code == "840" && balance_visibility && object_type == "TET" && state == "0" && processing_server_status != "-100"

    fun getLayoutManager(): GridLayoutManager {
        return try {
            if (Paper.book().read(Const.LAYOUT_MANAGER_GRID, true)) {
                GridLayoutManager(UniversalApplication.getContext(), 1)
            } else {
                GridLayoutManager(UniversalApplication.getContext(), 2)
            }
        } catch (e: Exception) {
            GridLayoutManager(UniversalApplication.getContext(), 1)
        }
    }

    fun getSpanCount(): Int {
        return if (Paper.book().read(Const.LAYOUT_MANAGER_GRID, true)) {
            1
        } else {
            2
        }
    }
}