package uz.fido.network.domain.model.monitoring

class WalletHistoryItem : ListItem() {
    var walletItem: AccountHistory? = null
    override val type = TYPE_GENERAL
}