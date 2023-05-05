package uz.fido.universaldigital.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uz.fido.utils.utility.activity.adjustFontScale
import uz.fido.utils.utility.theme.PreferencesImpl
import uz.fido.utils.utility.theme.ThemeDarkEnum
import uz.fido.utils.view.progress_bar.ProgressBarDialog
import javax.inject.Singleton

abstract class BaseActivity : AppCompatActivity() {

    private val job = Job()
    private var progressBarDialog: ProgressBarDialog? = null
    private val coroutineScope = CoroutineScope(job + Dispatchers.Main)
    private val preference by lazy { PreferencesImpl.instance(this) }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
    }

    override fun onStart() {
        super.onStart()
        this.adjustFontScale(resources.configuration)
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