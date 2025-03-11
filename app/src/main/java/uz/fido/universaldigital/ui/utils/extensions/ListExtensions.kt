package uz.fido.universaldigital.ui.utils.extensions

import android.content.Context
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.load
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.fragments.products.model.BankProducts
import uz.fido.universaldigital.ui.fragments.products.model.FastAccessOperation
import uz.fido.universaldigital.ui.utils.recyclerview.MenuServiceItem
import uz.fido.utils.const.CardConst.HUMO_CARD
import uz.fido.utils.const.CardConst.UZCARD
import uz.fido.utils.const.CardConst.WALLET

fun Fragment.getServiceList(): ArrayList<MenuServiceItem> {
    val serviceList = ArrayList<MenuServiceItem>()
    serviceList.add(
        MenuServiceItem(
            0,
            getString(R.string.bank_products),
            1
        )
    )
    serviceList.add(
        MenuServiceItem(
            icon = R.drawable.ic_3d_order_card,
            serviceName = getString(R.string.order_card),
            serviceId = 2,
            serviceDescription = getString(R.string.order_card_description)
        )
    )
    serviceList.add(
        MenuServiceItem(
            icon = R.drawable.ic_3d_apply_loan,
            serviceName = getString(R.string.apply_loan),
            serviceId = 3,
            serviceDescription = getString(R.string.apply_loan_description)
        )
    )
    serviceList.add(
        MenuServiceItem(
            icon = R.drawable.ic_3d_open_deposit,
            serviceName = getString(R.string.open_deposit),
            serviceId = 4,
            serviceDescription = getString(R.string.open_deposit_description)
        )
    )
    serviceList.add(
        MenuServiceItem(
            icon = R.drawable.ic_3d_open_wallet,
            serviceName = getString(R.string.open_wallet),
            serviceId = 5,
            serviceDescription = getString(R.string.open_wallet_description)
        )
    )
    serviceList.add(
        MenuServiceItem(
            0,
            getString(R.string.real_services),
            6
        )
    )

    serviceList.add(
        MenuServiceItem(
            R.drawable.all_cards,
            getString(R.string.my_cards),
            100
        )
    )

    serviceList.add(
        MenuServiceItem(
            R.drawable.ic_deposit,
            getString(R.string.my_deposits),
            101
        )
    )

    serviceList.add(
        MenuServiceItem(
            R.drawable.ic_my_credits,
            getString(R.string.my_credits),
            102
        )
    )

    serviceList.add(
        MenuServiceItem(
            R.drawable.application_icon,
            getString(R.string.my_applications),
            202
        )
    )
//    serviceList.add(
//        MenuServiceItem(
//            R.drawable.ic_service_transfer_to_account,
//            getString(R.string.transfer_to_account),
//            500
//        )
//    )
    serviceList.add(
        MenuServiceItem(
            R.drawable.ic_service_connect_sms_info,
            getString(R.string.connect_sms_info),
            600
        )
    )
//    serviceList.add(
//        MenuServiceItem(
//            R.drawable.ic_service_debts,
//            getString(R.string.check_debts),
//            700
//        )
//    )
//    serviceList.add(
//        MenuServiceItem(
//            R.drawable.ic_goal_image,
//            getString(R.string.goal),
//            201
//        )
//    )
    serviceList.add(
        MenuServiceItem(
            R.drawable.ic_atm_filial,
            getString(R.string.atm_and_filials),
            900
        )
    )
    serviceList.add(
        MenuServiceItem(
            R.drawable.ic_transfer_swift,
            getString(R.string.swift_transfer),
            801
        )
    )
    serviceList.add(
        MenuServiceItem(
            R.drawable.ic_service_money_transfers,
            getString(R.string.money_transfers),
            800
        )
    )
    serviceList.add(
        MenuServiceItem(
            R.drawable.ic_service_payment_in_places,
            getString(R.string.payment_in_places),
            200
        )
    )
    return serviceList
}

