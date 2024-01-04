package uz.fido.utils.view.progress_bar

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import uz.fido.utils.R
import uz.fido.utils.databinding.DialogProgressBinding

class ProgressBarDialog(context: Context, private val progressText: String?) :
    Dialog(context, R.style.ProgressDialogTheme) {

    private lateinit var binding: DialogProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!progressText.isNullOrBlank()) {
            binding.progressText.text = progressText
            binding.progressText.visibility = View.VISIBLE
        }
        this.setCancelable(true)
        this.setCanceledOnTouchOutside(true)
    }

    fun setMessage(s: String) {
        binding.progressText.text = s
    }
}