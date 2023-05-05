package uz.fido.network.domain.model.mib

import java.io.Serializable
import kotlin.collections.ArrayList

data class MibDetail(
    val org_id: String,
    val id: String,
    val features: ArrayList<MibDetails>? = ArrayList(),
    val org_type: String? = null,
    val debet_summa: String? = null
): Serializable