package uz.fido.universaldigital.ui.activities.seasons

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(
    private val repository: SeasonRepository
) : ViewModel() {

    init {
        fetchSeason()
    }

    val seasonLiveData = MutableLiveData<String>()

    fun fetchSeason() {
        repository.getSeason { season ->
            seasonLiveData.postValue(season)
        }
    }

}
