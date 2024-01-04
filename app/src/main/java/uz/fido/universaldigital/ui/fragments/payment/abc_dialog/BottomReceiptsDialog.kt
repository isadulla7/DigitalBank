package uz.fido.universaldigital.ui.fragments.payment.abc_dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogBottomReceiptsBinding
import uz.fido.universaldigital.ui.utils.file.FileUtils
import java.io.File

class BottomReceiptsDialog(private var html: String, private var name: String) : BottomSheetDialogFragment(),
    View.OnClickListener {

       private lateinit var binding:DialogBottomReceiptsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DialogBottomReceiptsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadView()

     setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.saveQrCode.setOnClickListener(this)
        binding.shareQrCode.setOnClickListener(this)
        binding.printQrCode.setOnClickListener(this)
    }
    private fun loadView() {
        val displayWidth = getScreenWidth(requireActivity())
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.builtInZoomControls = false
        binding.webView.setInitialScale(if (displayWidth != 0) (displayWidth * 0.14).toInt() else 100)
        binding.webView.settings.displayZoomControls = false
        binding.webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    fun getScreenWidth(@NonNull activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }


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

    private fun save(view: View, operationType: Int) {
        if (operationType == 1)
            takeScreenshot(view)
        else share(view)
    }

    private fun takeScreenshot(view: View) {
        val path = FileUtils.saveImageToGallery(requireContext(), FileUtils.takeScreenShot(view)!!, "Card QR")
        val intent: Intent = Intent().apply {
            val uriPath = FileProvider.getUriForFile(
                requireActivity(),
                requireActivity().applicationContext.packageName + ".my.package.name.provider",
                File(path)
            )
            setDataAndType(uriPath, "image/*")
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    private fun share(view: View) {
        val path = FileUtils.saveImageToGallery(requireContext(), FileUtils.takeScreenShot(view)!!, "${getString(R.string.receipt)} (${name})")
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            val uri =
                FileProvider.getUriForFile(
                    requireActivity(),
                    requireActivity().applicationContext.packageName.toString() + ".my.package.name.provider", File(path)
                )
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = "image/*"
        }
        startActivity(Intent.createChooser(shareIntent, "Send to"))
    }

    private fun startPrintImage(view: WebView) {
//        val path = FileUtils.saveImageToGallery(requireContext(), FileUtils.takeScreenShot(view)!!, "${getString(R.string.receipt)} (${name})")
//        activity?.also { context ->
//        val printManager = primaryBaseActivity.getSystemService(Context.PRINT_SERVICE) as PrintManager?
//        val jobName = "createPDFReport"
//        val adapter: PrintDocumentAdapter = view.createPrintDocumentAdapter()
//        printManager!!.print(jobName, view.createPrintDocumentAdapter(), null)

//        val printHelper = PrintHelper(primaryBaseActivity)
//        printHelper.scaleMode = PrintHelper.SCALE_MODE_FIT
//        val bitmap = BitmapFactory.decodeFile(path)
//        printHelper.printBitmap("${getString(R.string.receipt)} (${name})", bitmap)
//        PrintHelper(primaryBaseActivity).apply {
//            scaleMode = PrintHelper.SCALE_MODE_FIT
//        }.also { printHelper ->
//            val bitmap = BitmapFactory.decodeFile(path)
//            printHelper.printBitmap("${getString(R.string.receipt)} (${name})", bitmap)
//        }
//        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.save_qr_code -> {
                save(binding.webView, 1)
                dismiss()
            }
            R.id.share_qr_code -> {
                save(binding.webView, 2)
                dismiss()
            }
            R.id.print_qr_code -> {
                startPrintImage(binding.webView)
                dismiss()
            }
        }
    }
}