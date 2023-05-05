package uz.fido.network.domain.model.payment

class PaymentReference {

    var name_ru: String? = ""
    var name_en: String? = ""
    var name_uzc: String? = ""
    var name_uzl: String? = ""
    var order: Int? = 0
    var code: String? = ""
    var ref_code: String? = ""
    var name: String? = ""
    var flag: String? = ""

    companion object {
        const val TABLE_NAME = "references_list"
        const val COLUMN_NAME_RU = "name_ru"
        const val COLUMN_NAME_EN = "name_en"
        const val COLUMN_NAME_UC = "name_uzc"
        const val COLUMN_NAME_UL = "name_uzl"
        const val COLUMN_ORDER = "ord"
        const val COLUMN_CODE = "code"
        const val COLUMN_FLAG = "flag"
        const val COLUMN_REF_CODE = "ref_code"
    }

    fun PaymentReference(ref_code: String, code: String, order: Int, name: String, flag: String): PaymentReference {
        this.ref_code = ref_code
        this.code = code
        this.order = order
        this.name = name
        this.flag = flag
        return this
    }

}