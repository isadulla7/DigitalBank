package uz.fido.universaldigital.ui.fragments.payment.init_payment.second_step

import uz.fido.network.domain.model.payment.PaymentParams

fun PaymentParams.isReadOnly() = is_read_only == "Y"
fun PaymentParams.isRequired() = is_required == "Y"
fun PaymentParams.isAmountBlock() = code == "AMOUNT"
fun PaymentParams.isNavigationBlock() = param_type == "S"
fun PaymentParams.isBalanceBlock() = regular_exp_mask == "curr_balance"
fun PaymentParams.hasValidDefaultValue() = def_value != "null"
fun PaymentParams.getFormattedBalance() = def_value.replace(",", " ") + " UZS"