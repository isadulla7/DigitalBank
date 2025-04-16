package uz.fido.network.domain.model.payment.location

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class LocalPaymentType(
    val code: String = "",
    val icon_name: String ="",
    val id: String = "",
    val name: String ="",
    val order_number: String = "",
    val state: String ="",
    @PrimaryKey(autoGenerate = true)
    val room_id:Long=0L

): Serializable