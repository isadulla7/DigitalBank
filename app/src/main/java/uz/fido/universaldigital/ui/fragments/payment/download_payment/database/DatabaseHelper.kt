package uz.fido.universaldigital.ui.fragments.payment.download_payment.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import uz.fido.network.domain.model.payment.PaymentCashback
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.network.domain.model.payment.PaymentParams
import uz.fido.network.domain.model.payment.PaymentReference
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.utils.const.Const
import uz.fido.utils.log.Logger
import uz.fido.utils.utility.language.LocaleHelper
import java.sql.SQLException
import kotlin.collections.ArrayList

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
        createTables()
    }

    private fun createTables() {
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

    fun recreateTables() {
        dropTables()
        createTables()
    }

    fun clearAll() {
        clearGroupListTable()
        clearServiceListTable()
        clearPaymentDetailTable()
        clearRefParamTable()
        clearCashbackTable()
    }

    private fun dropTables() {
        try {
            this.writableDatabase?.execSQL("DROP TABLE IF EXISTS ${PaymentGroup.TABLE_NAME}")
            this.writableDatabase?.execSQL("DROP TABLE IF EXISTS ${PaymentService.TABLE_NAME}")
            this.writableDatabase?.execSQL("DROP TABLE IF EXISTS ${PaymentReference.TABLE_NAME}")
            this.writableDatabase?.execSQL("DROP TABLE IF EXISTS ${PaymentParams.TABLE_NAME}")
            this.writableDatabase?.execSQL("DROP TABLE IF EXISTS ${PaymentCashback.TABLE_NAME}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearGroupListTable() {
        this.writableDatabase!!.execSQL("DELETE from " + PaymentGroup.TABLE_NAME)
    }

    private fun clearServiceListTable() {
        this.writableDatabase!!.execSQL("DELETE from " + PaymentService.TABLE_NAME)
    }

    private fun clearPaymentDetailTable() {
        this.writableDatabase.execSQL("DELETE from " + PaymentParams.TABLE_NAME)
    }

    private fun clearRefParamTable() {
        this.writableDatabase!!.execSQL("DELETE from " + PaymentReference.TABLE_NAME)
    }

    private fun clearCashbackTable() {
        this.writableDatabase!!.execSQL("DELETE from " + PaymentCashback.TABLE_NAME)
    }

    fun clearSearchTable() {
        this.writableDatabase!!.execSQL("DELETE from search_list")
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
                Logger.writeErrorLog(ex.localizedMessage!!)
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
        val db = this.writableDatabase
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
            val query = "select distinct " + PaymentGroup.INDEX_NAME_RU +
                    ", " + PaymentGroup.INDEX_NAME_UC +
                    ", " + PaymentGroup.INDEX_NAME_UL +
                    ", " + PaymentGroup.INDEX_NAME_EN +
                    ", " + PaymentGroup.SERVICE_GROUP_CODE +
                    ", " + PaymentGroup.ICON_NAME +
                    ", " + PaymentGroup.ORDER +
                    ", " + PaymentGroup.COLUMN_PARENT_SERVICE +
                    " from " + PaymentGroup.TABLE_NAME
            val cursor = this.readableDatabase.rawQuery(query, null)
            val categories = ArrayList<PaymentGroup>()
            val nls = LocaleHelper.getSelectedLang(context)
            if (cursor != null && cursor.count > 0) {
                if (cursor.moveToFirst()) {
                    val serviceGroupCode = cursor.getColumnIndex(PaymentGroup.SERVICE_GROUP_CODE)
                    val iconName = cursor.getColumnIndex(PaymentGroup.ICON_NAME)
                    val order = cursor.getColumnIndex(PaymentGroup.ORDER)
                    val parentServiceGroupCode =
                        cursor.getColumnIndex(PaymentGroup.COLUMN_PARENT_SERVICE)
                    val nameIndex: Int = when (nls) {
                        0 -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_RU)
                        1 -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_UC)
                        2 -> cursor.getColumnIndex(
                            PaymentGroup.INDEX_NAME_UL
                        )

                        else -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_EN)
                    }
                    do {
                        val cat = PaymentGroup()
                        cat.PaymentGroup(
                            group_code = cursor.getString(serviceGroupCode),
                            name = cursor.getString(nameIndex),
                            icon_name = cursor.getString(iconName),
                            order = cursor.getInt(order),
                            parent_service_group_code = cursor.getString(parentServiceGroupCode)
                        )
                        categories.add(cat)
                    } while (cursor.moveToNext())
                    for (i in categories.indices) {
                        categories[i].service_list = getServiceList(categories[i].group_code!!)
                    }
                    for (i in categories.indices) {
                        if (categories[i].service_list!!.size > 0) {
                            if (categories[i].group_code != "1")
                                categoriesResult.add(categories[i])
                        }
                    }
                }
            }
            cursor?.close()
        } catch (e: Exception) {
            Logger.writeErrorLog(e.localizedMessage!!)
        }
        return categoriesResult
    }

    fun getServiceCashback(serviceId: String): PaymentCashback? {
        val query =
            "select * from " + PaymentCashback.TABLE_NAME + " where service_id= " + serviceId + " "
        val cursor = this.readableDatabase.rawQuery(query, null)
        var paymentCashback: PaymentCashback? = null
        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                val serviceId = cursor.getColumnIndex(PaymentCashback.COLUMN_SERVICE_ID)
                val glPercent = cursor.getColumnIndex(PaymentCashback.COLUMN_GL_PERCENT)
                val svPercent = cursor.getColumnIndex(PaymentCashback.COLUMN_SV_PERCENT)
                val tetPercent = cursor.getColumnIndex(PaymentCashback.COLUMN_TET_PERCENT)
                val klPercent = cursor.getColumnIndex(PaymentCashback.COLUMN_KL_PERCENT)
                val state = cursor.getColumnIndex(PaymentCashback.COLUMN_STATE)

                do {
                    paymentCashback = PaymentCashback().PaymentCashback(
                        gl_percent = cursor.getString(glPercent),
                        service_id = cursor.getString(serviceId),
                        state = cursor.getString(state),
                        sv_percent = cursor.getString(svPercent),
                        tet_percent = cursor.getString(tetPercent),
                        kl_percent = cursor.getString(klPercent)
                    )
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return paymentCashback
    }

    @Throws(SQLException::class)
    fun getServiceList(serviceGroupCode: String): ArrayList<PaymentService> {
        val query =
            "select * from " + PaymentService.TABLE_NAME.toString() + " where ${PaymentService.SERVICE_GROUP_CODE} = '" + serviceGroupCode + "' "
        val cursor = this.readableDatabase.rawQuery(query, null)
        val services = ArrayList<PaymentService>()
        val nls = LocaleHelper.getSelectedLang(context)
        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                val iconName = cursor.getColumnIndex(PaymentService.ICON_NAME)
                val serviceId = cursor.getColumnIndex(PaymentService.SERVICE_ID)
                val paymentDetailCode = cursor.getColumnIndex(PaymentService.PAYMENT_DETAIL_CODE)
                val paymentType = cursor.getColumnIndex(PaymentService.PAYMENT_TYPE)
                val ord = cursor.getColumnIndex(PaymentService.ORDER)
                val minAmount = cursor.getColumnIndex(PaymentService.MIN_AMOUNT)
                val maxAmount = cursor.getColumnIndex(PaymentService.MAX_AMOUNT)
                val identificationPaymentMethod =
                    cursor.getColumnIndex(PaymentService.COLUMN_IDENTIFICATION)
                val payRequestMethod = cursor.getColumnIndex(PaymentService.PAY_REQUEST_METHOD)
                val smsControlLimit = cursor.getColumnIndex(PaymentService.COLUMN_SMS_CONTROL_LIMIT)
                val nameIndex: Int = when (nls) {
                    0 -> cursor.getColumnIndex(PaymentService.INDEX_NAME_RU)
                    1 -> cursor.getColumnIndex(PaymentService.INDEX_NAME_UC)
                    2 -> cursor.getColumnIndex(PaymentService.INDEX_NAME_UL)
                    else -> cursor.getColumnIndex(PaymentService.INDEX_NAME_EN)
                }
                do {
                    try {
                        val service = PaymentService().PaymentService(
                            icon_name = cursor.getString(iconName),
                            service_id = cursor.getInt(serviceId),
                            payment_detail_code = cursor.getString(paymentDetailCode),
                            payment_type = cursor.getString(paymentType),
                            order = cursor.getInt(ord),
                            name_index = cursor.getString(nameIndex),
                            min_amount = cursor.getString(minAmount),
                            max_amount = cursor.getString(maxAmount),
                            identification_payment_method = cursor.getString(
                                identificationPaymentMethod
                            ),
                            pay_request_method = cursor.getString(payRequestMethod),
                            sms_control_limit = try {
                                cursor.getString(smsControlLimit) ?: ""
                            } catch (e: Exception) {
                                ""
                            }
                        )
                        services.add(service)
                    } catch (e: java.lang.Exception) {
                        Logger.writeErrorLog(e.localizedMessage!!)
                    }
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return services
    }

    @Throws(SQLException::class)
    fun getPaymentDetails(paymentDetailCode: String): ArrayList<PaymentParams> {
        val query =
            "select * from ${PaymentParams.TABLE_NAME} where ${PaymentParams.COLUMN_PAYMENT_DETAIL_CODE} = '$paymentDetailCode' order by ord "
        val cursor = this.readableDatabase.rawQuery(query, null)
        val paymentParamsList = ArrayList<PaymentParams>()
        val nls = LocaleHelper.getSelectedLang(context)
        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                val paymentDetailCode =
                    cursor.getColumnIndex(PaymentParams.COLUMN_PAYMENT_DETAIL_CODE)
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
                var nameIndex: Int = when (nls) {
                    0 -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_RU)
                    1 -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_UC)
                    2 -> cursor.getColumnIndex(
                        PaymentGroup.INDEX_NAME_UL
                    )

                    else -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_EN)
                }
                if (cursor.getString(nameIndex).isEmpty()) nameIndex =
                    cursor.getColumnIndex(PaymentGroup.INDEX_NAME_RU)
                val hintIndex: Int = when (nls) {
                    0 -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_RU)
                    1 -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_UC)
                    2 -> cursor.getColumnIndex(
                        PaymentGroup.INDEX_NAME_UL
                    )

                    else -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_EN)
                }
                do {
                    val paymentParams = PaymentParams().PaymentParams(
                        payment_detail_code = cursor.getString(paymentDetailCode),
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
                        name = cursor.getString(nameIndex),
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
        val query =
            "select * from " + PaymentService.TABLE_NAME + " where service_id= " + contractId + " "
        val cursor = this.readableDatabase.rawQuery(query, null)
        var service: PaymentService? = null
        val nls = LocaleHelper.getSelectedLang(context)
        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                val iconName = cursor.getColumnIndex(PaymentService.ICON_NAME)
                val serviceId = cursor.getColumnIndex(PaymentService.SERVICE_ID)
                val paymentDetailCode = cursor.getColumnIndex(PaymentService.PAYMENT_DETAIL_CODE)
                val paymentType = cursor.getColumnIndex(PaymentService.PAYMENT_TYPE)
                val ord = cursor.getColumnIndex(PaymentService.ORDER)
                val maxAmount = cursor.getColumnIndex(PaymentService.MAX_AMOUNT)
                val minAmount = cursor.getColumnIndex(PaymentService.MIN_AMOUNT)
                val identificationPaymentMethod =
                    cursor.getColumnIndex(PaymentService.COLUMN_IDENTIFICATION)
                val payRequestMethod = cursor.getColumnIndex(PaymentService.PAY_REQUEST_METHOD)
                val smsControlLimit = cursor.getColumnIndex(PaymentService.COLUMN_SMS_CONTROL_LIMIT)
                var nameIndex: Int
                nameIndex =
                    when (nls) {
                        0 -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_RU)
                        1 -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_UC)
                        2 -> cursor.getColumnIndex(
                            PaymentGroup.INDEX_NAME_UL
                        )

                        else -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_EN)
                    }
                do {
                    service = PaymentService().PaymentService(
                        icon_name = cursor.getString(iconName),
                        service_id = cursor.getInt(serviceId),
                        payment_detail_code = cursor.getString(paymentDetailCode),
                        payment_type = cursor.getString(paymentType),
                        order = cursor.getInt(ord),
                        name_index = cursor.getString(nameIndex),
                        min_amount = cursor.getString(minAmount),
                        max_amount = cursor.getString(maxAmount),
                        identification_payment_method = cursor.getString(identificationPaymentMethod),
                        pay_request_method = cursor.getString(payRequestMethod),
                        sms_control_limit = try {
                            cursor.getString(smsControlLimit) ?: ""
                        } catch (e: Exception) {
                            ""
                        }
                    )
                    if (service.nameIndex!!.isEmpty()) {
                        nameIndex = cursor.getColumnIndex("name_ru")
                        service.nameIndex = cursor.getString(nameIndex)
                    }
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return service
    }

    @Throws(SQLException::class)
    fun getRefParamList(refCode: String, likeCode: String?): ArrayList<PaymentReference> {
        val query: String = if (likeCode == null) {
            "select * from ${PaymentReference.TABLE_NAME} t where t.ref_code = '$refCode'  order by t.ord "
        } else {
            "select * from ${PaymentReference.TABLE_NAME} t where t.ref_code = '$refCode' and t.code like '$likeCode%' order by t.ord "
        }
        val cursor = this.readableDatabase.rawQuery(query, null)
        val refParamList = ArrayList<PaymentReference>()
        val nls = LocaleHelper.getSelectedLang(context)
        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                val refCode = cursor.getColumnIndex(PaymentReference.COLUMN_REF_CODE)
                val code = cursor.getColumnIndex(PaymentReference.COLUMN_CODE)
                val order = cursor.getColumnIndex(PaymentReference.COLUMN_ORDER)
                val flag = cursor.getColumnIndex(PaymentReference.COLUMN_FLAG)
                val nameIndex: Int = when (nls) {
                    0 -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_RU)
                    1 -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_UC)
                    2 -> cursor.getColumnIndex(
                        PaymentGroup.INDEX_NAME_UL
                    )

                    else -> cursor.getColumnIndex(PaymentGroup.INDEX_NAME_EN)
                }
                do {
                    val refParam = PaymentReference().PaymentReference(
                        ref_code = cursor.getString(refCode),
                        code = cursor.getString(code),
                        order = cursor.getInt(order),
                        name = cursor.getString(nameIndex),
                        flag = cursor.getString(flag)
                    )
                    refParamList.add(refParam)
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return refParamList
    }
}