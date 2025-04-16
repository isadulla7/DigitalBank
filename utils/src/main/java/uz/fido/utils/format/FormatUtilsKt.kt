package uz.fido.utils.format

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Objects

object FormatUtilsKt {

    fun saveImageToGallery(context: Context, bitmap: Bitmap, fileName: String): String {
        try {
            val fos: OutputStream?
            var destPath = ""
            val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM).toString()
            val image = File(imagesDir, "$fileName.jpg")
            if (image.parentFile?.exists() == false) image.parentFile?.mkdir()
            fos = FileOutputStream(image)
            destPath = image.toString()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            Objects.requireNonNull(fos).close()
            fos.flush()
            fos.close()
            return destPath
        } catch (ignored: Exception) {
            return ""
        }
    }
}