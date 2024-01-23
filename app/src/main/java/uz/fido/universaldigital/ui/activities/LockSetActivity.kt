package uz.fido.universaldigital.ui.activities

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseActivity
import uz.fido.universaldigital.databinding.ActivityLockSetBinding

@AndroidEntryPoint
class LockSetActivity : BaseActivity() {

    private lateinit var binding: ActivityLockSetBinding
    private var onBackPress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDetails()
    }

    private fun initDetails() {
        binding.tvTitle.text = getString(R.string.data_security_function)
        binding.tvDescription.text = getString(R.string.emulator_is_not_allowed)
        binding.buttonOpenSettings.text = getString(R.string.exit)
        binding.buttonOpenSettings.setOnClickListener {
            finishAffinity()
        }
    }



    override fun onBackPressed() {
        if (onBackPress) {
            super.onBackPressed()
        }
    }

}