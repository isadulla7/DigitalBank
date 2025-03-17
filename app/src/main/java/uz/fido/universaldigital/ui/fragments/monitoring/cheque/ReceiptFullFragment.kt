package uz.fido.universaldigital.ui.fragments.monitoring.cheque

import android.app.Activity
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.webkit.WebView
import android.webkit.WebViewClient
import uz.fido.universaldigital.base.SimpleAbstractFragment
import uz.fido.universaldigital.databinding.FragmentReceiptFullBinding
import uz.fido.utils.utility.fragment.pop

class ReceiptFullFragment : SimpleAbstractFragment<FragmentReceiptFullBinding>(FragmentReceiptFullBinding::inflate) {

    private var html: String?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClick()
    }

    private fun setOnClick() {
      arguments?.let {
          html = it.getString("html")
      }
        binding.close.setOnClickListener { pop() }
        loadView()
    }

    private fun loadView() {
        val displayWidth = getScreenWidth(requireActivity())

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.textZoom = 150
        binding.webView.overScrollMode = WebView.OVER_SCROLL_NEVER
        binding.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        binding.webView.setInitialScale(if (displayWidth != 0) (displayWidth * 0.14).toInt() else 100)
        binding.webView.settings.displayZoomControls = false

        binding.webView.loadDataWithBaseURL(null, html!!, "text/html", "UTF-8", null)
        binding.webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsets(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }
}