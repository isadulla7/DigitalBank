package uz.fido.network.domain.model.cards

data class ProductType(
    val price: Int,
    val name: String,
    val code: String,
    val card_type: Int,
    val delivery_amount: Int,
    val transact_process_perc: String,
    val is_virtual: String,
    val card_validity_period: String,
    val currency: String,
    val description: String,
    val insurance_deposit: String,
    val design_price_list: ArrayList<PriceItem>,
    val is_allowed_user_types: ArrayList<Int>
)