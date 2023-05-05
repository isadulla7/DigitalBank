package uz.fido.network.domain.model.payment

import java.io.Serializable

class PaymentCashback : Serializable {
    var gl_percent: String? = ""
    var service_id: String? = ""
    var state: String? = ""
    var sv_percent: String? = ""
    var tet_percent: String? = ""
    var kl_percent: String? = ""

    companion object {
        const val TABLE_NAME = "cashback_list"

        const val COLUMN_GL_PERCENT = "gl_percent"
        const val COLUMN_SERVICE_ID = "service_id"
        const val COLUMN_STATE = "state"
        const val COLUMN_SV_PERCENT = "sv_percent"
        const val COLUMN_TET_PERCENT = "tet_percent"
        const val COLUMN_KL_PERCENT = "kl_percent"
    }

    fun PaymentCashback(
        gl_percent: String,
        service_id: String,
        state: String,
        sv_percent: String,
        tet_percent: String,
        kl_percent: String
    ): PaymentCashback {
        this.gl_percent = gl_percent
        this.service_id = service_id
        this.state = state
        this.sv_percent = sv_percent
        this.tet_percent = tet_percent
        this.kl_percent = kl_percent
        return this
    }
}