package uz.fido.universaldigital.ui.fragments.monitoring.cheque

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.view.View
import androidx.core.content.FileProvider
import uz.fido.network.domain.model.p2p.TransferDto
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentTransferPdfChequeBinding
import uz.fido.universaldigital.ui.fragments.transfers.success.SuccessTransferFragment
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.format.Format
import uz.fido.utils.utility.fragment.pop
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.sqrt

class TransferChequeFragment : BaseSimpleFragment<FragmentTransferPdfChequeBinding>(FragmentTransferPdfChequeBinding::inflate) {

    private lateinit var pdfRenderer: PdfRenderer
    private lateinit var currentPage: PdfRenderer.Page
    private lateinit var parcelFileDescriptor: ParcelFileDescriptor
    private lateinit var transferDto: TransferDto
    private lateinit var file: File
    private var childName = ""
    private val dateFormat2 = SimpleDateFormat("ddMMyyyyhhmmss", Locale.getDefault())
    private var operation = ""
    private lateinit var model: TransferChequeModel

    companion object {
        const val OPERATION_MONITORING = "operation_monitoring"
        const val OPERATION_P2P = "operation_p2p"
        const val CHEQUE_MODEL = "cheque_model"
        const val OPERATION = "operation"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            operation = requireArguments().getString(OPERATION).toString()
            if (operation == OPERATION_MONITORING) {
                model = requireArguments().serializable<TransferChequeModel>(CHEQUE_MODEL) as TransferChequeModel
            } else {
                transferDto = requireArguments().serializable<TransferDto>(SuccessTransferFragment.TRANSFER_DTO) as TransferDto
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSetOnClickListeners()
        drawPdfCheque()
    }

    private fun drawPdfCheque() {
        if (operation == OPERATION_P2P) {
            val percent = (transferDto.commission ?: 0.0).toBigDecimal()
            val commissionAmount = percent * (transferDto.transferAmount?.toBigDecimal()?.divide(BigDecimal(10000)) ?: BigDecimal(0))
            val totalAmount = transferDto.transferAmount?.toBigDecimal()?.divide(BigDecimal(100))?.plus(commissionAmount)
            val model = TransferChequeModel(
                transactionDate = requireArguments().getString("operation_date").toString(),
                transactionAmount = Format.formatAmount((transferDto.transferAmount?.toDouble()?.div(100)).toString()) + " " + getString(
                    R.string.sum_text
                ),
                transactionFee = "$percent % (" + Format.formatAmount(commissionAmount.toString()) + " " + getString(
                    R.string.sum_text
                ) + ")",
                transactionNumber = transferDto.requestId.orEmpty(),
                senderCardNumber = Format.formatCardNumberForCheque(transferDto.senderCard?.object_value ?: ""),
                senderCardName = transferDto.senderCard?.embossed_name.orEmpty(),
                receiverCardName = transferDto.receiverCard?.card_owner.orEmpty(),
                receiverCardNumber = Format.formatCardNumberForCheque(transferDto.receiverCard?.card_number ?: ""),
                operationName = "(${getOperationName()})",
                totalAmount = "${uz.fido.utils.utility.format.Format.formatAmount(totalAmount.toString())} ${getString(uz.fido.utils.R.string.sum)}"
            )
            drawCheque(model)
        } else if (operation == OPERATION_MONITORING) {
            if (this::model.isInitialized) {
                drawCheque(model)
            }
        } else {
            toast(getString(uz.fido.utils.R.string.unkknown_error))
            pop()
        }
    }

    private fun initSetOnClickListeners() {
        binding.apply {
            appBar.setOnBackButtonClickListener { pop() }
            btnShare.setOnClickListener {
                share(file)
            }
            btnSave.setOnClickListener {
                toast(getString(R.string.successfully_saved))
            }
        }
    }

    private fun share(file: File) {
        try {
            // Get URI for the file (use FileProvider if it's in private storage)
            val uri = FileProvider.getUriForFile(requireContext(), requireActivity().applicationContext.packageName.toString() + ".my.package.name.provider", file)
            // Create an intent to share the file
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/pdf"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            // Grant temporary permission to read the file
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // Start the share intent
            startActivity(Intent.createChooser(shareIntent, "Share PDF"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun drawCheque(model: TransferChequeModel) {
        createPdfDocument(model)
        openPdf()
    }

    private fun getOperationName(): String {
        return when (transferDto.operation) {
            SuccessTransferFragment.TRANSFER_BY_CARD -> getString(R.string.transfer_to_card)
            SuccessTransferFragment.TRANSFER_BY_PHONE -> getString(R.string.by_phone_number)
            SuccessTransferFragment.TRANSFER_BY_WALLET -> getString(R.string.by_wallet_number)
            SuccessTransferFragment.TRANSFER_OVER_MY_CARDS -> getString(R.string.over_my_cards)
            else -> getString(R.string.transfer)
        }
    }

    private fun openPdf() {
        try {
            // Load your PDF file from assets or internal storage
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                childName
            )
            parcelFileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

            // Create PDF Renderer
            pdfRenderer = PdfRenderer(parcelFileDescriptor)

            // Open the first page
            openPage()
        } catch (e: Exception) {
            println("heyyyy$e")
            e.printStackTrace()
        }
    }

    private fun openPage() {
        if (::currentPage.isInitialized) {
            currentPage.close()
        }

        currentPage = pdfRenderer.openPage(0)

        val width = currentPage.width
        val height = currentPage.height

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        binding.chequeImage.setImageBitmap(bitmap)
    }

    private fun createPdfDocument(model: TransferChequeModel) {

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(960, 1440, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        //white box
        val paint = Paint()
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)

        //logo of bank
        val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.universalbank_with_name)
        val scaledBitmap = Bitmap.createScaledBitmap(logoBitmap, 400, 400, true)
        canvas.drawBitmap(scaledBitmap, 0f, -120f, null)

        val typefaceRegular = Typeface.createFromAsset(requireContext().assets, "fonts/Inter-Regular.ttf")
        val typefaceMedium = Typeface.createFromAsset(requireContext().assets, "fonts/Inter-Medium.ttf")
        val typefaceBold = Typeface.createFromAsset(requireContext().assets, "fonts/Inter-Bold.ttf")

        val tinyText = Paint()
        tinyText.color = Color.BLACK
        tinyText.typeface = typefaceRegular
        tinyText.textSize = 20f

        val paintRegularText = Paint()
        paintRegularText.color = Color.BLACK
        paintRegularText.typeface = typefaceRegular
        paintRegularText.textSize = 28f

        val paintMediumText = Paint()
        paintMediumText.color = Color.BLACK
        paintMediumText.typeface = typefaceMedium
        paintMediumText.textSize = 28f

        val paintBoldHeader = Paint()
        paintBoldHeader.color = Color.BLACK
        paintBoldHeader.typeface = typefaceBold
        paintBoldHeader.textSize = 36f

        val paintAmount = Paint()
        paintAmount.color = Color.BLACK
        paintAmount.typeface = typefaceBold
        paintAmount.textSize = 30f

        val startPositionX = 50f
        var startPositionY = 360f

        val paintDottedLine = Paint()
        paintDottedLine.color = Color.BLACK
        paintDottedLine.strokeWidth = 2f

        canvas.drawText(
            getString(R.string.receipt).uppercase(),
            pageInfo.pageWidth / 2 - paintBoldHeader.measureText(getString(R.string.receipt)) / 2,
            250f,
            paintBoldHeader
        )
        canvas.drawText(
            model.operationName,
            pageInfo.pageWidth / 2 - paintMediumText.measureText(model.operationName) / 2,
            300f,
            paintMediumText
        )

        // Draw a dotted line
        drawDottedLine(
            canvas,
            startPositionX,
            350f,
            pageInfo.pageWidth - startPositionX,
            350f,
            paintDottedLine
        )

        if (model.senderCardNumber.isNotEmpty()) {
            val senderCard = model.senderCardNumber
            startPositionY += 50f
            canvas.drawText(
                getString(R.string.sender_card),
                startPositionX,
                startPositionY,
                paintRegularText
            )
            val xPosition1 =
                pageInfo.pageWidth - paintRegularText.measureText(senderCard) - startPositionX
            canvas.drawText(senderCard, xPosition1, startPositionY, paintMediumText)
        }

        if (model.senderCardName.isEmpty()) {
            model.senderCardName = getString(R.string.not_mentioned)
        }
        if (model.senderCardName.length > 25) {
            model.senderCardName = model.senderCardName.substring(0, 24) + "..."
        }
        startPositionY += 50f
        canvas.drawText(
            getString(R.string.sender_name),
            startPositionX,
            startPositionY,
            paintRegularText
        )
        val xPosition2 =
            pageInfo.pageWidth - paintRegularText.measureText(model.senderCardName) - startPositionX
        canvas.drawText(model.senderCardName, xPosition2, startPositionY, paintMediumText)


        if (model.receiverCardNumber.isNotEmpty()) {
            val receiverCard = model.receiverCardNumber
            startPositionY += 50f
            canvas.drawText(
                getString(R.string.receiver_card),
                startPositionX,
                startPositionY,
                paintRegularText
            )
            val xPosition3 =
                pageInfo.pageWidth - paintRegularText.measureText(receiverCard) - startPositionX
            canvas.drawText(receiverCard, xPosition3, startPositionY, paintMediumText)
        }
        if (model.receiverCardName.isEmpty()) {
            model.receiverCardName = getString(R.string.not_mentioned)
        }
        if (model.receiverCardName.length > 25) {
            model.receiverCardName = model.receiverCardName.substring(0, 24) + "..."
        }
        startPositionY += 50f
        canvas.drawText(
            getString(R.string.receiver_name),
            startPositionX,
            startPositionY,
            paintRegularText
        )
        val xPosition4 =
            pageInfo.pageWidth - paintRegularText.measureText(model.receiverCardName) - startPositionX
        canvas.drawText(model.receiverCardName, xPosition4, startPositionY, paintMediumText)

        if (model.transactionNumber.isNotEmpty()) {
            startPositionY += 50
            canvas.drawText(
                getString(R.string.transaction_number),
                startPositionX,
                startPositionY,
                paintRegularText
            )
            val xPosition5 =
                pageInfo.pageWidth - paintRegularText.measureText(model.transactionNumber) - startPositionX
            canvas.drawText(model.transactionNumber, xPosition5, startPositionY, paintMediumText)
        }

        startPositionY += 50f
        canvas.drawText(
            getString(R.string.date_time),
            startPositionX,
            startPositionY,
            paintRegularText
        )
        val xPosition6 =
            pageInfo.pageWidth - paintRegularText.measureText(model.transactionDate) - startPositionX
        canvas.drawText(model.transactionDate, xPosition6, startPositionY, paintMediumText)


        startPositionY += 50f
        // Draw a dotted line
        drawDottedLine(
            canvas,
            startPositionX,
            startPositionY,
            pageInfo.pageWidth - startPositionX,
            startPositionY,
            paintDottedLine
        )

        if (model.transactionFee.isNotEmpty()) {
            startPositionY += 70f
            canvas.drawText(
                getString(R.string.commission),
                startPositionX,
                startPositionY,
                paintRegularText
            )
            val xPosition7 =
                pageInfo.pageWidth - paintAmount.measureText(model.transactionFee) - startPositionX
            canvas.drawText(model.transactionFee, xPosition7, startPositionY, paintAmount)
        }

        if (model.transactionAmount.isNotEmpty()) {
            startPositionY += 50f
            canvas.drawText(
                getString(R.string.amount),
                startPositionX,
                startPositionY,
                paintRegularText
            )
            val xPosition8 =
                pageInfo.pageWidth - paintAmount.measureText(model.transactionAmount) - startPositionX
            canvas.drawText(model.transactionAmount, xPosition8, startPositionY, paintAmount)
        }

        startPositionY += 50f
        canvas.drawText(
            getString(R.string.to_payment),
            startPositionX,
            startPositionY,
            paintRegularText
        )
        val xPosition8 =
            pageInfo.pageWidth - paintAmount.measureText(model.totalAmount) - startPositionX
        canvas.drawText(model.totalAmount, xPosition8, startPositionY, paintAmount)


        val logoQrCode = BitmapFactory.decodeResource(resources, R.drawable.universalbank12)
        val scaledBitmap1 = Bitmap.createScaledBitmap(logoQrCode, 200, 200, true)

        // Draw logo at the top
        canvas.drawBitmap(scaledBitmap1, 710f, 1194f, null)

        canvas.drawText(
            "universaldigitalbank@gmail.com",
            startPositionX,
            1290f,
            tinyText
        )

        canvas.drawText(
            "www.universalbank.uz",
            startPositionX,
            1340f,
            tinyText
        )

        canvas.drawText(
            "+998712001110",
            startPositionX,
            1390f,
            tinyText
        )

        // Finish the page
        pdfDocument.finishPage(page)

        // Save the document to file
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val suffix = dateFormat2.format(Calendar.getInstance().time).trim()
        childName = "Universalbank_receipt_${suffix}.pdf"
        file = File(directory, childName)
        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            println("heyyyy$e")
            e.printStackTrace()
        }

        // Close the document
        pdfDocument.close()
    }

    private fun drawDottedLine(
        canvas: Canvas,
        startX: Float,
        startY: Float,
        stopX: Float,
        stopY: Float,
        paint: Paint
    ) {
        val distance =
            sqrt(((stopX - startX) * (stopX - startX) + (stopY - startY) * (stopY - startY)).toDouble()).toFloat()
        val dotLength = 10f
        val spaceLength = 5f
        val totalLength = dotLength + spaceLength
        var drawnLength = 0f
        var drawX: Float
        var drawY: Float
        while (drawnLength < distance) {
            // Calculate the next point to draw
            val ratio = drawnLength / distance
            drawX = startX + (stopX - startX) * ratio
            drawY = startY + (stopY - startY) * ratio

            // Draw the dot
            canvas.drawLine(drawX, drawY, drawX + dotLength, drawY, paint)

            // Move the drawn length by the total length of dot and space
            drawnLength += totalLength
        }
    }

    private fun closePdfRenderer() {
        try {
            currentPage.close()
            pdfRenderer.close()
            parcelFileDescriptor.close()
        } catch (e: Exception) {
            recordException(e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closePdfRenderer()
    }


}