package uz.fido.universaldigital.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.Const
import uz.myid.android.sdk.capture.MyIdClient
import uz.myid.android.sdk.capture.MyIdConfig
import uz.myid.android.sdk.capture.MyIdException
import uz.myid.android.sdk.capture.MyIdResult
import uz.myid.android.sdk.capture.MyIdResultListener
import uz.myid.android.sdk.capture.model.MyIdBuildMode
import uz.myid.android.sdk.capture.model.MyIdCameraShape
import uz.myid.android.sdk.capture.model.MyIdEntryType
import uz.myid.android.sdk.capture.model.MyIdImageFormat
import uz.myid.android.sdk.capture.model.MyIdResidentType
import uz.myid.android.sdk.capture.model.MyIdResolution
import uz.myid.android.sdk.capture.takeUserResult
import java.util.Locale

/**
 * This is activity of MY ID
 * All views of activity is SDK(you can't change it)
 */
class FaceIdActivity : BaseActivity(), MyIdResultListener {

    private val client: MyIdClient = MyIdClient()
    private var clientPassport: String = ""
    private var clientBirthday: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_App_SplashScreen)
        getArgs()
        startMyId()
    }

    /**
     * This is main function of this activity, this function calls MY_ID, in this function:
     * client_id is unique id of bank in MY_ID
     * there are 2 types of EntryType: AUTH and FACE
     * there are 2 types of BuildMode: PRODUCTION and DEBUG
     * organization details is optional*
     */

    private fun startMyId() {
        val myIdConfig = MyIdConfig.builder(clientId = Keys.getMyIdClientId())
            .withClientHash(Keys.getMyIdClientHash(), Keys.getMyIdClientHashId())
            .withPassportData(clientPassport).withBirthDate(clientBirthday)
            .withBuildMode(MyIdBuildMode.PRODUCTION).withEntryType(MyIdEntryType.AUTH)
            .withResidency(MyIdResidentType.USER_DEFINED).withLocale(Locale(initLanguage()))
            .withCameraShape(MyIdCameraShape.CIRCLE)
            .withResolution(MyIdResolution.RESOLUTION_720).withImageFormat(MyIdImageFormat.PNG)
            .build()
        val intent = client.createIntent(this, myIdConfig)
        result.launch(intent)
    }

    private val result = takeUserResult(this)

    /**
     * MY ID result is successful
     * You can go back now
     */
    override fun onSuccess(result: MyIdResult) {
        try {
            val resultIntent = Intent()
            resultIntent.putExtra("code", result.code)
            setResult(RESULT_OK, resultIntent)
            onBackPressed()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onError(exception: MyIdException) {
        Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
        val resultIntent = Intent()
        resultIntent.putExtra("code", exception.code)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onUserExited() {
        finish()
    }

    private fun initLanguage(): String {
        return when (getFromPaper(Const.APP_LANGUAGE, "ru").lowercase(Locale.getDefault())) {
            LANG_UZ, LANG_UZL -> LANG_UZ
            LANG_RU -> LANG_RU
            else -> LANG_EN
        }
    }

    private fun getArgs() {
        intent.extras?.let {
            clientPassport = it.getString(CLIENT_PASSPORT).orEmpty()
            clientBirthday = it.getString(CLIENT_DATE_OF_BIRTH).orEmpty()
        }
    }

    companion object {
        const val CLIENT_DATE_OF_BIRTH = "birthday"
        const val CLIENT_PASSPORT = "passport"
        const val ERROR_CODE_WRONG_PASSPORT_DATA = "2"
        const val MODE = "mode"
        const val STRONG = "strong"
        const val LANG_RU = "ru"
        const val LANG_UZ = "uz"
        const val LANG_UZL = "uzl"
        const val LANG_EN = "eng"
    }

}