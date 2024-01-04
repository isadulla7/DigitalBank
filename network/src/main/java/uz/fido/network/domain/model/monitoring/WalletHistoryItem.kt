package uz.fido.network.domain.model.monitoring

import fido.mkbank.ui.ib.fragments.menu.menu_monitoring.model.ListItem

class WalletHistoryItem : ListItem() {
    var walletItem: AccountHistory? = null
    override val type = TYPE_GENERAL
}