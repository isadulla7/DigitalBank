package uz.fido.network.domain.model.deposits.constructor

import java.io.Serializable

data class DepositConstructor(
    val percent: String,
    val dep_id: String,
    val amount: String,
    val dep_type: String,
    val date: String,
    val pay_to_card: String,
    val pay_to_object_value: String,
    val replenishment: String,
    val interest_payment: String,
    val capitalization: String,
//    val early_closure_of_deposit: String,
    val partial_withdrawal: String,
//    val prolongation: String,
    val dcParam103: String,
    val dcParam104: String,
    val dep_name:String
) : Serializable