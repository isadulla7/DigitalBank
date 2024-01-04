package uz.fido.universaldigital.ui.utils.file

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Objects


object FileUtils {

    val TAG = "FileUtils"

    fun takeScreenShot(view: View): Bitmap? {
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }

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

    fun realPathFromUriApi19(context: Context, uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(
                        type,
                        ignoreCase = true
                    )
                ) return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        val column = "_data"
        val projection = arrayOf(column)
        val cursor = if (uri != null) context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        ) else null
        cursor.use {
            if (it != null && it.moveToFirst()) {
                val index = it.getColumnIndexOrThrow(column)
                val result = it.getString(index)
                it.close()
                return result
            }
        }
        return null
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean =
        "com.google.android.apps.photos.content" == uri.authority

    private fun isExternalStorageDocument(uri: Uri): Boolean =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloadsDocument(uri: Uri): Boolean =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMediaDocument(uri: Uri): Boolean =
        "com.android.providers.media.documents" == uri.authority


}