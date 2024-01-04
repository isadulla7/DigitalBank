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

            /*            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val resolver: ContentResolver = context.contentResolver
                            val contentValues = ContentValues()
                            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.jpg")
                            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri)!!)
            //                if (photoFile.parentFile?.exists() == false) photoFile.parentFile?.mkdir()
                            destPath = imageUri?.let { getPath(context, it).toString() }.toString()
                        } else {*/
            val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM).toString()
            val image = File(imagesDir, "$fileName.jpg")
            if (image.parentFile?.exists() == false) image.parentFile?.mkdir()
            fos = FileOutputStream(image)
            destPath = image.toString()
//            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            Objects.requireNonNull(fos).close()

            fos.flush()
            fos.close()
            return destPath

            /*            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val resolver = context.contentResolver
                            val contentValues = ContentValues()
                            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName.replace(".jpg", ""))
                            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, DCIM_FOLDER)
                        }
                        val uri: Uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
                            destPath = FileUtils.getPath(context, uri).toString()
                            fos = resolver.openOutputStream(uri)
                         } else {
                             val dir =
                                 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath.toString() + File.separator + MEDIA_FOLDER
                             val file = File(dir)
                             if (!file.exists()) file.mkdir()

                             val temp = File(dir, fileName)
                             fos = FileOutputStream(temp)

                             val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                             scanIntent.data = Uri.fromFile(temp)
                             context.sendBroadcast(scanIntent)
                             destPath = dir
                         }
                        bitmap.let {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                        }
                */
        } catch (ignored: Exception) {
            return ""
        }
    }
}