fun Fragment.getTransferTypes(): ArrayList<MenuServiceItem> {
    val transferTypes = ArrayList<MenuServiceItem>()
    transferTypes.add(
        MenuServiceItem(
            icon = R.drawable.ic_transfer_to_card,
            serviceName = getString(R.string.to_card),
            serviceId = 100,
            serviceDescription = getString(R.string.to_card_desc)
        )
    )
    transferTypes.add(
        MenuServiceItem(
            R.drawable.ic_transfer_over_my_cards,
            getString(R.string.over_my_cards),
            200,
            serviceDescription = getString(R.string.over_my_cards_desc)
        )
    )
    transferTypes.add(
        MenuServiceItem(
            R.drawable.ic_transfer_by_phone,
            getString(R.string.by_phone_number),
            300,
            getString(R.string.by_phone_number_desc)
        )
    )
    transferTypes.add(
        MenuServiceItem(
            R.drawable.ic_transfer_by_wallet,
            getString(R.string.by_wallet_number),
            400,
            getString(R.string.by_wallet_number_desc)
        )
    )
    transferTypes.add(
        MenuServiceItem(
            R.drawable.ic_service_transfer_to_account,
            getString(R.string.by_requisites),
            500,
            getString(R.string.transfer_to_account)
        )
    )
    transferTypes.add(
        MenuServiceItem(
            R.drawable.ic_transfer_request_money,
            getString(R.string.request_money),
            600,
            getString(R.string.request_money_desc)
        )
    )
//    transferTypes.add(
//        MenuServiceItem(
//            R.drawable.ic_conversion_24dp,
//            getString(R.string.currency_exchange),
//            700,
//            getString(R.string.currency_exchange)
//        )
//    )
    return transferTypes
}

fun getFastAccessOperationList(context: Context): ArrayList<FastAccessOperation> {
    val operations = ArrayList<FastAccessOperation>()
    operations.add(
        FastAccessOperation(
            id = 10,
            name = context.getString(R.string.my_products),
            icon = "ic_my_products",
            isVisible = true
        )
    )
    operations.add(
        FastAccessOperation(
            id = 11,
            name = context.getString(R.string.transfer_to_card),
            icon = "ic_transfer_to_card",
            isVisible = true
        )
    )
    operations.add(
        FastAccessOperation(
            id = 18,
            name = context.getString(R.string.my_deposits),
            icon = "ic_deposit",
            isVisible = true
        )
    )
    operations.add(
        FastAccessOperation(
            id = 19,
            name = context.getString(R.string.my_credits),
            icon = "ic_my_credits",
            isVisible = true
        )
    )
    operations.add(
        FastAccessOperation(
            id = 121,
            name = context.getString(R.string.my_home),
            icon = "ic_my_home",
            isVisible = true
        )
    )
    operations.add(
        FastAccessOperation(
            id = 13,
            name = context.getString(R.string.qr_payment),
            icon = "ic_service_qr_payment"
        )
    )
    operations.add(
        FastAccessOperation(
            id = 14,
            name = context.getString(R.string.currency_exchange),
            icon = "ic_conversion_24dp",
            isVisible = true
        )
    )
    operations.add(
        FastAccessOperation(
            id = 15,
            name = context.getString(R.string.by_requisites),
            icon = "ic_transfer_by_requisites"
        )
    )
    operations.add(
        FastAccessOperation(
            id = 16,
            name = context.getString(R.string.swift_transfer),
            icon = "ic_transfer_swift"
        )
    )
//    operations.add(
//        FastAccessOperation(
//            id = 20,
//            name = context.getString(R.string.loan_repayment),
//            icon = "ishonch_png"
//        )
//    )
    return operations
}

fun getBankProducts(context: Context): ArrayList<BankProducts> {
    val operations = ArrayList<BankProducts>()
    operations.add(
        BankProducts(
            id = 1, name = context.getString(R.string.for_you_p2p), icon = "ic_3d_p2p"
        )
    )
    operations.add(
        BankProducts(
            id = 2,
            name = context.getString(R.string.for_you_online_conversion),
            icon = "ic_3d_conversion"
        )
    )
    operations.add(
        BankProducts(
            id = 3,
            name = context.getString(R.string.for_you_target),
            icon = "ic_3d_target"
        )
    )
    return operations
}

fun bankProductsWithLargeImage(context: Context): ArrayList<BankProducts> {
    val operations = ArrayList<BankProducts>()
    operations.add(
        BankProducts(
            id = 0,
            name = context.getString(R.string.bank_product_1),
            icon = "ic_bank_product_illustration_2",
            description = context.getString(R.string.bank_product_desc_1)
        )
    )
    operations.add(
        BankProducts(
            id = 1,
            name = context.getString(R.string.bank_product_2),
            icon = "ic_bank_product_illustration_3",
            description = context.getString(R.string.bank_product_desc_2)
        )
    )
    operations.add(
        BankProducts(
            id = 2,
            name = context.getString(R.string.bank_product_3),
            icon = "ic_bank_product_illustration_1",
            description = context.getString(R.string.bank_product_desc_3)
        )
    )
    operations.add(
        BankProducts(
            id = 3,
            name = context.getString(R.string.microloan),
            icon = "ic_bank_product_illustration_4",
            description = context.getString(R.string.bank_product_desc_4)
        )
    )
    return operations
}

