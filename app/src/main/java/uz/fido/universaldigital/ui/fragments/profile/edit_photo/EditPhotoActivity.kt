package uz.fido.universaldigital.ui.fragments.profile.edit_photo

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.google.android.gms.common.util.IOUtils
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityEditPhotoBinding
import uz.fido.universaldigital.ui.utils.file.FileUtils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * This activity used for edit photo picked by gallery
 * You can resize and crop photo
 */
class EditPhotoActivity : BaseActivity() {

    private lateinit var binding: ActivityEditPhotoBinding
    private lateinit var mContentResolver: ContentResolver

    private val mOutputFormat = Bitmap.CompressFormat.JPEG
    private var newImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initSetOnClickListeners()
        initImageBoundListener()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { finish() }
        binding.btnSave.setOnClickListener {
            saveButtonClickEvent()
        }
    }

    private fun initImageBoundListener() {
        binding.imageView.setImageBoundsListener { binding.cropOverlay.imageBounds }
    }

    private fun init() {
        val uri = intent!!.getStringExtra("uri")!!
        var storagePhotoUri: Uri = uri.toUri()
        if (!uri.contains("${BuildConfig.APPLICATION_ID}.my.package.name.provider")) {
            try {
                storagePhotoUri =
                    Uri.fromFile(File(FileUtils.realPathFromUriApi19(this, uri.toUri())!!))
            } catch (e: Exception) {
                onBackPressed()
            }
        }
        mContentResolver = applicationContext.contentResolver
        newImageFile = createDirPath()
        copy(this, storagePhotoUri, newImageFile)
        val mSaveUri = Uri.fromFile(newImageFile)
        val bitmap = getBitmap(mSaveUri)
        val drawable = BitmapDrawable(resources, bitmap)
        val minScale = binding.imageView.setMinimumScaleToFit(drawable)
        binding.imageView.maximumScale = minScale * 3
        binding.imageView.mediumScale = minScale * 2
        binding.imageView.scale = minScale
        binding.imageView.setImageDrawable(drawable)
        binding.imageView.isDrawingCacheEnabled = true
    }

    private fun createDirPath(): File {
        val directoryPath =
            getExternalFilesDir(null)?.absolutePath.toString() + LOCALE_IMAGE_DIRECTORY
        val directory = File(directoryPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)
        val date = dateFormat.format(Calendar.getInstance().time)
        val newFileName = "$date.jpg"
        return File(directoryPath, newFileName)
    }

    private fun getBitmap(uri: Uri): Bitmap? {
        var inputStream: InputStream?
        val returnedBitmap: Bitmap?
        try {
            inputStream = mContentResolver.openInputStream(uri)
            //Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream!!.close()
            var scale = 1
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = 2.0.pow(
                    (ln(
                        IMAGE_MAX_SIZE / o.outHeight.coerceAtLeast(o.outWidth).toDouble()
                    ) / ln(0.5)).roundToInt().toDouble()
                ).toInt()
            }

            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = mContentResolver.openInputStream(uri)
            var bitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream!!.close()

            //First check
            val ei = ExifInterface(uri.path!!)
            when (ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    returnedBitmap = rotateImage(bitmap!!, 90F)
                    //Free up the memory
                    bitmap.recycle()
                    bitmap = null
                }

                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    returnedBitmap = rotateImage(bitmap!!, 180F)
                    //Free up the memory
                    bitmap.recycle()
                    bitmap = null
                }

                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    returnedBitmap = rotateImage(bitmap!!, 270F)
                    //Free up the memory
                    bitmap.recycle()
                    bitmap = null
                }

                else -> returnedBitmap = bitmap
            }
            return returnedBitmap

        } catch (e: FileNotFoundException) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
        return null
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
        try {
            val inputStream: InputStream =
                context.contentResolver.openInputStream(srcUri!!) ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            IOUtils.copyStream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isSuccessFullySaved(): Boolean {
        val croppedImage = binding.imageView.croppedImage ?: return false
        if (newImageFile != null) {
            try {
                val fileOutputStream = FileOutputStream(newImageFile)
                croppedImage.let {
                    croppedImage.compress(mOutputFormat, 90, fileOutputStream)
                }
                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
                return false
            }
        } else {
            return false
        }
        croppedImage.recycle()
        return true
    }

    private fun saveButtonClickEvent() {
        if (isSuccessFullySaved()) {
            val returnIntent = Intent()
            returnIntent.putExtra(RESULT_IMAGE, newImageFile!!.toUri().toString())
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        } else Toast.makeText(this, getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val IMAGE_MAX_SIZE = 1024
        const val RESULT_IMAGE = "result_image"
        const val LOCALE_IMAGE_DIRECTORY = "/Universal Digital/Images/"
        const val DATE_FORMAT = "yyyy-MM-dd_HH:mm:ss"
    }

}