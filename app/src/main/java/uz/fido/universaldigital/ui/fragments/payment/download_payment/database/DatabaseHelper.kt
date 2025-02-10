package uz.fido.universaldigital.ui.fragments.payment.download_payment.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uz.fido.network.domain.model.payment.LanguageUtils
import uz.fido.network.domain.model.payment.PaymentCashback
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentReference
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.universaldigital.ui.utils.lang.LocaleHelper
import uz.fido.utils.const.Const
import uz.fido.utils.log.Logger
import java.sql.SQLException

class DatabaseHelper(
    private val context: Context,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    constructor(context: Context) : this(
        context = context,
        name = "universal_digital_payment",
        factory = null,
        version = Const.DB_HELPER_VERSION
    )

    override fun onCreate(db: SQLiteDatabase?) {}

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    init {
        try {
            createPaymentTables()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createPaymentTables() {
        val database = this.writableDatabase
        database?.execSQL(
            "CREATE TABLE IF NOT EXISTS " +
                    "${PaymentGroup.TABLE_NAME}(" +
                    "${PaymentGroup.ICON_NAME} TEXT," +
                    "${PaymentGroup.INDEX_NAME_RU} TEXT," +
                    "${PaymentGroup.INDEX_NAME_UL} TEXT," +
                    "${PaymentGroup.INDEX_NAME_UC} TEXT," +
                    "${PaymentGroup.INDEX_NAME_EN} TEXT," +
                    "${PaymentGroup.SERVICE_GROUP_CODE} TEXT," +
                    "${PaymentGroup.COLUMN_PARENT_SERVICE} TEXT," +
                    "${PaymentGroup.ORDER} INTEGER)"
        )
        database?.execSQL(
            "CREATE TABLE IF NOT EXISTS " +
                    "${PaymentService.TABLE_NAME}(" +
                    "${PaymentService.ICON_NAME} TEXT," +
                    "${PaymentService.INDEX_NAME_RU} TEXT," +
                    "${PaymentService.INDEX_NAME_UL} TEXT," +
                    "${PaymentService.INDEX_NAME_UC} TEXT," +
                    "${PaymentService.INDEX_NAME_EN} TEXT," +
                    "${PaymentService.SERVICE_GROUP_CODE} TEXT," +
                    "${PaymentService.SERVICE_ID} TEXT," +
                    "${PaymentService.PAYMENT_DETAIL_CODE} TEXT," +
                    "${PaymentService.PAYMENT_TYPE} TEXT," +
                    "${PaymentService.ORDER} INTEGER," +
                    "${PaymentService.MIN_AMOUNT} TEXT," +
                    "${PaymentService.MAX_AMOUNT} TEXT," +
                    "${PaymentService.COLUMN_SMS_CONTROL_LIMIT} TEXT," +
                    "${PaymentService.COLUMN_IDENTIFICATION} TEXT," +
                    "${PaymentService.PAY_REQUEST_METHOD} TEXT)"
        )
        database?.execSQL(
            "CREATE TABLE IF NOT EXISTS " +
                    "${PaymentReference.TABLE_NAME}(" +
                    "${PaymentReference.COLUMN_NAME_RU} TEXT," +
                    "${PaymentReference.COLUMN_NAME_UL} TEXT," +
                    "${PaymentReference.COLUMN_NAME_UC} TEXT," +
                    "${PaymentReference.COLUMN_NAME_EN} TEXT," +
                    "${PaymentReference.COLUMN_ORDER} INTEGER," +
                    "${PaymentReference.COLUMN_CODE} TEXT," +
                    "${PaymentReference.COLUMN_FLAG} TEXT," +
                    "${PaymentReference.COLUMN_REF_CODE} TEXT)"
        )
        database?.execSQL(
            "CREATE TABLE IF NOT EXISTS " +
                    "${PaymentCashback.TABLE_NAME}(" +
                    "${PaymentCashback.COLUMN_GL_PERCENT} TEXT," +
                    "${PaymentCashback.COLUMN_SERVICE_ID} TEXT," +
                    "${PaymentCashback.COLUMN_STATE} TEXT," +
                    "${PaymentCashback.COLUMN_SV_PERCENT} TEXT," +
                    "${PaymentCashback.COLUMN_TET_PERCENT} TEXT," +
                    "${PaymentCashback.COLUMN_KL_PERCENT} TEXT)"
        )
        database?.execSQL(
            "CREATE TABLE IF NOT EXISTS " +
                    "${PaymentParams.TABLE_NAME}(" +
                    "${PaymentParams.COLUMN_ICON_NAME} TEXT, " +
                    "${PaymentParams.COLUMN_INDEX_NAME_RU} TEXT," +
                    "${PaymentParams.COLUMN_INDEX_NAME_UC} TEXT," +
                    "${PaymentParams.COLUMN_INDEX_NAME_UL} TEXT," +
                    "${PaymentParams.COLUMN_INDEX_NAME_EN} TEXT," +
                    "${PaymentParams.COLUMN_INDEX_HINT_RU} TEXT," +
                    "${PaymentParams.COLUMN_INDEX_HINT_UC} TEXT," +
                    "${PaymentParams.COLUMN_INDEX_HINT_UL} TEXT," +
                    "${PaymentParams.COLUMN_INDEX_HINT_EN} TEXT," +
                    "${PaymentParams.COLUMN_IS_VISIBLE} TEXT," +
                    "${PaymentParams.COLUMN_PARAM_TYPE} TEXT," +
                    "${PaymentParams.COLUMN_ORDER} TEXT," +
                    "${PaymentParams.COLUMN_PARAM_LENGTH} TEXT," +
                    "${PaymentParams.COLUMN_IS_REQUIRED} TEXT," +
                    "${PaymentParams.COLUMN_LEVEL_POSITION} TEXT," +
                    "${PaymentParams.COLUMN_IS_READ_ONLY} TEXT," +
                    "${PaymentParams.COLUMN_PAYMENT_DETAIL_CODE} TEXT," +
                    "${PaymentParams.COLUMN_CODE} TEXT," +
                    "${PaymentParams.COLUMN_MANDATORY} TEXT," +
                    "${PaymentParams.COLUMN_GROUP_ORD} TEXT," +
                    "${PaymentParams.COLUMN_DEF_VALUE} TEXT," +
                    "${PaymentParams.COLUMN_REF_CODE} TEXT," +
                    "${PaymentParams.COLUMN_PREFIX} TEXT," +
                    "${PaymentParams.COLUMN_SETTLEMENT} TEXT," +
                    "${PaymentParams.COLUMN_MASK} TEXT," +
                    "${PaymentParams.COLUMN_REGULAR_EXP_MASK} TEXT)"
        )
    }

    fun clearAll() {
        clearGroupListTable()
        clearServiceListTable()
        clearPaymentDetailTable()
        clearRefParamTable()
        clearCashbackTable()
    }

    private fun clearGroupListTable() {
        try {
            writableDatabase.execSQL("DELETE FROM " + PaymentGroup.TABLE_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearServiceListTable() {
        try {
            writableDatabase.execSQL("DELETE FROM " + PaymentService.TABLE_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearPaymentDetailTable() {
        try {
            writableDatabase.execSQL("DELETE FROM " + PaymentParams.TABLE_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearRefParamTable() {
        try {
            writableDatabase.execSQL("DELETE FROM " + PaymentReference.TABLE_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearCashbackTable() {
        try {
            writableDatabase.execSQL("DELETE FROM " + PaymentCashback.TABLE_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun insertServiceGroups(groupList: List<PaymentGroup>) {
        val db = this.writableDatabase
        groupList.sortedBy { it.order }
        for (paymentGroup in groupList) {
            try {
                val values = ContentValues()
                values.put(PaymentGroup.SERVICE_GROUP_CODE, paymentGroup.service_group_code)
                values.put(PaymentGroup.INDEX_NAME_RU, paymentGroup.name_ru)
                values.put(PaymentGroup.INDEX_NAME_UC, paymentGroup.name_uzc)
                values.put(PaymentGroup.INDEX_NAME_UL, paymentGroup.name_uzl)
                values.put(PaymentGroup.INDEX_NAME_EN, paymentGroup.name_en)
                values.put(PaymentGroup.ICON_NAME, paymentGroup.icon_name)
                values.put(PaymentGroup.ORDER, paymentGroup.order)
                values.put(
                    PaymentGroup.COLUMN_PARENT_SERVICE,
                    paymentGroup.parent_service_group_code
                )
                db.insert(PaymentGroup.TABLE_NAME, null, values)
            } catch (ex: Exception) {
                Logger.writeErrorLog(ex.localizedMessage.orEmpty())
            }
        }
        db.close()
    }

    fun insertCashbackList(list: List<PaymentCashback>) {
        val db = this.writableDatabase
        for (paymentCashback in list) {
            try {
                val values = ContentValues()
                values.put(PaymentCashback.COLUMN_GL_PERCENT, paymentCashback.gl_percent)
                values.put(PaymentCashback.COLUMN_SERVICE_ID, paymentCashback.service_id)
                values.put(PaymentCashback.COLUMN_STATE, paymentCashback.state)
                values.put(PaymentCashback.COLUMN_SV_PERCENT, paymentCashback.sv_percent)
                values.put(PaymentCashback.COLUMN_TET_PERCENT, paymentCashback.tet_percent)
                values.put(PaymentCashback.COLUMN_KL_PERCENT, paymentCashback.kl_percent)
                db.insert(PaymentCashback.TABLE_NAME, null, values)
            } catch (ex: Exception) {
                Logger.writeErrorLog(ex.localizedMessage!!)
            }
        }
        db.close()
    }

    fun insertServiceList(serviceList: List<PaymentService>) {
        val db = writableDatabase
        for (service in serviceList) {
            try {
                val values = ContentValues()
                values.put(PaymentService.SERVICE_GROUP_CODE, service.service_group_code)
                values.put(PaymentService.INDEX_NAME_RU, service.name_ru)
                values.put(PaymentService.INDEX_NAME_UC, service.name_uzc)
                values.put(PaymentService.INDEX_NAME_UL, service.name_uzl)
                values.put(PaymentService.INDEX_NAME_EN, service.name_en)
                values.put(PaymentService.ICON_NAME, service.icon_name)
                values.put(PaymentService.ORDER, service.order)
                values.put(PaymentService.SERVICE_GROUP_CODE, service.service_group_code)
                values.put(PaymentService.SERVICE_ID, service.service_id)
                values.put(PaymentService.PAYMENT_DETAIL_CODE, service.payment_detail_code)
                values.put(PaymentService.PAYMENT_TYPE, service.payment_type)
                values.put(PaymentService.MIN_AMOUNT, service.min_amount)
                values.put(PaymentService.MAX_AMOUNT, service.max_amount)
                values.put(PaymentService.PAY_REQUEST_METHOD, service.pay_request_method)
                values.put(PaymentService.COLUMN_SMS_CONTROL_LIMIT, service.sms_control_limit)
                values.put(
                    PaymentService.COLUMN_IDENTIFICATION,
                    service.identification_payment_method
                )
                db.insert(PaymentService.TABLE_NAME, null, values)
            } catch (ex: Exception) {
                Logger.writeErrorLog(ex.localizedMessage!!)
            }
        }
        db.close()
    }

    fun insertPaymentParams(paymentParams: List<PaymentParams>) {
        val db = this.writableDatabase
        for (paymentParam in paymentParams) {
            try {
                val values = ContentValues()
                values.put(PaymentParams.COLUMN_ICON_NAME, paymentParam.icon_name)
                values.put(PaymentParams.COLUMN_INDEX_NAME_RU, paymentParam.name_ru)
                values.put(PaymentParams.COLUMN_INDEX_NAME_UC, paymentParam.name_uzc)
                values.put(PaymentParams.COLUMN_INDEX_NAME_UL, paymentParam.name_uzl)
                values.put(PaymentParams.COLUMN_INDEX_NAME_EN, paymentParam.name_en)
                values.put(PaymentParams.COLUMN_INDEX_HINT_RU, paymentParam.hint_ru)
                values.put(PaymentParams.COLUMN_INDEX_HINT_UC, paymentParam.hint_uzc)
                values.put(PaymentParams.COLUMN_INDEX_HINT_UL, paymentParam.hint_uzl)
                values.put(PaymentParams.COLUMN_INDEX_HINT_EN, paymentParam.hint_en)
                values.put(PaymentParams.COLUMN_IS_VISIBLE, paymentParam.is_visible)
                values.put(PaymentParams.COLUMN_PARAM_TYPE, paymentParam.param_type)
                values.put(PaymentParams.COLUMN_ORDER, paymentParam.ord)
                values.put(PaymentParams.COLUMN_PARAM_LENGTH, paymentParam.param_length)
                values.put(PaymentParams.COLUMN_IS_REQUIRED, paymentParam.is_required)
                values.put(PaymentParams.COLUMN_LEVEL_POSITION, paymentParam.level_position)
                values.put(PaymentParams.COLUMN_IS_READ_ONLY, paymentParam.is_read_only)
                values.put(
                    PaymentParams.COLUMN_PAYMENT_DETAIL_CODE,
                    paymentParam.payment_detail_code
                )
                values.put(PaymentParams.COLUMN_CODE, paymentParam.code)
                values.put(PaymentParams.COLUMN_MANDATORY, paymentParam.mondatory)
                values.put(PaymentParams.COLUMN_GROUP_ORD, paymentParam.group_ord)
                values.put(PaymentParams.COLUMN_DEF_VALUE, paymentParam.def_value)
                values.put(PaymentParams.COLUMN_REF_CODE, paymentParam.ref_code)
                values.put(PaymentParams.COLUMN_REGULAR_EXP_MASK, paymentParam.regular_exp_mask)
                values.put(PaymentParams.COLUMN_PREFIX, paymentParam.prefix)
                values.put(PaymentParams.COLUMN_SETTLEMENT, paymentParam.settlement)
                values.put(PaymentParams.COLUMN_MASK, paymentParam.field_mask)
                db.insert(PaymentParams.TABLE_NAME, null, values)
            } catch (ex: Exception) {
                Logger.writeErrorLog(ex.localizedMessage!!)
            }
        }
        db.close()
    }

    fun insertReferenceList(referenceList: List<PaymentReference>) {
        val db = this.writableDatabase
        for (reference in referenceList) {
            try {
                val values = ContentValues()
                values.put(PaymentReference.COLUMN_NAME_RU, reference.name_ru)
                values.put(PaymentReference.COLUMN_NAME_EN, reference.name_en)
                values.put(PaymentReference.COLUMN_NAME_UC, reference.name_uzc)
                values.put(PaymentReference.COLUMN_NAME_UL, reference.name_uzl)
                values.put(PaymentReference.COLUMN_ORDER, reference.order)
                values.put(PaymentReference.COLUMN_CODE, reference.code)
                values.put(PaymentReference.COLUMN_FLAG, reference.flag)
                values.put(PaymentReference.COLUMN_REF_CODE, reference.ref_code)
                db.insert(PaymentReference.TABLE_NAME, null, values)
            } catch (ex: Exception) {
                Logger.writeErrorLog(ex.localizedMessage!!)
            }
        }
        db.close()
    }

    @Throws(SQLException::class)
    fun getGroupList(): ArrayList<PaymentGroup> {
        val categoriesResult = ArrayList<PaymentGroup>()
        try {
            val query = "SELECT DISTINCT " + PaymentGroup.INDEX_NAME_RU +
                    ", " + PaymentGroup.INDEX_NAME_UC +
                    ", " + PaymentGroup.INDEX_NAME_UL +
                    ", " + PaymentGroup.INDEX_NAME_EN +
                    ", " + PaymentGroup.SERVICE_GROUP_CODE +
                    ", " + PaymentGroup.ICON_NAME +
                    ", " + PaymentGroup.ORDER +
                    ", " + PaymentGroup.COLUMN_PARENT_SERVICE +
                    " FROM " + PaymentGroup.TABLE_NAME
            val cursor = this.readableDatabase.rawQuery(query, null)
            val categories = ArrayList<PaymentGroup>()
            if (cursor != null && cursor.count > 0) {
                if (cursor.moveToFirst()) {
                    val serviceGroupCode = cursor.getColumnIndex(PaymentGroup.SERVICE_GROUP_CODE)
                    val iconName = cursor.getColumnIndex(PaymentGroup.ICON_NAME)
                    val order = cursor.getColumnIndex(PaymentGroup.ORDER)
                    val parentServiceGroupCode = cursor.getColumnIndex(PaymentGroup.COLUMN_PARENT_SERVICE)
                    val nameIndex = cursor.getNameIndex()
                    do {
                        val paymentGroup = PaymentGroup()
                        paymentGroup.PaymentGroup(
                            group_code = cursor.getString(serviceGroupCode),
                            name = cursor.getPreferredName(nameIndex),
                            icon_name = cursor.getString(iconName),
                            order = cursor.getInt(order),
                            parent_service_group_code = cursor.getString(parentServiceGroupCode)
                        )
                        categories.add(paymentGroup)
                    } while (cursor.moveToNext())
                    for (i in categories.indices) {
                        categories[i].service_list = getServiceList(categories[i].group_code.orEmpty())
                    }
                    for (i in categories.indices) {
                        categories[i].service_list?.let { serviceList ->
                            if (serviceList.size > 0) {
                                if (categories[i].group_code != "1") {
                                    categoriesResult.add(categories[i])
                                }
                            }
                        }
                    }
                }
            }
            cursor?.close()
        } catch (e: Exception) {
            Logger.writeErrorLog(e.localizedMessage.orEmpty())
        }
        return categoriesResult
    }

    @Throws(SQLException::class)
    fun getServiceList(serviceGroupCode: String): ArrayList<PaymentService> {
        val query = "SELECT * FROM " + PaymentService.TABLE_NAME + " WHERE ${PaymentService.SERVICE_GROUP_CODE} = ? "
        val cursor = this.readableDatabase.rawQuery(query, arrayOf(serviceGroupCode))
        val services = ArrayList<PaymentService>()
        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                val iconName = cursor.getColumnIndex(PaymentService.ICON_NAME)
                val serviceId = cursor.getColumnIndex(PaymentService.SERVICE_ID)
                val paymentDetailCode = cursor.getColumnIndex(PaymentService.PAYMENT_DETAIL_CODE)
                val paymentType = cursor.getColumnIndex(PaymentService.PAYMENT_TYPE)
                val order = cursor.getColumnIndex(PaymentService.ORDER)
                val minAmount = cursor.getColumnIndex(PaymentService.MIN_AMOUNT)
                val maxAmount = cursor.getColumnIndex(PaymentService.MAX_AMOUNT)
                val identificationPaymentMethod = cursor.getColumnIndex(PaymentService.COLUMN_IDENTIFICATION)
                val payRequestMethod = cursor.getColumnIndex(PaymentService.PAY_REQUEST_METHOD)
                val smsControlLimit = cursor.getColumnIndex(PaymentService.COLUMN_SMS_CONTROL_LIMIT)
                val nameIndex = cursor.getNameIndex()
                do {
                    try {
                        val service = PaymentService().PaymentService(
                            icon_name = cursor.getString(iconName),
                            service_id = cursor.getInt(serviceId),
                            payment_detail_code = cursor.getString(paymentDetailCode),
                            payment_type = cursor.getString(paymentType),
                            order = cursor.getInt(order),
                            name_index = cursor.getPreferredName(nameIndex),
                            min_amount = cursor.getString(minAmount),
                            max_amount = cursor.getString(maxAmount),
                            identification_payment_method = cursor.getString(identificationPaymentMethod),
                            pay_request_method = cursor.getString(payRequestMethod),
                            sms_control_limit = cursor.getString(smsControlLimit).orEmpty()
                        )
                        services.add(service)
                    } catch (e: java.lang.Exception) {
                        Logger.writeErrorLog(e.localizedMessage.orEmpty())
                    }
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return services
    }

    @Throws(SQLException::class)
    fun getPaymentDetails(paymentDetailCode: String): ArrayList<PaymentParams> {
        val query = "SELECT * FROM ${PaymentParams.TABLE_NAME} WHERE ${PaymentParams.COLUMN_PAYMENT_DETAIL_CODE} = ? ORDER BY ord"
        val cursor = this.readableDatabase.rawQuery(query, arrayOf(paymentDetailCode))
        val paymentParamsList = ArrayList<PaymentParams>()
        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                val detailCodeIndex = cursor.getColumnIndex(PaymentParams.COLUMN_PAYMENT_DETAIL_CODE)
                val isVisible = cursor.getColumnIndex(PaymentParams.COLUMN_IS_VISIBLE)
                val paramType = cursor.getColumnIndex(PaymentParams.COLUMN_PARAM_TYPE)
                val ord = cursor.getColumnIndex(PaymentParams.COLUMN_ORDER)
                val paramLength = cursor.getColumnIndex(PaymentParams.COLUMN_PARAM_LENGTH)
                val isRequired = cursor.getColumnIndex(PaymentParams.COLUMN_IS_REQUIRED)
                val isReadOnly = cursor.getColumnIndex(PaymentParams.COLUMN_IS_READ_ONLY)
                val code = cursor.getColumnIndex(PaymentParams.COLUMN_CODE)
                val mandatory = cursor.getColumnIndex(PaymentParams.COLUMN_MANDATORY)
                val groupOrd = cursor.getColumnIndex(PaymentParams.COLUMN_GROUP_ORD)
                val defValue = cursor.getColumnIndex(PaymentParams.COLUMN_DEF_VALUE)
                val iconName = cursor.getColumnIndex(PaymentParams.COLUMN_ICON_NAME)
                val levelPosition = cursor.getColumnIndex(PaymentParams.COLUMN_LEVEL_POSITION)
                val refCode = cursor.getColumnIndex(PaymentParams.COLUMN_REF_CODE)
                val regularExpMask = cursor.getColumnIndex(PaymentParams.COLUMN_REGULAR_EXP_MASK)
                val prefix = cursor.getColumnIndex(PaymentParams.COLUMN_PREFIX)
                val settlement = cursor.getColumnIndex(PaymentParams.COLUMN_SETTLEMENT)
                val mask = cursor.getColumnIndex(PaymentParams.COLUMN_MASK)
                val nameIndex = cursor.getNameIndex()
                val hintIndex = cursor.getNameIndex()
                do {
                    val paymentParams = PaymentParams().PaymentParams(
                        payment_detail_code = cursor.getString(detailCodeIndex),
                        is_visible = cursor.getString(isVisible),
                        param_type = cursor.getString(paramType),
                        ord = cursor.getString(ord),
                        param_length = cursor.getString(paramLength),
                        is_required = cursor.getString(isRequired),
                        is_read_only = cursor.getString(isReadOnly),
                        code = cursor.getString(code),
                        mandatory = cursor.getString(mandatory),
                        group_ord = cursor.getString(groupOrd),
                        def_value = cursor.getString(defValue),
                        icon_name = cursor.getString(iconName),
                        level_position = cursor.getString(levelPosition),
                        name = cursor.getPreferredName(nameIndex),
                        hint = cursor.getString(hintIndex),
                        ref_code = cursor.getString(refCode),
                        regular_exp_mask = cursor.getString(regularExpMask),
                        prefix = cursor.getString(prefix),
                        settlement = cursor.getString(settlement),
                        mask = cursor.getString(mask)
                    )
                    paymentParamsList.add(paymentParams)
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return paymentParamsList
    }

    @Throws(SQLException::class)
    fun getServiceByContractId(contractId: String): PaymentService? {
        val query = "SELECT * FROM " + PaymentService.TABLE_NAME + " WHERE service_id= ? "
        val cursor = this.readableDatabase.rawQuery(query, arrayOf(contractId))
        var service: PaymentService? = null
        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                val iconName = cursor.getColumnIndex(PaymentService.ICON_NAME)
                val serviceId = cursor.getColumnIndex(PaymentService.SERVICE_ID)
                val paymentDetailCode = cursor.getColumnIndex(PaymentService.PAYMENT_DETAIL_CODE)
                val paymentType = cursor.getColumnIndex(PaymentService.PAYMENT_TYPE)
                val ord = cursor.getColumnIndex(PaymentService.ORDER)
                val maxAmount = cursor.getColumnIndex(PaymentService.MAX_AMOUNT)
                val minAmount = cursor.getColumnIndex(PaymentService.MIN_AMOUNT)
                val identificationPaymentMethod = cursor.getColumnIndex(PaymentService.COLUMN_IDENTIFICATION)
                val payRequestMethod = cursor.getColumnIndex(PaymentService.PAY_REQUEST_METHOD)
                val smsControlLimit = cursor.getColumnIndex(PaymentService.COLUMN_SMS_CONTROL_LIMIT)
                val nameIndex = cursor.getNameIndex()
                do {
                    service = PaymentService().PaymentService(
                        icon_name = cursor.getString(iconName),
                        service_id = cursor.getInt(serviceId),
                        payment_detail_code = cursor.getString(paymentDetailCode),
                        payment_type = cursor.getString(paymentType),
                        order = cursor.getInt(ord),
                        name_index = cursor.getPreferredName(nameIndex),
                        min_amount = cursor.getString(minAmount),
                        max_amount = cursor.getString(maxAmount),
                        identification_payment_method = cursor.getString(identificationPaymentMethod),
                        pay_request_method = cursor.getString(payRequestMethod),
                        sms_control_limit = cursor.getString(smsControlLimit).orEmpty()
                    )
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return service
    }

    @Throws(SQLException::class)
    fun getRefParamList(refCode: String, likeCode: String?): ArrayList<PaymentReference> {
        val args: MutableList<String> = mutableListOf()
        val query: String
        if (likeCode == null) {
            query = "SELECT * FROM ${PaymentReference.TABLE_NAME} t WHERE t.ref_code = ?  ORDER BY t.ord "
            args.add(refCode)
        } else {
            query = "SELECT * FROM ${PaymentReference.TABLE_NAME} t WHERE t.ref_code = ? AND t.code LIKE ? ORDER BY t.ord "
            args.add(refCode)
            args.add("$likeCode%")
        }
        val cursor = this.readableDatabase.rawQuery(query, args.toTypedArray())
        val refParamList = ArrayList<PaymentReference>()
        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                val refCodeIndex = cursor.getColumnIndex(PaymentReference.COLUMN_REF_CODE)
                val code = cursor.getColumnIndex(PaymentReference.COLUMN_CODE)
                val order = cursor.getColumnIndex(PaymentReference.COLUMN_ORDER)
                val flag = cursor.getColumnIndex(PaymentReference.COLUMN_FLAG)
                val nameIndex = cursor.getNameIndex()
                do {
                    val refParam = PaymentReference().PaymentReference(
                        ref_code = cursor.getString(refCodeIndex),
                        code = cursor.getString(code),
                        order = cursor.getInt(order),
                        name = cursor.getPreferredName(nameIndex),
                        flag = cursor.getString(flag)
                    )
                    refParamList.add(refParam)
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return refParamList
    }

    private fun Cursor.getNameIndex(): Int {
        val selectedLanguage = LocaleHelper.getSelectedLang(context)
        return when (selectedLanguage) {
            0 -> getColumnIndex(LanguageUtils.INDEX_NAME_RU)
            1 -> getColumnIndex(LanguageUtils.INDEX_NAME_UC)
            2 -> getColumnIndex(LanguageUtils.INDEX_NAME_UL)
            3 -> getColumnIndex(LanguageUtils.INDEX_NAME_EN)
            else -> getColumnIndex(LanguageUtils.INDEX_NAME_RU)
        }
    }

    @SuppressLint("Range")
    private fun Cursor.getPreferredName(nameIndex: Int): String {
        return if (getString(nameIndex).isNullOrEmpty()) {
            getString(getColumnIndex(LanguageUtils.INDEX_NAME_RU))
        } else {
            getString(nameIndex)
        }
    }
}