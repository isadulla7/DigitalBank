package uz.fido.universaldigital.ui.fragments.payment.download_payment.database

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteStatement
import uz.fido.network.domain.model.payment.LanguageUtils
import uz.fido.network.domain.model.payment.PaymentCashback
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentReference
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.universaldigital.ui.utils.lang.LocaleHelper
import uz.fido.utils.const.Const
import java.sql.SQLException

class DatabaseHelper(
    private val context: Context,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    constructor(context: Context) : this(
        context = context, name = "universal_digital_payment",
        factory = null, version = Const.DB_HELPER_VERSION
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
        db.beginTransaction()
        try {
            val statement = db.compileStatement(
                "INSERT INTO ${PaymentGroup.TABLE_NAME} (" +
                        "${PaymentGroup.SERVICE_GROUP_CODE}, " +
                        "${PaymentGroup.INDEX_NAME_RU}, " +
                        "${PaymentGroup.INDEX_NAME_UC}, " +
                        "${PaymentGroup.INDEX_NAME_UL}, " +
                        "${PaymentGroup.INDEX_NAME_EN}, " +
                        "${PaymentGroup.ICON_NAME}, " +
                        "${PaymentGroup.ORDER}, " +
                        "${PaymentGroup.COLUMN_PARENT_SERVICE}) " +
                        "VALUES (?,?,?,?,?,?,?,?)"
            )

            for (paymentGroup in groupList.sortedBy { it.order }) {
                statement.clearBindings()
                statement.bindStringOrNull(1, paymentGroup.service_group_code)
                statement.bindStringOrNull(2, paymentGroup.name_ru)
                statement.bindStringOrNull(3, paymentGroup.name_uzc)
                statement.bindStringOrNull(4, paymentGroup.name_uzl)
                statement.bindStringOrNull(5, paymentGroup.name_en)
                statement.bindStringOrNull(6, paymentGroup.icon_name)
                statement.bindLong(7, paymentGroup.order?.toLong() ?: 0)
                statement.bindStringOrNull(8, paymentGroup.parent_service_group_code)

                statement.executeInsert()
            }

            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun insertCashbackList(list: List<PaymentCashback>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            val statement = db.compileStatement(
                "INSERT INTO ${PaymentCashback.TABLE_NAME} (" +
                        "${PaymentCashback.COLUMN_GL_PERCENT}, " +
                        "${PaymentCashback.COLUMN_SERVICE_ID}, " +
                        "${PaymentCashback.COLUMN_STATE}, " +
                        "${PaymentCashback.COLUMN_SV_PERCENT}, " +
                        "${PaymentCashback.COLUMN_TET_PERCENT}, " +
                        "${PaymentCashback.COLUMN_KL_PERCENT}) " +
                        "VALUES (?,?,?,?,?,?)"
            )

            for (paymentCashback in list) {
                statement.clearBindings()
                statement.bindStringOrNull(1, paymentCashback.gl_percent)
                statement.bindStringOrNull(2, paymentCashback.service_id)
                statement.bindStringOrNull(3, paymentCashback.state)
                statement.bindStringOrNull(4, paymentCashback.sv_percent)
                statement.bindStringOrNull(5, paymentCashback.tet_percent)
                statement.bindStringOrNull(6, paymentCashback.kl_percent)
                statement.executeInsert()
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun insertServiceList(serviceList: List<PaymentService>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val statement = db.compileStatement(
                "INSERT INTO ${PaymentService.TABLE_NAME} (" +
                        "${PaymentService.SERVICE_GROUP_CODE}, " +
                        "${PaymentService.INDEX_NAME_RU}, " +
                        "${PaymentService.INDEX_NAME_UC}, " +
                        "${PaymentService.INDEX_NAME_UL}, " +
                        "${PaymentService.INDEX_NAME_EN}, " +
                        "${PaymentService.ICON_NAME}, " +
                        "${PaymentService.ORDER}, " +
                        "${PaymentService.SERVICE_ID}, " +
                        "${PaymentService.PAYMENT_DETAIL_CODE}, " +
                        "${PaymentService.PAYMENT_TYPE}, " +
                        "${PaymentService.MIN_AMOUNT}, " +
                        "${PaymentService.MAX_AMOUNT}, " +
                        "${PaymentService.PAY_REQUEST_METHOD}, " +
                        "${PaymentService.COLUMN_SMS_CONTROL_LIMIT}, " +
                        "${PaymentService.COLUMN_IDENTIFICATION}) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
            )

            for (service in serviceList) {
                statement.clearBindings()
                statement.bindStringOrNull(1, service.service_group_code)
                statement.bindStringOrNull(2, service.name_ru)
                statement.bindStringOrNull(3, service.name_uzc)
                statement.bindStringOrNull(4, service.name_uzl)
                statement.bindStringOrNull(5, service.name_en)
                statement.bindStringOrNull(6, service.icon_name)
                statement.bindLong(7, service.order?.toLong() ?: 0)
                statement.bindLong(8, service.service_id?.toLong() ?: 0)
                statement.bindStringOrNull(9, service.payment_detail_code)
                statement.bindStringOrNull(10, service.payment_type)
                statement.bindStringOrNull(11, service.min_amount)
                statement.bindStringOrNull(12, service.max_amount)
                statement.bindStringOrNull(13, service.pay_request_method)
                statement.bindStringOrNull(14, service.sms_control_limit)
                statement.bindStringOrNull(15, service.identification_payment_method)
                statement.executeInsert()
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun insertPaymentParams(paymentParams: List<PaymentParams>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            val statement = db.compileStatement(
                "INSERT INTO ${PaymentParams.TABLE_NAME} (" +
                        "${PaymentParams.COLUMN_ICON_NAME}, " +
                        "${PaymentParams.COLUMN_INDEX_NAME_RU}, " +
                        "${PaymentParams.COLUMN_INDEX_NAME_UC}, " +
                        "${PaymentParams.COLUMN_INDEX_NAME_UL}, " +
                        "${PaymentParams.COLUMN_INDEX_NAME_EN}, " +
                        "${PaymentParams.COLUMN_INDEX_HINT_RU}, " +
                        "${PaymentParams.COLUMN_INDEX_HINT_UC}, " +
                        "${PaymentParams.COLUMN_INDEX_HINT_UL}, " +
                        "${PaymentParams.COLUMN_INDEX_HINT_EN}, " +
                        "${PaymentParams.COLUMN_IS_VISIBLE}, " +
                        "${PaymentParams.COLUMN_PARAM_TYPE}, " +
                        "${PaymentParams.COLUMN_ORDER}, " +
                        "${PaymentParams.COLUMN_PARAM_LENGTH}, " +
                        "${PaymentParams.COLUMN_IS_REQUIRED}, " +
                        "${PaymentParams.COLUMN_LEVEL_POSITION}, " +
                        "${PaymentParams.COLUMN_IS_READ_ONLY}, " +
                        "${PaymentParams.COLUMN_PAYMENT_DETAIL_CODE}, " +
                        "${PaymentParams.COLUMN_CODE}, " +
                        "${PaymentParams.COLUMN_MANDATORY}, " +
                        "${PaymentParams.COLUMN_GROUP_ORD}, " +
                        "${PaymentParams.COLUMN_DEF_VALUE}, " +
                        "${PaymentParams.COLUMN_REF_CODE}, " +
                        "${PaymentParams.COLUMN_REGULAR_EXP_MASK}, " +
                        "${PaymentParams.COLUMN_PREFIX}, " +
                        "${PaymentParams.COLUMN_SETTLEMENT}, " +
                        "${PaymentParams.COLUMN_MASK}) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
            )
            for (paymentParam in paymentParams) {
                statement.clearBindings()
                statement.bindString(1, paymentParam.icon_name)
                statement.bindString(2, paymentParam.name_ru)
                statement.bindString(3, paymentParam.name_uzc)
                statement.bindString(4, paymentParam.name_uzl)
                statement.bindString(5, paymentParam.name_en)
                statement.bindString(6, paymentParam.hint_ru)
                statement.bindString(7, paymentParam.hint_uzc)
                statement.bindString(8, paymentParam.hint_uzl)
                statement.bindString(9, paymentParam.hint_en)
                statement.bindString(10, paymentParam.is_visible)
                statement.bindString(11, paymentParam.param_type)
                statement.bindString(12, paymentParam.ord)
                statement.bindString(13, paymentParam.param_length)
                statement.bindString(14, paymentParam.is_required)
                statement.bindString(15, paymentParam.level_position)
                statement.bindString(16, paymentParam.is_read_only)
                statement.bindString(17, paymentParam.payment_detail_code)
                statement.bindString(18, paymentParam.code)
                statement.bindString(19, paymentParam.mondatory)
                statement.bindString(20, paymentParam.group_ord)
                statement.bindString(21, paymentParam.def_value)
                statement.bindString(22, paymentParam.ref_code)
                statement.bindString(23, paymentParam.regular_exp_mask)
                statement.bindString(24, paymentParam.prefix)
                statement.bindString(25, paymentParam.settlement)
                statement.bindString(26, paymentParam.field_mask)
                statement.executeInsert()
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun insertReferenceList(referenceList: List<PaymentReference>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            val statement = db.compileStatement(
                "INSERT INTO ${PaymentReference.TABLE_NAME} (" +
                        "${PaymentReference.COLUMN_NAME_RU}, " +
                        "${PaymentReference.COLUMN_NAME_EN}, " +
                        "${PaymentReference.COLUMN_NAME_UC}, " +
                        "${PaymentReference.COLUMN_NAME_UL}, " +
                        "${PaymentReference.COLUMN_ORDER}, " +
                        "${PaymentReference.COLUMN_CODE}, " +
                        "${PaymentReference.COLUMN_FLAG}, " +
                        "${PaymentReference.COLUMN_REF_CODE}) " +
                        "VALUES (?,?,?,?,?,?,?,?)"
            )
            for (reference in referenceList) {
                statement.clearBindings()
                statement.bindStringOrNull(1, reference.name_ru)
                statement.bindStringOrNull(2, reference.name_en)
                statement.bindStringOrNull(3, reference.name_uzc)
                statement.bindStringOrNull(4, reference.name_uzl)
                statement.bindLong(5, reference.order?.toLong() ?: 0)
                statement.bindStringOrNull(6, reference.code)
                statement.bindStringOrNull(7, reference.flag)
                statement.bindStringOrNull(8, reference.ref_code)
                statement.executeInsert()
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
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
            e.printStackTrace()
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
                        e.printStackTrace()
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

    private fun SQLiteStatement.bindStringOrNull(index: Int, value: String?) {
        if (value.isNullOrEmpty()) bindString(index, "") else bindString(index, value)
    }

}