fun cardLogoByType(card: CardResponse): Int {
    return when (card.object_type) {
        UZCARD -> R.drawable.ic_card_type_uzcard
        HUMO_CARD -> R.drawable.ic_card_type_humo
        WALLET -> R.drawable.ic_card_type_wallet_gray
        else -> {
            if (card.object_value.startsWith("4")) {
                R.drawable.ic_card_type_visa
            } else {
                R.drawable.ic_card_type_master
            }
        }
    }
}

fun cardLogoByType(cardType: String): Int {
    return when (cardType) {
        UZCARD -> R.drawable.ic_card_type_uzcard
        HUMO_CARD -> R.drawable.ic_card_type_humo
        WALLET -> R.drawable.ic_card_type_wallet_gray
        else -> {
            if (cardType.startsWith("4")) {
                R.drawable.ic_card_type_visa
            } else {
                R.drawable.ic_card_type_master
            }
        }
    }
}

fun cardMiniLogoByType(cardType: String): Int {
    return when (cardType) {
        UZCARD -> R.drawable.ic_card_type_uzcard_16
        HUMO_CARD -> R.drawable.ic_card_type_humo_16
        WALLET -> R.drawable.ic_card_type_wallet_16
        else -> {
            if (cardType.startsWith("4")) {
                R.drawable.ic_card_type_visa_16
            } else {
                R.drawable.ic_card_type_master_card_16
            }
        }
    }
}

fun whiteCardLogoByType(card: CardResponse): Int {
    return when (card.object_type) {
        UZCARD -> R.drawable.ic_card_type_uzcard
        HUMO_CARD -> R.drawable.ic_card_type_humo
        WALLET -> R.drawable.ic_card_type_wallet_gray
        else -> {
            if (card.object_value.startsWith("4")) {
                R.drawable.ic_card_type_visa
            } else {
                R.drawable.ic_card_type_master
            }
        }
    }
}

fun ImageView.loadPaymentIcon(groupCode: String) {
    val icon = when (groupCode) {
        "2" -> R.drawable.payment_mobile
        "3" -> R.drawable.others
        "4" -> R.drawable.payment_internet
        "5" -> R.drawable.ic_payment_communal
        "7" -> R.drawable.ic_payment_gos_uslugi
        "9" -> R.drawable.payment_loan
        "10" -> R.drawable.payment_insurance
        "11" -> R.drawable.payment_taxi
        "12" -> R.drawable.payment_game
        "13" -> R.drawable.ic_payment_education
        "14" -> R.drawable.ic_wallet_blue
        "15" -> R.drawable.payment_foreign
        "16" -> R.drawable.ic_payment_charity
        "17" -> R.drawable.payment_internet_services
        "18" -> R.drawable.ic_internet_services
        "21" -> R.drawable.ic_credit_operations
        else -> R.drawable.others
    }
    this.load(icon) {
        crossfade(true)
    }
}

fun Context.getDrawableFromRes(name: String): Int {
    val resId = this.resources.getIdentifier(name, "drawable", this.packageName)
    return if (resId != 0) {
        resId
    } else R.drawable.bg_0
}

fun getCardBackgroundList(): ArrayList<String> {
    val cardBgNames = ArrayList<String>()
    cardBgNames.add("bg_0")
    cardBgNames.add("bg_1")
    cardBgNames.add("bg_2")
    cardBgNames.add("bg_3")
    cardBgNames.add("bg_4")
    cardBgNames.add("bg_5")
    cardBgNames.add("bg_6")
    cardBgNames.add("bg_7")
    cardBgNames.add("bg_8")
    cardBgNames.add("bg_9")
    cardBgNames.add("bg_10")
    cardBgNames.add("bg_11")
    cardBgNames.add("bg_12")
    cardBgNames.add("bg_13")
    cardBgNames.add("bg_14")
    return cardBgNames
}

fun ImageView.loadCardBackgroundImage(cardResponse: CardResponse) {
    val icon = when (cardResponse.bg_icon_name) {
        "bg_0" -> R.drawable.bg_0
        "bg_1" -> R.drawable.bg_1
        "bg_2" -> R.drawable.bg_2
        "bg_3" -> R.drawable.bg_3
        "bg_4" -> R.drawable.bg_4
        "bg_5" -> R.drawable.bg_5
        "bg_6" -> R.drawable.bg_6
        "bg_7" -> R.drawable.bg_7
        "bg_8" -> R.drawable.bg_8
        "bg_9" -> R.drawable.bg_9
        "bg_10" -> R.drawable.bg_10
        "bg_11" -> R.drawable.bg_11
        "bg_12" -> R.drawable.bg_12
        "bg_13" -> R.drawable.bg_13
        "bg_14" -> R.drawable.bg_14
        else -> R.drawable.bg_1
    }
    this.load(icon)
}