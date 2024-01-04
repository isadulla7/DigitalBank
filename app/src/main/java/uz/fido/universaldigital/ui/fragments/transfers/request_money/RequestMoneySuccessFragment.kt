package uz.fido.universaldigital.ui.fragments.transfers.request_money

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.QrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColors
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorShapes
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentRequestMoneySuccessBinding

@AndroidEntryPoint
class RequestMoneySuccessFragment :
    BaseSimpleFragment<FragmentRequestMoneySuccessBinding>(FragmentRequestMoneySuccessBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        generateQrCode(requireArguments().getString("url").toString())
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
        ).build()
        binding.qrCode.setImageDrawable(QrCodeDrawable(data, options))
    }

}