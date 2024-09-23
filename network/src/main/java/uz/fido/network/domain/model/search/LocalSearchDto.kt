package uz.fido.network.domain.model.search

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.network.domain.model.payment.PaymentService

@Entity
class LocalSearchDto(
    @PrimaryKey(autoGenerate = true)
    val id:Long=0L,
    val serviceGroupCode: String = "",
    val groupCode: String = "",
    val name: String = "",
    val imageName: String = "",
    val groupName: String = "",
    val room_id:String="",
)