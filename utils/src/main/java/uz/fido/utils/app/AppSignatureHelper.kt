package uz.fido.utils.app

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Base64.NO_PADDING
import android.util.Base64.NO_WRAP
import android.util.Base64.encodeToString
import android.util.Log
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays

class AppSignatureHelper(context: Context) : ContextWrapper(context) {

    private val appSignatures: ArrayList<String>
        get() {
            val appCodes = ArrayList<String>()

            try {
                val packageName = packageName
                val packageManager = packageManager
                val signatures = packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                ).signatures
                for (signature in signatures) {
                    val hash = hash(packageName, signature.toCharsString())
                    if (hash != null) {
                        appCodes.add(String.format("%s", hash))
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, "Unable to find package to obtain hash.", e)
            }

            return appCodes
        }

    val appKeyHash: String
        get() {
            return try {
                appSignatures[0]
            } catch (e: Exception) {
                ""
            }
        }

    companion object {
        val TAG: String = AppSignatureHelper::class.java.simpleName

        private const val HASH_TYPE = "SHA-256"
        private const val NUM_HASHED_BYTES = 9
        private const val NUM_BASE64_CHAR = 11

        private fun hash(packageName: String, signature: String): String? {
            val appInfo = "$packageName $signature"
            try {
                val messageDigest = MessageDigest.getInstance(HASH_TYPE)
                messageDigest.update(appInfo.toByteArray(Charset.forName("UTF-8")))
                var hashSignature = messageDigest.digest()

                // truncated into NUM_HASHED_BYTES
                hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
                // encode into Base64
                var base64Hash = encodeToString(hashSignature, NO_PADDING or NO_WRAP)
                base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

                Log.d(TAG, String.format("pkg: %s -- hash: %s", packageName, base64Hash))
                return base64Hash
            } catch (e: NoSuchAlgorithmException) {
                Log.e(TAG, "hash:NoSuchAlgorithm", e)
            }

            return null
        }
    }
}