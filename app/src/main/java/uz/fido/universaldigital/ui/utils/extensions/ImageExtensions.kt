package uz.fido.universaldigital.ui.utils.extensions

import android.content.Context
import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import okhttp3.OkHttpClient
import uz.fido.universaldigital.R
import uz.fido.utils.const.Const
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

fun ImageView.loadUserImage() {
    if ((Paper.book().read(Const.PAPER_USER_PHOTO_PATH, "") ?: "").isNotEmpty()) {
        Picasso.get()
            .load(Paper.book().read(Const.PAPER_USER_PHOTO_PATH, ""))
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.ic_user)
            .into(this)
    } else {
        this.setImageResource(R.drawable.ic_user)
    }
}

fun ImageView.loadImage(context: Context, imageUrl: String, placeHolder: Int) {
    if (imageUrl.isNotEmpty()) {
        load(imageUrl, coilDownloader(context)) {
            crossfade(false)
            placeholder(placeHolder)
            error(placeHolder)
        }
    } else {
        this.setImageResource(placeHolder)
    }
}

fun coilDownloader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .okHttpClient(getUnsafeOkHttpClient())
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()
}

private fun getUnsafeOkHttpClient(): OkHttpClient {
    return try {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
        })
        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { _, _ -> true }
        builder.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}

