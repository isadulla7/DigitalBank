package uz.fido.universaldigital.ui.fragments.monitoring.cheque

import android.annotation.SuppressLint
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

    private var html: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            html = it.getString("html")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClick()
        loadView()
    }

    private fun setOnClick() {
        binding.close.setOnClickListener { pop() }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadView() {
        val displayWidth = getScreenWidth(requireActivity())
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.builtInZoomControls = true
            settings.loadWithOverviewMode = true
            settings.textZoom = 150
            overScrollMode = WebView.OVER_SCROLL_NEVER
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            setInitialScale(if (displayWidth != 0) (displayWidth * 0.14).toInt() else 100)
            settings.displayZoomControls = false
            loadDataWithBaseURL(null, html!!, "text/html", "UTF-8", null)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }
}