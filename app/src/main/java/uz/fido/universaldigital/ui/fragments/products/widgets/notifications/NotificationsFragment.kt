package uz.fido.universaldigital.ui.fragments.products.widgets.notifications

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.news.GetNotificationsRequest
import uz.fido.network.domain.model.news.Notification
import uz.fido.network.domain.model.news.UpdateNotificationState
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentNotificationsBinding
import uz.fido.universaldigital.ui.fragments.payment.templates.adapter.PaymentTemplatesAdapter
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.sticky.EndlessRecyclerViewScrollListener
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationsBinding, NotificationsViewModel>(
    FragmentNotificationsBinding::inflate, NotificationsViewModel::class.java
), BaseInterface {

    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var shimmerAdapter: PaymentTemplatesAdapter
    private val menuProductViewModel by activityViewModels<MenuProductsViewModel>()

    private var notificationsAdapter: NotificationsAdapter? = null
    private var skeletonScreen: SkeletonScreen? = null
    private var news = ArrayList<Notification>()
    private var pageNumber = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSetOnClickListeners()
        initNotifications()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun initNotifications() {
        shimmerAdapter = PaymentTemplatesAdapter(this)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pageNumber = page - 1
                getNotification((page - 1).toString())
            }
        }
        binding.notifications.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            notificationsAdapter = NotificationsAdapter(this@NotificationsFragment, news)
            adapter = notificationsAdapter
            addOnScrollListener(scrollListener)
        }
        binding.shimmerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = shimmerAdapter
        }
        if (news.isEmpty()) getNotification("0")
    }

    internal fun getNotification(page: String) {
        if (page == "0") {
            scrollListener.resetState()
            binding.shimmerView.visibility = View.VISIBLE
            skeletonScreen = showSkeleton(binding.shimmerView, shimmerAdapter, R.layout.shimmer_item_branch, 8)
        } else {
            binding.progressBar.visibility = View.VISIBLE

        }
        viewModel.getNotifications(
            getClientToken(), GetNotificationsRequest(
                page_number = page, page_item_size = "20"
            )
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    if (skeletonScreen != null) {
                        skeletonScreen?.hide()
                        binding.shimmerView.visibility = View.GONE
                    }
                    binding.progressBar.visibility = View.INVISIBLE
                    if (!news.containsAll(it.data!!.notifications)) {
                        news.addAll(it.data!!.notifications)
                    }
                    notificationsAdapter!!.setList(news)
                    binding.emptyView.isVisible = news.isEmpty()
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    override fun openNotification(item: Notification) {
        super<BaseFragment>.openNotification(item)
        item.is_read = "Y"
        notificationsAdapter!!.notifyItemChanged(news.indexOf(item))
        val filter = news.filter { it.is_read == "N" }
        val arraylist = arrayListOf<Notification>()
        arraylist.addAll(filter)
        menuProductViewModel.setNotificationList(arraylist)
        val list = ArrayList<String>()
        list.add(item.notification_id)
        viewModel.updateNotificationStatus(
            getClientToken(), UpdateNotificationState(list)
        ).observe(viewLifecycleOwner) {}
        gotoWithSlide(R.id.readNotificationFragment, bundleOf("item" to item))
    }

}