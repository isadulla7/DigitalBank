package uz.fido.utils.update_checker

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import uz.fido.utils.databinding.DialogInAppUpdateBinding

/**
 * Created by Husniddin Muhammad Amin on 17.01.2023
 * Tashkent, Uzbekistan.
 */

class UpdateDownloadedDialog(private var installClickListener: () -> Unit) : DialogFragment() {

    private lateinit var binding: DialogInAppUpdateBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogInAppUpdateBinding.inflate(inflater, container, false)
        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        }
        binding.animationView.setAnimation("restart_anim.json")
        binding.animationView.playAnimation()
        binding.reopen.setOnClickListener {
            installClickListener.invoke()
        }
        binding.cancel.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
}