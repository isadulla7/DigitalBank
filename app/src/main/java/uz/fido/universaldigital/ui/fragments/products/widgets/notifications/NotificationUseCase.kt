package uz.fido.universaldigital.ui.fragments.products.widgets.notifications

import uz.fido.network.data.repository.UtilsRepositoryImpl
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.news.GetNotificationsRequest
import uz.fido.network.domain.model.news.NotificationHistoryResponse
import uz.fido.network.domain.model.news.UpdateNotificationState
import uz.fido.utils.utility.user.getClientToken
import javax.inject.Inject

interface NotificationUseCase {
    suspend fun getNotifications(request: GetNotificationsRequest): NotificationHistoryResponse?
    suspend fun updateNotificationStatus(updateNewsStatusRequest: UpdateNotificationState)
}

class NotificationUseCaseImpl @Inject constructor(
    private val utilsRepositoryImpl: UtilsRepositoryImpl
) : NotificationUseCase {

    override suspend fun getNotifications(request: GetNotificationsRequest): NotificationHistoryResponse? {
        val response = utilsRepositoryImpl.getNotifications(getClientToken(), request)
        return if (response.status == Status.SUCCESS) {
            response.data
        } else null
    }

    override suspend fun updateNotificationStatus(updateNewsStatusRequest: UpdateNotificationState) {
        utilsRepositoryImpl.updateNotificationStatus(getClientToken(), updateNewsStatusRequest)
    }


}