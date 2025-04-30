package uz.fido.universaldigital.ui.fragments.services.qr_payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.payment.qr.QrCode
import uz.fido.network.domain.model.qr_code.QrCodeConvert
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentQrPaymentBinding
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import java.io.StringReader
import java.util.concurrent.Executors
import kotlin.math.abs

@AndroidEntryPoint
@SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
class QrPaymentFragment : BaseSimpleFragment<FragmentQrPaymentBinding>(
    FragmentQrPaymentBinding::inflate
) {

    private lateinit var list: ArrayList<QrCode>
    private lateinit var camera: Camera

    private var lensFacing = CameraSelector.LENS_FACING_BACK

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var barcodeScanner: BarcodeScanner? = null
    private var previewUseCase: Preview? = null
    private var imageProxy: ImageProxy? = null

    private var status: Boolean = false
    private var isErrorShowed = false

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        insertServiceList()
        setupCamera()
    }

    private fun changeFlashLightState(status: Boolean) {
        if (status) {
            binding.additional.setImageResource(R.drawable.ic_flashlight_off)
        } else {
            binding.additional.setImageResource(R.drawable.ic_flashlight_on)
        }
        camera.cameraControl.enableTorch(status)
        this.status = status
    }

    private fun insertServiceList() {
        list = ArrayList()
        list.add(QrCode(getString(R.string.version_standard), "00", "", 2))
        list.add(QrCode(getString(R.string.tip_qr_code), "01", "", 2))
        list.add(QrCode(getString(R.string.info_about_delivery), "40", "", 0))
        list.add(QrCode(getString(R.string.unique_id_payment_system), "00", "", null))
        list.add(QrCode(getString(R.string.unique_qr_of_payment_system), "01", "", null))
        list.add(QrCode(getString(R.string.payment_subject), "02", "", null))
        list.add(QrCode(getString(R.string.unique_id_service_and_goods), "03", "", null))
        list.add(QrCode(getString(R.string.name_good_and_service), "04", "", null))
        list.add(QrCode(getString(R.string.code_category_of_delivery), "52", "", 4))
        list.add(QrCode(getString(R.string.currency_of_payment), "53", "", 3))
        list.add(QrCode(getString(R.string.payment_amount), "54", "", null))
        list.add(QrCode(getString(R.string.free_type), "55", "", 2))
        list.add(QrCode(getString(R.string.commission_fee_amount), "56", "", null))
        list.add(QrCode(getString(R.string.commission_percentage), "57", "", null))
        list.add(QrCode(getString(R.string.code_of_country), "58", "", 2))
        list.add(QrCode(getString(R.string.service_provider_name), "59", "", null))
        list.add(QrCode(getString(R.string.location_of_service_provider), "60", "", null))
        list.add(QrCode(getString(R.string.postcode), "61", "", null))
        list.add(QrCode(getString(R.string.additional_payment_information), "62", "", 0))
        list.add(QrCode(getString(R.string.check_number), "01", "", null))
        list.add(QrCode(getString(R.string.mobile_phone_number), "02", "", null))
        list.add(QrCode(getString(R.string.product_label), "03", "", null))
        list.add(QrCode(getString(R.string.loyalty_program_code), "04", "", null))
        list.add(QrCode(getString(R.string.reference_label), "05", "", null))
        list.add(QrCode(getString(R.string.consumer_label), "06", "", null))
        list.add(QrCode(getString(R.string.terminal_label), "07", "", null))
        list.add(QrCode(getString(R.string.purpose_of_payment), "08", "", null))
        list.add(QrCode(getString(R.string.request_additional_info_from_consumer), "09", "", null))
        list.add(QrCode(getString(R.string.localized_service_provider_information), "64", "", 0))
        list.add(QrCode(getString(R.string.language_cod), "00", "", 2))
        list.add(QrCode(getString(R.string.localized_name_of_service_provider), "01", "", null))
        list.add(QrCode(getString(R.string.localized_location_of_service_provider), "02", "", null))
        list.add(QrCode(getString(R.string.additional_info_about_service_provider), "80", "", null))
        list.add(QrCode(getString(R.string.unique_id_payment_system), "00", "", 0))
        list.add(QrCode(getString(R.string.processing_center_id_branch_code), "01", "", null))
        list.add(QrCode(getString(R.string.unique_id_terminal_electron_wallet), "02", "", null))
        list.add(QrCode(getString(R.string.phone_number), "03", "", 12))
        list.add(QrCode(getString(R.string.address_mail), "04", "", null))
        list.add(QrCode(getString(R.string.control_amount), "63", "", 4))
    }

    private fun setupCamera() {
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[CameraXViewModel::class.java].processCameraProvider.observe(viewLifecycleOwner) { provider: ProcessCameraProvider? ->
            cameraProvider = provider
            bindCameraUseCases()
        }
    }

    private fun bindCameraUseCases() {
        bindPreviewUseCase()
        bindAnalyseUseCase()
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }
        previewUseCase = Preview.Builder().setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(binding.previewView.display.rotation).build()
            .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }
        try {
            cameraProvider!!.bindToLifecycle(
                this, cameraSelector!!, previewUseCase
            )
            camera = cameraProvider!!.bindToLifecycle(
                this, cameraSelector!!, previewUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            illegalStateException.printStackTrace()
        } catch (illegalArgumentException: IllegalArgumentException) {
            illegalArgumentException.printStackTrace()
        }
    }

    private fun bindAnalyseUseCase() {
        BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
        barcodeScanner = BarcodeScanning.getClient()

        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }

        analysisUseCase = ImageAnalysis.Builder().setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(binding.previewView.display.rotation).build()

        // Initialize our background executor
        val cameraExecutor = Executors.newSingleThreadExecutor()

        analysisUseCase?.setAnalyzer(cameraExecutor) { imageProxy ->
            this.imageProxy = imageProxy
            processImageProxy(barcodeScanner!!, imageProxy)
        }

        try {
            cameraProvider!!.bindToLifecycle(
                this, cameraSelector!!, analysisUseCase
            )
        } catch (illegalStateException: IllegalStateException) {
            illegalStateException.printStackTrace()
        } catch (illegalArgumentException: IllegalArgumentException) {
            illegalArgumentException.printStackTrace()
        }
    }

    private fun processImageProxy(
        barcodeScanner: BarcodeScanner, imageProxy: ImageProxy
    ) {
        val inputImage =
            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(inputImage).addOnSuccessListener { barcodes ->
            barcodes.forEach {
                val reader = JsonReader(StringReader(it.displayValue))
                val values = it.displayValue
                reader.isLenient = true
                values?.let { data ->
                    filterData(data)
                }
            }
        }.addOnFailureListener {
        }.addOnCompleteListener {
            imageProxy.close()
        }
    }

    private fun filterData(data: String) {
        var values = data
        when {
            values.startsWith("0") -> {
                for (i in list.indices) {
                    if (values.isNotEmpty()) {
                        var value = values.substring(0, 2)
                        if (value == list[i].id) {
                            values = values.substring(2, values.length)
                            if (list[i].length != null) {
                                val length = list[i].length
                                value = values.substring(2, 2 + length!!)
                                values = values.substring(2 + length, values.length)
                                list[i].value = value
                            } else {
                                val length = Integer.valueOf(values.substring(0, 2))
                                list[i].length = length
                                value = values.substring(2, 2 + length)
                                values = values.substring(2 + length, values.length)
                                list[i].value = value
                            }
                        }
                    }
                }
                try {
                    barcodeScanner?.close()
                    imageProxy?.close()
                    gotoWithSlide(
                        R.id.paymentFragment, bundle = bundleOf(
                            PaymentFragment.PAYMENT_OPERATION to PaymentFragment.PAYMENT_OPERATION_QR,
                            "qr_list" to list
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            values.startsWith("card_number") -> {
                val cardNumber = values.substring(values.indexOf(":") + 1, values.length)
                barcodeScanner?.close()
                imageProxy?.close()
                gotoWithSlide(
                    R.id.menuTransfersFragment, bundleOf("card_number" to cardNumber)
                )
            }

            values.startsWith("https") -> {
                if (!isErrorShowed) {
                    isErrorShowed = true
                    showSnackbar(getString(R.string.qr_not_supported)) {
                        pop()
                    }
                }
            }

            else -> {
                try {
                    val qrCodeMunis0204: QrCodeConvert =
                        Gson().fromJson(values, QrCodeConvert::class.java)
                    if (qrCodeMunis0204.p_acc != null) {
                        barcodeScanner?.close()
                        imageProxy?.close()
                        if (qrCodeMunis0204.is_notary_service != null) {
                            gotoWithSlide(
                                R.id.paymentFragment, bundle = bundleOf(
                                    PaymentFragment.PAYMENT_OPERATION to PaymentFragment.PAYMENT_OPERATION_PAYMENT,
                                    PaymentFragment.PAYMENT_SERVICE_ID to 22.toString(),
                                    PaymentFragment.PAYMENT_SERVICE_DEFAULT_VALUE to qrCodeMunis0204.p_acc
                                )
                            )
                        } else {
                            gotoWithSlide(
                                R.id.paymentFragment, bundle = bundleOf(
                                    PaymentFragment.PAYMENT_OPERATION to PaymentFragment.PAYMENT_OPERATION_PAYMENT,
                                    PaymentFragment.PAYMENT_SERVICE_ID to 18.toString(),
                                    PaymentFragment.PAYMENT_SERVICE_DEFAULT_VALUE to qrCodeMunis0204.p_acc
                                )
                            )
                        }
                        return
                    }
                } catch (e: Exception) {
                    if (arguments != null && !isErrorShowed && values.isNotEmpty() && !values.startsWith(
                            "http"
                        )
                    ) {
                        isErrorShowed = true
                        setFragmentResult(QR_SCANNER_RESULT, bundleOf("result" to data))
                        pop()
                        return
                    }
                }
            }

        }
    }

    private fun initSetOnClickListeners() {
        binding.back.setOnClickListener { pop() }
        binding.additional.setOnClickListener { changeFlashLightState(!status) }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = width.coerceAtLeast(height).toDouble() / width.coerceAtMost(height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private val screenAspectRatio: Int
        get() {
            val metrics = DisplayMetrics().also { binding.previewView.display?.getRealMetrics(it) }
            return aspectRatio(metrics.widthPixels, metrics.heightPixels)
        }

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        const val QR_SCANNER_RESULT = "qr_scanner_result"
    }
}