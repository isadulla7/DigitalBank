package uz.fido.universaldigital.ui.fragments.products.widgets.notifications.news

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.news.News
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentNewsViewBinding
import uz.fido.universaldigital.ui.utils.extensions.loadImage
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class MainNewsViewFragment : BaseSimpleFragment<FragmentNewsViewBinding>(
    FragmentNewsViewBinding::inflate
), BaseInterface {

    private lateinit var news: News

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initView()
    }


    private fun initView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        news = requireArguments().serializable<News>("news") as News
        val url = "${Keys.paynetPhotoUrl()}${news.img_url}"
        binding.newsImage.loadImage(requireContext(), url, R.drawable.ic_universal_pattern_1)
        binding.newsTitle.text = news.title
        binding.description.text = news.content
        binding.date.text = news.date
    }


}