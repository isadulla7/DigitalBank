package uz.fido.universaldigital.ui.fragments.products.widgets.search

import android.content.Context
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.fragments.products.widgets.search.model.SearchItem

object SearchList {

    var searchList = ArrayList<SearchItem>()

    const val GROUP_NAME_PAYMENT = "payment"
    const val GROUP_NAME_PAYMENT_GROUP = "payment_group"
    const val GROUP_NAME_APP_FUNCTIONALITY = "app_function"

    fun fillSearchList(context: Context) {
        searchList.add(
            SearchItem(
                name = context.getString(R.string.add_card),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "001"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.notifications),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_home_notification",
                id = "002"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.chat),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_chat",
                id = "003"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.my_cards),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "all_cards",
                id = "004"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.transfer_to_card),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_transfer_to_card",
                id = "005"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.my_deposits),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_deposit",
                id = "006"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.my_credits),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_my_credits",
                id = "007"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.humo_pay),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_services_humo_pay",
                id = "008"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.my_home),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_my_home",
                id = "009"
            )
        )

        searchList.add(
            SearchItem(
                name = context.getString(R.string.qr_payment),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_service_qr_payment",
                id = "010"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.by_requisites),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_transfer_by_requisites",
                id = "012"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.over_my_cards),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_transfer_over_my_cards",
                id = "014"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.by_phone_number),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_transfer_by_phone",
                id = "015"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.my_details),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_profile",
                id = "018"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.settings),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_settings",
                id = "019"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.security),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_security",
                id = "020"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.about_bank),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "info",
                id = "021"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.atm_and_filials),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "022"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.connect_with_bank),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "023"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.public_offer),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "024"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.change_pin_code),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "025"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.change_password),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "026"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.trusted_devices),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "027"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.change_language),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "028"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.app_theme),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "029"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.uzs_account),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_sum_account",
                id = "030"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.budget_payment),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_budget_payment",
                id = "032"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.order_card),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "035"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.open_deposit),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "037"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.open_wallet),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                id = "038"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.my_applications),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "application_icon",
                id = "040"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.connect_sms_info),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_service_connect_sms_info",
                id = "041"
            )
        )
        searchList.add(
            SearchItem(
                name = context.getString(R.string.auto_payments),
                groupName = GROUP_NAME_APP_FUNCTIONALITY,
                imageName = "ic_auto_payments",
                id = "044"
            )
        )
    }

    // profile
    // my details
    // Settings
    // Security
    // About bank
    // ATM and branches
    // Connect with bank
    // Public offer
    // Change PIN
    // Change Password
    // Trusted devices
    // Change Language
    // App theme
    // notifications
    // chat
    // to card
    // between your accounts
    // by phone number
    // by wallet number
    // request funds
    // order card
    // apply for a loan
    // open deposit
    // open wallet
    // my applications
    // transfer to account
    // connect humo sms
    // atms and branches
    // swift transfers
    // money transfers
    // loan repayment
    // auto payments
    // my home
    // templates
    // qr payment
    // history
}