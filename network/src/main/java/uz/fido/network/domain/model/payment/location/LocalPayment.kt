package uz.fido.network.domain.model.payment.location

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class LocalPayment(
    var unique_id: Int? = 0,
    var address: String = "",
    var icon_name: String = "",
    var id: String = "",
    var type_id: String = "",
    var sv_merchant_name: String = "",
    var phone: String = "",
    var state: String = "",
    var x_coordinate: String = "",
    var y_coordinate: String = "",
    var created_on: String = "",
    var modified_on: String = "",
    var distance: String? = "",
    @PrimaryKey(autoGenerate = true)
    var room_id:Long=0L
):Serializable
//    fun LocalPayment(
//        room_id:Long,
//        unique_id: Int,
//        address: String,
//        icon_name: String,
//        id: String,
//        type_id: String,
//        sv_merchant_name: String,
//        phone: String,
//        state: String,
//        x_coordinate: String,
//        y_coordinate: String,
//        created_on: String,
//        modified_on: String,
//        distance: String
//    ): LocalPayment {
//        @PrimaryKey(autoGenerate = true)
//        this.room_id=room_id
//        this.unique_id = unique_id
//        this.address = address
//        this.icon_name = icon_name
//        this.id = id
//        this.type_id = type_id
//        this.sv_merchant_name = sv_merchant_name
//        this.phone = phone
//        this.state = state
//        this.x_coordinate = x_coordinate
//        this.y_coordinate = y_coordinate
//        this.created_on = created_on
//        this.modified_on = modified_on
//        this.distance = distance
//        return this
//    }
