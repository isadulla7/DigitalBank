package uz.fido.universaldigital.ui.fragments.products.widgets.notifications

import android.app.Application
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import uz.fido.network.domain.datasource.interfaces.IUtilsRepository
import uz.fido.network.domain.model.news.GetNewsRequest
import uz.fido.network.domain.model.news.GetNotificationsRequest
import uz.fido.network.domain.model.news.Notification
import uz.fido.network.domain.model.news.UpdateNotificationState
import uz.fido.universaldigital.base.AbstractViewModel
import uz.fido.utils.utility.activity.LiveEvent
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    application: Application,
    private val utilsRepository: IUtilsRepository
) : AbstractViewModel(application) {

    var notifications = LiveEvent<ArrayList<Notification>>()

    fun getNotifications(token: String, request: GetNotificationsRequest) = liveData(Dispatchers.IO) {
        emit(utilsRepository.getNotifications(token, request))
    }

    fun updateNotificationStatus(token: String, updateNewsStatusRequest: UpdateNotificationState) = liveData(Dispatchers.IO) {
        emit(utilsRepository.updateNotificationStatus(token, updateNewsStatusRequest))
    }

    fun getNewsRequest(token: String, getNewsRequest: GetNewsRequest) = liveData(Dispatchers.IO) {
        emit(utilsRepository.getNewsList(token, getNewsRequest))
    }
}