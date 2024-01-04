package uz.fido.network.domain.model.popular_transfers


data class PopularTransferResponse(
    val code: Int,
    val msg: String,
    val popular_transfers: ArrayList<PopularTransfers>
)