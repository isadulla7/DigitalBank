package uz.fido.universaldigital.ui.fragments.payment.abc_dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.databinding.DialogBottomQrCodeBinding

class BottomQRcodeDialog(private var qrcode: String) : BottomSheetDialogFragment() {


    private lateinit var binding: DialogBottomQrCodeBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog
                .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return bottomSheetDialog
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomQrCodeBinding.inflate(inflater, container, false)
        bottomSheetInfoQr(qrcode)
        return binding.root
    }

    private fun bottomSheetInfoQr(check_qr: String) {
        if (check_qr.length > 8){
            if (check_qr.startsWith("https://")){
                binding.webView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        view.loadUrl(request.url.toString())
                        return true
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)
                        binding.progress.visibility = View.INVISIBLE
                        binding.tvItem.visibility = View.INVISIBLE
                    }
                }
                binding.webView.settings.javaScriptEnabled = true
                binding.webView.loadUrl(check_qr)
            }else{

//                 binding.tvItem.text = check_qr
//                 binding.progress.visibility = View.INVISIBLE
//                dialog.setContentView(binding.root)
//                dialog.show()
            }}else{

        }
    }
}