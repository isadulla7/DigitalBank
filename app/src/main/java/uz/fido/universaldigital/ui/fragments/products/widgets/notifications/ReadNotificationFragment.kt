package uz.fido.universaldigital.ui.fragments.products.widgets.notifications

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.news.Notification
import uz.fido.network.domain.model.news.UpdateNotificationState
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentReadNotificationBinding
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class ReadNotificationFragment :
    BaseFragment<FragmentReadNotificationBinding, NotificationsViewModel>(
        FragmentReadNotificationBinding::inflate, NotificationsViewModel::class.java
    ), BaseInterface {

    private var item: Notification? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            item = it.serializable<Notification>("item") as Notification
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.appBar.setOnBackButtonClickListener { pop() }
        init()
    }

    private fun init() {
        item?.let { notification ->
            binding.newsTitle.text = notification.title
            binding.description.text = notification.text
            binding.date.text = notification.created_on
            changeNewsStatus(notification)
        }
    }

    private fun changeNewsStatus(item: Notification) {
        val list = ArrayList<String>()
        list.add(item.notification_id)
        if (item.is_read == "N") viewModel.updateNotificationStatus(
            getClientToken(), UpdateNotificationState(list)
        ).observe(viewLifecycleOwner) {}
    }

}