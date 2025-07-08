package uz.fido.network.domain.model.cards

import uz.fido.network.domain.model.abc_base.BaseResponse
import java.io.Serializable

data class CardResponse(
    var balance: String,
    val bank_name: String,
    var bg_icon_name: String,
    val currency_char: String,
    val currency_code: String,
    val embossed_name: String,
    val account_code: String,
    val bank_code: String,
    val address: String,
    var is_main: String,
    val object_expiry: String,
    val object_id: String,
    var object_name: String,
    val object_type: String,
    var object_value: String,
    var state: String,
    var stateName: String? = "",
    var safe_mode: String,
    var pay_with_sms: String,
    var pin_counter: Int? = 0,
    var processing_server_status: String? = "",
    var owerdraft_limit: String? = "0",
    var balance_visibility: Boolean = true,
    var card_selected: Boolean = true,
    var selected_for_monitoring: Boolean? = false,
    var is_target_object: String? = "",
    var error_message: String? = "",
    var isVirtual: String? = "N",
    var is_our_bank: String? = "N",
    var overdraft_limit: String? = "0",
    var object_status: String? = "",
    var is_Dv: String? = null,
    var savDepId: String? = null
) : BaseResponse(), Serializable