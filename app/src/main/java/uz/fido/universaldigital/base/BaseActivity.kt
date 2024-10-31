package uz.fido.universaldigital.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uz.fido.universaldigital.ui.utils.lang.LocaleHelper
import uz.fido.universaldigital.ui.utils.lang.LocaleHelper.getLanguage
import uz.fido.utils.utility.activity.adjustFontScale
import uz.fido.utils.utility.theme.PreferencesImpl
import uz.fido.utils.utility.theme.ThemeDarkEnum
import uz.fido.utils.view.progress_bar.ProgressBarDialog

abstract class BaseActivity : AppCompatActivity() {

    private val job = Job()
    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)
    private val preference by lazy { PreferencesImpl.instance(this) }
    private var progressBarDialog: ProgressBarDialog? = null

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase!!, getLanguage(newBase)))
    }


    override fun onStart() {
        super.onStart()
        this.adjustFontScale(resources.configuration)
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }

    fun showProgress(progressText: String? = null) {
        coroutineScope.launch {
            if (progressBarDialog != null) {
                if (!progressBarDialog!!.isShowing) {
                    progressBarDialog = ProgressBarDialog(this@BaseActivity, progressText)
                    progressBarDialog!!.show()
                }
            } else {
                progressBarDialog = ProgressBarDialog(this@BaseActivity, progressText)
                progressBarDialog!!.show()
            }
        }
    }

    fun hideProgress() {
        coroutineScope.launch {
            if (progressBarDialog != null) {
                progressBarDialog!!.dismiss()
            }
        }
    }

    fun isCurrentThemeDark(): Boolean {
        return preference.isThemeDark == ThemeDarkEnum.THEME_NIGHT.name
    }

    override fun onDestroy() {
        super.onDestroy()
        hideProgress()
    }

}