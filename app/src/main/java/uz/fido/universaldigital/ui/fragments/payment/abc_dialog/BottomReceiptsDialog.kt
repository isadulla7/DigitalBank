package uz.fido.universaldigital.ui.fragments.payment.abc_dialog

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Insets
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintJob
import android.print.PrintManager
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogBottomReceiptsBinding
import uz.fido.universaldigital.ui.utils.file.FileUtils
import java.io.File
import java.io.FileOutputStream

class BottomReceiptsDialog(private var html: String, private var name: String, val webViewClick: () -> Unit = {}) : BottomSheetDialogFragment(),
    View.OnClickListener {

    private lateinit var binding: DialogBottomReceiptsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomReceiptsBinding.inflate(inflater, container, false)
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
        binding.receiptBlock.setOnClickListener(this)
        binding.webView.setOnClickListener(this)

    }

    private fun loadView() {
        val displayWidth = getScreenWidth(requireActivity())
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.setInitialScale(if (displayWidth != 0) (displayWidth * 0.14).toInt() else 100)
        binding.webView.settings.displayZoomControls = true
        binding.webView.setOnTouchListener { v, event ->
            webViewClick()
            true
        }
        binding.webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
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
        val path = FileUtils.saveImageToGallery(requireContext(), FileUtils.takeScreenShot(view), "Card QR")
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
        val path = FileUtils.saveImageToGallery(requireContext(), FileUtils.takeScreenShot(view), "${getString(R.string.receipt)} (${name})")
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

//    val printAdapter = object : PrintDocumentAdapter() {
//        override fun onLayout(
//            oldAttributes: PrintAttributes?,
//            newAttributes: PrintAttributes?,
//            cancellationSignal: CancellationSignal?,
//            callback: LayoutResultCallback,
//            metadata: Bundle?
//        ) {
//            val printerInfo = printManager.print("screenshot", printAdapter, PrintAttributes.Builder().build())
//
//            if (printerInfo == null) {
//                // Printer mavjud emas
//                // Foydalanuvchiga printer tanlashni taklif qilish
//                Toast.makeText(requireContext(), "Printer topilmadi!", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            // Printer topilsa, chop etish jarayonini davom ettirish
//            callback.onLayoutFinished(
//                PrintDocumentInfo.Builder("screenshot.png")
//                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_PHOTO)
//                    .build(),
//                oldAttributes == newAttributes
//            )
//        }
//
//        override fun onWrite(
//            pages: Array<out PageRange>?,
//            destination: ParcelFileDescriptor?,
//            cancellationSignal: CancellationSignal?,
//            callback: WriteResultCallback
//        ) {
//            destination?.let {
//                val bitmap = captureWebView(binding.webView) // Rasmni olish funksiyasi
//                val outputStream = FileOutputStream(it.fileDescriptor)
//                bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//                outputStream.flush()
//                outputStream.close()
//
//                callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
//            }
//        }
//    }

    private fun startPrintImage() {

//        val printJob = printManager.print(
//            "screenshot",
//            printAdapter,
//            PrintAttributes.Builder().build()
//        )
//        val bitmap=captureWebView(binding.webView)
//        if (bitmap!=null){
//        val uri = saveBitmapToDownloads(requireContext(), bitmap)
//        if (uri != null) {
//            val printIntent = Intent(Intent.ACTION_VIEW).apply {
//                setDataAndType(uri, "image/*")  // Fayl turi (rasm yoki boshqa fayl turini tanlang)
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // Uri'ga ruxsat berish
//            }
//
//            startActivity(printIntent)
//        } else {
//            Toast.makeText(context, "Image save failed!", Toast.LENGTH_SHORT).show()
//        }
//        }
    }
    fun saveBitmapToDownloads(context: Context, bitmap: Bitmap): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "screenshot.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // "Pictures" papkasiga saqlash
            }

            val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val resolver = context.contentResolver
            val imageUri: Uri? = resolver.insert(contentUri, contentValues)

            imageUri?.let {
                resolver.openOutputStream(it).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream!!)
                    outputStream?.flush()
                }
            }
            imageUri
        } else {
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "screenshot.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // File va Uri ni qaytarish
            Uri.fromFile(file)
        }
    }

    private fun captureWebView(webView: WebView): Bitmap? {
        try {
            val bitmap = Bitmap.createBitmap(
                webView.width, webView.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            webView.draw(canvas)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
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
                startPrintImage()
                dismiss()
            }

            R.id.receipt_block, R.id.web_view -> {
                Log.d("TAG", "onClick: ")
                webViewClick()
            }
        }
    }
}