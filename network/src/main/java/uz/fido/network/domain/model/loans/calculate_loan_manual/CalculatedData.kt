package uz.fido.network.domain.model.loans.calculate_loan_manual

import java.io.Serializable

data class CalculatedData(
    val month_number: String,
    val oper_date: String,
    val saldo_dept: String,
    val sum_all: String,
    val sum_dept: String,
    val sum_percent: String

) : Serializable