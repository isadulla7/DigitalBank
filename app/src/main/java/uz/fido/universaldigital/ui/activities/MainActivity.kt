package uz.fido.universaldigital.ui.activities

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityMainBinding
import uz.fido.utils.internet_checker.InternetConnectionChecker
import uz.fido.utils.internet_checker.NoConnectionDialog
import uz.fido.utils.update_checker.UpdateChecker
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var updateChecker: UpdateChecker

    private var noConnectionDialog: NoConnectionDialog? = null
    private var pausedMillis: Long = 0L
    private var isStop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkUpdate()
    }

    override fun onResume() {
        super.onResume()
        internetListener()
        isStop = false
    }

    override fun onStop() {
        super.onStop()
        pausedMillis = Calendar.getInstance().timeInMillis
        isStop = true
    }

    private fun checkUpdate() {
        updateChecker = UpdateChecker(this)
        updateChecker.checkUpdate()
    }

    private fun internetListener() {
        InternetConnectionChecker(this).observeForever { isConnected ->
            if (isConnected) {
                if (!isDestroyed && !isFinishing) {
                    if (noConnectionDialog != null) {
                        noConnectionDialog?.dismiss()
                        noConnectionDialog = null
                    }
                }
            } else if (!this@MainActivity.isStop && !isDestroyed && !isFinishing) {
                noConnectionDialog = NoConnectionDialog()
                noConnectionDialog?.show(supportFragmentManager, "")
            }
        }
    }
}