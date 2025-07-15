package uz.fido.universaldigital.ui.activities.security

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.Const
import uz.fido.utils.security.getFromSecureStore
import uz.myid.android.sdk.capture.MyIdClient
import uz.myid.android.sdk.capture.MyIdConfig
import uz.myid.android.sdk.capture.MyIdException
import uz.myid.android.sdk.capture.MyIdResult
import uz.myid.android.sdk.capture.MyIdResultListener
import uz.myid.android.sdk.capture.model.MyIdCameraResolution
import uz.myid.android.sdk.capture.model.MyIdCameraShape
import uz.myid.android.sdk.capture.model.MyIdEntryType
import uz.myid.android.sdk.capture.model.MyIdEnvironment
import uz.myid.android.sdk.capture.model.MyIdImageFormat
import uz.myid.android.sdk.capture.model.MyIdLocale
import uz.myid.android.sdk.capture.model.MyIdResidency
import uz.myid.android.sdk.capture.takeMyIdResult
import java.util.Locale

/**
 * This is activity of MY ID
 * All views of activity is SDK(you can't change it)
 */
class FaceIdActivity : BaseActivity(), MyIdResultListener {

    private val client: MyIdClient = MyIdClient()
    private var clientPassport: String = ""
    private var clientBirthday: String = ""
    private var isResident: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_App_SplashScreen)
        getArgs()
        startMyId()
    }

    /**
     * This is main function of this activity, this function calls MY_ID, in this function:
     * client_id is unique id of client in MY_ID
     * there are 2 types of EntryType: AUTH and FACE
     * there are 2 types of BuildMode: PRODUCTION and DEBUG
     * organization details is optional*
     */

    private fun startMyId() {
        val residentType = when (isResident) {
            "Y" -> MyIdResidency.Resident
            "N" -> MyIdResidency.NonResident
            else -> {
                clientPassport = ""
                clientPassport = ""
                MyIdResidency.UserDefined
            }
        }
        val myIdConfig = MyIdConfig.Builder(clientId = Keys.getMyIdClientId())
            .withClientHash(Keys.getMyIdClientHash(), Keys.getMyIdClientHashId())
            .withPassportData(clientPassport).withBirthDate(clientBirthday)
            .withEnvironment(MyIdEnvironment.Production)
            .withEntryType(MyIdEntryType.Identification)
            .withResidency(residentType)
            .withLocale(initLanguage())
            .withCameraShape(MyIdCameraShape.Circle)
            .withCameraResolution(MyIdCameraResolution.High)
            .withImageFormat(MyIdImageFormat.PNG)
            .build()
        val intent = client.createIntent(this, myIdConfig)
        result.launch(intent)
    }

    private val result = takeMyIdResult(this)

    /**
     * MY ID result is successful
     * You can go back now with RESULT_OK
     */
    override fun onSuccess(result: MyIdResult) {
        try {
            val resultIntent = Intent()
            resultIntent.putExtra(CODE, result.code)
            setResult(RESULT_OK, resultIntent)
            onBackPressed()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * MY ID result isn't successful
     * You can get exception code and back
     */
    override fun onError(exception: MyIdException) {
        Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
        val resultIntent = Intent()
        resultIntent.putExtra(EXCEPTION_CODE, exception.code)
        setResult(RESULT_CANCELED, resultIntent)
        finish()
    }

    /**
     * When user exited just finish activity
     */
    override fun onUserExited() {
        finish()
    }

    private fun initLanguage(): MyIdLocale {
        return when (getFromSecureStore(Const.APP_LANGUAGE, LANG_RU).lowercase(Locale.getDefault())) {
            LANG_UZ, LANG_UZL -> MyIdLocale.Uzbek
            LANG_RU -> MyIdLocale.Russian
            else -> MyIdLocale.English
        }
    }

    private fun getArgs() {
        intent.extras?.let {
            clientPassport = it.getString(CLIENT_PASSPORT).orEmpty()
            clientBirthday = it.getString(CLIENT_DATE_OF_BIRTH).orEmpty()
            isResident = it.getString(RESIDENCY_TYPE).orEmpty()
        }
    }

    companion object {
        const val ERROR_CODE_OLD_PASSPORT_DATA = 34
        const val ERROR_CODE_WRONG_PASSPORT_DATA = 2
        const val CLIENT_DATE_OF_BIRTH = "birthday"
        const val CLIENT_PASSPORT = "passport"
        const val EXCEPTION_CODE = "exception_code"
        const val RESIDENCY_TYPE = "residency_type"
        const val CODE = "code"
        const val LANG_RU = "ru"
        const val LANG_UZ = "uz"
        const val LANG_UZL = "uzl"
    }

}