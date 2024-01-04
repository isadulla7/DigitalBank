package uz.fido.universaldigital.ui.fragments.profile.about_bank

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentPublicOfferBinding
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class PublicOfferFragment : BaseFragment<FragmentPublicOfferBinding, MenuProfileViewModel>(
    FragmentPublicOfferBinding::inflate, MenuProfileViewModel::class.java
) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        loadPublicOffer()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun loadPublicOffer() {
        showProgress()
        binding.webview.loadUrl("https://universalbank.uz/about-bank")
        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (isVisible && !isDetached) {
                    hideProgress()
                }
            }
        }
    }


}