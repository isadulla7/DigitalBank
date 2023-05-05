package uz.fido.universaldigital.base

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel

abstract class AbstractViewModel(application: Application) : AndroidViewModel(application) {

    protected val context: Context get() = getApplication()

    open fun toast(string: String) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    open fun onProgressDialogCancelled() {
        //noop
    }

}