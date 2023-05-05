package uz.fido.network.domain.model.loans.calculate_loan_manual

import java.io.Serializable

data class CalcLoanManualRequest(
    val token: String,
    val command: String,
    val sum_all: String,
    val perc_rate: String,
    val initial_percent: String,
    val term_month: String,
    val grace_period: String,
    val start_date: String
) : Serializable