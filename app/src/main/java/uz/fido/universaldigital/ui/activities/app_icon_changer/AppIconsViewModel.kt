package uz.fido.universaldigital.ui.activities.app_icon_changer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppIconsViewModel @Inject constructor(
    private val repository: AppIconRepository
) : ViewModel() {

    init {
        fetchAppIcon()
    }

    val appIconLiveData = MutableLiveData<String>()

    fun fetchAppIcon() {
        repository.getAppIcon { icon ->
            appIconLiveData.postValue(icon)
        }
    }

}
