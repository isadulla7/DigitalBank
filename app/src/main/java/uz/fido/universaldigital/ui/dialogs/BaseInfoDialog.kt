package uz.fido.universaldigital.ui.dialogs

import android.os.Bundle
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseDialogFragment
import uz.fido.universaldigital.databinding.BaseDialogInfoBinding

class BaseInfoDialog(
    private var title: String? = null,
    private var subtitle: String? = null,
    private var buttonText: String? = null,
    private var onClickListener: (() -> Unit)? = null
) : BaseDialogFragment<BaseDialogInfoBinding>(BaseDialogInfoBinding::inflate) {

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        try {
            initDialogDetails()
            initSetOnClickListeners()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initDialogDetails() {
        binding.apply {
            title.text = this@BaseInfoDialog.title ?: getString(R.string.attention)
            subtitle.text = this@BaseInfoDialog.subtitle ?: getString(R.string.unknown)
            buttonText?.let {
                ok.text = it
            }
        }
    }

    private fun initSetOnClickListeners() {
        binding.ok.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.invoke()
            } else {
                dismissAllowingStateLoss()
            }
        }
    }

}