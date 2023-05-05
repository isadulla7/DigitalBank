package uz.fido.network.domain.model.loans.loan_params

import uz.fido.network.domain.model.payment.AllServiceLists
import java.io.Serializable

data class ReferencesLoanParams(
    val REF_009: ArrayList<AllServiceLists>,
    val REF_004: ArrayList<AllServiceLists>,
    val REF_027: ArrayList<AllServiceLists>,
    val REF_050: ArrayList<AllServiceLists>,
    val LN_DAYS_IN_YEAR: ArrayList<AllServiceLists>,
    val GRAPH_TYPE: ArrayList<AllServiceLists>,
    val REF_031: ArrayList<AllServiceLists>,
    val REF_034: ArrayList<AllServiceLists>,
    val REF_041: ArrayList<AllServiceLists>,
    val REF_074: ArrayList<AllServiceLists>,
    val REF_003: ArrayList<AllServiceLists>,
    val CRM_REF_008: ArrayList<AllServiceLists>,
    val REF_038: ArrayList<AllServiceLists>,
    val REF_036: ArrayList<AllServiceLists>,
    val REF_002: ArrayList<AllServiceLists>,
    val LN_PERS_RATE_DESC: ArrayList<AllServiceLists>,
    val REF_017: ArrayList<AllServiceLists>,
    val LN_DEPARTMENT: ArrayList<AllServiceLists>,
    val LN_CLIENT_TYPE: ArrayList<AllServiceLists>,
    val REF_004_1: ArrayList<AllServiceLists>,
    val REF_051: ArrayList<AllServiceLists>,
) : Serializable