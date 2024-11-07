package uz.fido.network.domain.model.search

import androidx.room.Entity
import androidx.room.PrimaryKey

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