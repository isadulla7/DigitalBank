package uz.fido.universaldigital.ui.fragments.transfers.request_money

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.QrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColors
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogo
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoPadding
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorLogoShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorShapes
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentRequestMoneySuccessBinding
import uz.fido.universaldigital.ui.activities.MainActivity


@AndroidEntryPoint
class RequestMoneySuccessFragment :
    BaseSimpleFragment<FragmentRequestMoneySuccessBinding>(FragmentRequestMoneySuccessBinding::inflate) {

    private var generatedUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generatedUrl = requireArguments().getString("url").toString()
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        generateQrCode(generatedUrl)
        initSetOnClickListeners()
    }

    private fun generateQrCode(url: String) {
        val data = QrData.Url(url)
        val options = QrVectorOptions.Builder().setColors(
            QrVectorColors(
                dark = QrVectorColor.Solid(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blackColor
                    )
                ), ball = QrVectorColor.Solid(
                    ContextCompat.getColor(requireContext(), R.color.blackColor)
                )
            )
        ).setShapes(
            QrVectorShapes(
                darkPixel = QrVectorPixelShape.RoundCorners(.0f),
                ball = QrVectorBallShape.RoundCorners(.0f),
                frame = QrVectorFrameShape.RoundCorners(.0f),
            )
        ).setLogo(
            QrVectorLogo(
                drawable = ContextCompat
                    .getDrawable(requireContext(), R.drawable.ic_universal_logo_red),
                size = .25f,
                padding = QrVectorLogoPadding.Natural(.1f),
                shape = QrVectorLogoShape.Circle
            )
        ).build()
        binding.qrCode.setImageDrawable(QrCodeDrawable(data, options))
    }

    private fun initSetOnClickListeners() {
        binding.copyUrl.setOnClickListener {
            val clipboard: ClipboardManager? =
                requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("Copied", generatedUrl)
            clipboard?.setPrimaryClip(clip)
        }
        binding.shareUrl.setOnClickListener {
            val intent = Intent()
            intent.setAction(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_TEXT, generatedUrl)
            startActivity(Intent.createChooser(intent, "Share via"))
        }
        binding.gotoMainPage.setOnClickListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

}