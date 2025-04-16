package uz.fido.universaldigital.ui.fragments.products.widgets.notifications.news

import android.os.Build
import android.os.Bundle
import android.text.Html
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.news.News
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentNewsViewBinding
import uz.fido.universaldigital.ui.utils.extensions.loadImage
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MainNewsViewFragment : BaseSimpleFragment<FragmentNewsViewBinding>(FragmentNewsViewBinding::inflate) {

    private var news: News? = null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.appBar.setOnBackButtonClickListener { pop() }
        initDetails()
    }

    private fun initDetails() {
        news = requireArguments().serializable<News>("news") as News
        news?.let { news ->
            val imageUrl = "${Keys.paynetPhotoUrl()}${news.img_url}"
            binding.newsImage.loadImage(requireContext(), imageUrl, R.drawable.cornered_bg_white_10dp)
            binding.newsTitle.text = Html.fromHtml(news.title, Html.FROM_HTML_MODE_LEGACY)
            binding.description.text = Html.fromHtml(news.content, Html.FROM_HTML_MODE_LEGACY)
            binding.date.text = news.date
        }
    }

}