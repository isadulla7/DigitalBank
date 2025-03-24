package uz.fido.universaldigital.ui.activities.app_icon_changer

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uz.fido.utils.const.Const
import javax.inject.Singleton

@Singleton
class AppIconRepository {

    private val database = FirebaseDatabase.getInstance().getReference(Const.FIREBASE_APP_ICON_NAME)

    fun getAppIcon(callback: (String) -> Unit) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(AppIcons.APP_ICON_DEFAULT)
            }
        })
    }
}