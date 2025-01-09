package uz.fido.universaldigital.ui.fragments.products.widgets.notifications.news

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.news.GetNewsRequest
import uz.fido.network.domain.model.news.News
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentNotificationsBinding
import uz.fido.universaldigital.ui.fragments.products.widgets.notifications.NotificationsViewModel
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class NewsFragment : BaseFragment<FragmentNotificationsBinding, NotificationsViewModel>(
    FragmentNotificationsBinding::inflate, NotificationsViewModel::class.java
), BaseInterface {

    private lateinit var newsAdapter: NewsAdapter
    private var news = ArrayList<News>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDetails()
        if (news.isEmpty()) getNews()
    }

    private fun initDetails() {
        binding.notifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            newsAdapter = NewsAdapter(this@NewsFragment, news)
            adapter = newsAdapter
            itemAnimator = null
        }
    }

    override fun openNews(item: News, transitionName: String) {
        super<BaseFragment>.openNews(item, transitionName)
        val bundle = Bundle()
        bundle.putSerializable("news", item)
        goto(R.id.mainNewsViewFragment, bundle)
    }


    private fun getNews() {
        val skeletonScreen = showSkeleton(binding.notifications, newsAdapter, R.layout.shimmer_item_news)
        viewModel.getNewsRequest(getClientToken(), GetNewsRequest("info")).observe(viewLifecycleOwner) {
            it?.let { response ->
                when (response.status) {
                    Status.SUCCESS -> {
                        skeletonScreen.hide()
                        news = response.data!!.news_list
                        newsAdapter.setList(news)
                    }

                    Status.ERROR -> {
                        skeletonScreen.hide()
                        showSnackbar(response.message.toString())
                    }
                }
            }
        }
    }


}