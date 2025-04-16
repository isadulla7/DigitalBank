package uz.fido.network.domain.model.payment

import android.annotation.SuppressLint
import java.io.Serializable

class PaymentParams : Serializable {

    var name: String? = null
    var hint: String? = null

    var icon_name: String? = ""
    var name_ru: String? = ""
    var name_uzc: String? = ""
    var name_uzl: String? = ""
    var name_en: String? = ""
    var hint_ru: String? = ""
    var hint_uzc: String? = ""
    var hint_uzl: String? = ""
    var hint_en: String? = ""
    var is_visible: String? = ""
    var param_type: String? = ""
    var ord: String? = "0"
    var param_length: String? = ""
    var is_required: String? = ""
    var level_position: String? = ""
    var is_read_only: String? = ""
    var payment_detail_code: String? = ""
    var code: String = ""
    var mondatory: String? = ""
    var group_ord: String? = ""
    var def_value: String = ""
    var ref_code: String? = ""
    var regular_exp_mask: String? = ""
    var prefix: String? = ""
    var settlement: String? = ""
    var field_mask: String? = ""

    companion object {
        const val TABLE_NAME = "payment_details"

        const val COLUMN_ICON_NAME = "icon_name"
        const val COLUMN_INDEX_NAME_RU = "name_ru"
        const val COLUMN_INDEX_NAME_UC = "name_uzc"
        const val COLUMN_INDEX_NAME_UL = "name_uzl"
        const val COLUMN_INDEX_NAME_EN = "name_en"
        const val COLUMN_INDEX_HINT_RU = "hint_ru"
        const val COLUMN_INDEX_HINT_UC = "hint_uzc"
        const val COLUMN_INDEX_HINT_UL = "hint_uzl"
        const val COLUMN_INDEX_HINT_EN = "hint_en"
        const val COLUMN_IS_VISIBLE = "is_visible"
        const val COLUMN_PARAM_TYPE = "param_type"
        const val COLUMN_ORDER = "ord"
        const val COLUMN_PARAM_LENGTH = "param_length"
        const val COLUMN_IS_REQUIRED = "is_required"
        const val COLUMN_LEVEL_POSITION = "level_position"
        const val COLUMN_IS_READ_ONLY = "is_read_only"
        const val COLUMN_PAYMENT_DETAIL_CODE = "payment_detail_code"
        const val COLUMN_CODE = "code"
        const val COLUMN_MANDATORY = "mondatory"
        const val COLUMN_GROUP_ORD = "group_ord"
        const val COLUMN_DEF_VALUE = "def_value"
        const val COLUMN_REF_CODE = "ref_code"
        const val COLUMN_REGULAR_EXP_MASK = "regular_exp_mask"
        const val COLUMN_PREFIX = "prefix"
        const val COLUMN_SETTLEMENT = "settlement"
        const val COLUMN_MASK = "field_mask"
    }

    @SuppressLint("NotConstructor")
    fun PaymentParams(
        payment_detail_code: String,
        is_visible: String,
        param_type: String,
        ord: String,
        param_length: String,
        is_required: String,
        is_read_only: String,
        code: String,
        mandatory: String,
        group_ord: String,
        def_value: String,
        icon_name: String,
        level_position: String,
        name: String,
        hint: String,
        ref_code: String,
        regular_exp_mask: String,
        prefix: String,
        settlement: String,
        mask: String
    ): PaymentParams {
        this.payment_detail_code = payment_detail_code
        this.is_visible = is_visible
        this.param_type = param_type
        this.ord = ord
        this.param_length = param_length
        this.is_required = is_required
        this.is_read_only = is_read_only
        this.code = code
        this.mondatory = mandatory
        this.group_ord = group_ord
        this.def_value = def_value
        this.icon_name = icon_name
        this.level_position = level_position
        this.name = name
        this.hint = hint
        this.ref_code = ref_code
        this.regular_exp_mask = regular_exp_mask
        this.prefix = prefix
        this.settlement = settlement
        this.field_mask = mask
        return this
    }

    override fun toString(): String {
        return "\npayment_detail_code: ${payment_detail_code}\n" +
                "is_visible: ${is_visible}\n" +
                "param_type: ${param_type}\n" +
                "ord: ${ord}\n" +
                "param_length: ${param_length}\n" +
                "is_required: ${is_required}\n" +
                "is_read_only: ${is_read_only}\n" +
                "code: ${code}\n" +
                "mondatory: ${mondatory}\n" +
                "group_ord: ${group_ord}\n" +
                "def_value: $def_value\n" +
                "level_position: $level_position\n" +
                "name: $name\n" +
                "hint: $hint\n" +
                "ref_code: $ref_code\n" +
                "regular_exp_mask: $regular_exp_mask\n" +
                "settlement: $settlement\n" +
                "mask: $field_mask\n" +
                "prefix: $prefix\n"
    }

}