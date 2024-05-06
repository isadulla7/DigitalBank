package uz.fido.network.domain.model.payment

import android.annotation.SuppressLint
import java.io.Serializable
import java.util.ArrayList

class PaymentGroup : Serializable {

    var icon_name: String? = ""
    var name_ru: String? = ""
    var name_uzl: String? = ""
    var name_en: String? = ""
    var order: Int? = 0
    var name_uzc: String? = ""
    var service_group_code: String? = ""
    var group_code: String? = ""
    var name: String? = ""
    var parent_service_group_code: String? = ""
    var service_list: ArrayList<PaymentService>? = ArrayList()
    var sub_group_list: ArrayList<PaymentGroup> = ArrayList()

    companion object {
        const val TABLE_NAME: String = "service_groups"
        const val SERVICE_GROUP_CODE = "service_group_code"
        const val INDEX_NAME_RU = "name_ru"
        const val INDEX_NAME_UC = "name_uzc"
        const val INDEX_NAME_UL = "name_uzl"
        const val INDEX_NAME_EN = "name_en"
        const val ICON_NAME = "icon_name"
        const val ORDER = "ord"
        const val COLUMN_PARENT_SERVICE = "parent_service_group_code"
        const val CONTRACT_ID = "contract_id"
        const val PAYMENT_DETAIL_CODE = "payment_detail_code"
    }

    @SuppressLint("NotConstructor")
    fun PaymentGroup(group_code: String, name: String, icon_name: String, order: Int, parent_service_group_code: String) {
        this.group_code = group_code
        this.name = name
        this.icon_name = icon_name
        this.order = order
        this.parent_service_group_code = parent_service_group_code
    }

}