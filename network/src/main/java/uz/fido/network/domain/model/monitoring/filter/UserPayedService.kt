package uz.fido.network.domain.model.monitoring.filter


import com.google.gson.annotations.SerializedName
import uz.fido.network.domain.model.payment.local_history.LocalMonitoring
import java.io.Serializable

data class UserPayedService(
    val service_id: Int?=null,
    val icon_name:String="",
    val service_name: String?=null,
    var service_current:Boolean=false,
    var list:ArrayList<LocalMonitoring> = arrayListOf()
): Serializable