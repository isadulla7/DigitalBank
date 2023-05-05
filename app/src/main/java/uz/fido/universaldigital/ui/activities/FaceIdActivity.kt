package uz.fido.universaldigital.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.utils.const.Const.APP_LANGUAGE
import uz.fido.utils.const.MyIdServiceConst.MY_ID_CLIENT_ID
import uz.myid.android.sdk.capture.*
import java.util.*

/**
 * Created by Husniddin Muhammad Amin on 05.05.2023
 * Tashkent, Uzbekistan.
 */

/**
 * This is activity of MY ID
 * All views of activity is SDK(you can't change it)
 */
@AndroidEntryPoint
class FaceIdActivity : BaseActivity(), MyIdResultListener {

    private val client: MyIdClient = MyIdClient()
    private var clientPassport: String = ""
    private var clientBirthday: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val myIdConfig =
            MyIdConfig.builder(MY_ID_CLIENT_ID).withPassportData(clientPassport)
                .withBirthDate(clientBirthday).withEntryType(MyIdEntryType.AUTH)
                .withBuildMode(MyIdBuildMode.PRODUCTION).withLocale(Locale(initLanguage()))
                .withPhoto(false).build()
        val intent = client.createIntent(this, myIdConfig)
        result.launch(intent)
    }

    private val result = takeUserResult(this)

    /**
     * MY ID result is successful
     * You can go back now
     */
    override fun onSuccess(result: MyIdResult) {
        val resultIntent = Intent()
        resultIntent.putExtra("code", result.code.toString())
        setResult(RESULT_OK, resultIntent)
        onBackPressed()
    }

    override fun onError(e: MyIdException) {
        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onUserExited() {
        finish()
    }

    private fun initLanguage(): String {
        return when (Paper.book().read(APP_LANGUAGE, "ru").lowercase(Locale.getDefault())) {
            LANG_UZ, LANG_UZL -> LANG_UZ
            LANG_RU -> LANG_RU
            else -> LANG_EN
        }
    }

    private fun getArgs() {
        val bundle = intent.extras
        bundle?.let {
            clientPassport = it.getString(CLIENT_PASSPORT) ?: ""
            clientBirthday = it.getString(CLIENT_DATE_OF_BIRTH) ?: ""
        }
    }

    companion object {
        const val CLIENT_DATE_OF_BIRTH = "birthday"
        const val CLIENT_PASSPORT = "passport"
        const val LANG_RU = "ru"
        const val LANG_UZ = "uz"
        const val LANG_UZL = "uzl"
        const val LANG_EN = "eng"
    }

}