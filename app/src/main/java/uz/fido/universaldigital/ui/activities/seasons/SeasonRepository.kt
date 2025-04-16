package uz.fido.universaldigital.ui.activities.seasons

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Singleton

@Singleton
class SeasonRepository {

    private val database = FirebaseDatabase.getInstance().getReference("season")

    fun getSeason(callback: (String) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(Season.DEFAULT)
            }
        })
    }
}