package uz.fido.network.domain.model.payment.location

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity
class LocalPaymentRoom(
    @PrimaryKey(autoGenerate = true)
    var room_id:Long=0,
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
    var distance: String? = ""
):Serializable
