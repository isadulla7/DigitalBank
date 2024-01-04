package uz.fido.network.domain.model.cards

data class PriceItem(
    val name: String,
    val image_name: String,
    val design_price_id: Int,
    val design_price: Int,
    val card_type: Int
)