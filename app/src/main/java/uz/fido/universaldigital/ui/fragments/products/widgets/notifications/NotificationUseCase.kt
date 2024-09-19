package uz.fido.universaldigital.ui.fragments.products.widgets.notifications

import uz.fido.network.data.repository.UtilsRepositoryImpl
import javax.inject.Inject

interface NotificationUseCase {
//    suspend fun getNotifications(request: GetNotificationsRequest): NotificationHistoryResponse?
//    suspend fun updateNotificationStatus(updateNewsStatusRequest: UpdateNotificationState)
}

class NotificationUseCaseImpl @Inject constructor(
    private val utilsRepositoryImpl: UtilsRepositoryImpl
) : NotificationUseCase {




}