package uz.fido.universaldigital.base

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus

abstract class AbstractViewModel(application: Application) : AndroidViewModel(application) {

    protected val context: Context get() = getApplication()

    val vmScope = viewModelScope + Dispatchers.IO

    open fun toast(string: String) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

}