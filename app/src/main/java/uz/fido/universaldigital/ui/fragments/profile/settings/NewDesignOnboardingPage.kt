package uz.fido.universaldigital.ui.fragments.profile.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.FragmentNewDesignOnboardBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.utils.const.Const

@AndroidEntryPoint
class NewDesignOnboardingPage : DialogFragment() {

    private lateinit var binding: FragmentNewDesignOnboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, uz.fido.utils.R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNewDesignOnboardBinding.inflate(inflater, container, false)
        loadImage()
        initOnClickListeners()
        return binding.root
    }

    private fun loadImage() {
        if (getFromPaper(Const.NEW_DESIGN, "N") == "Y") {
            binding.title.setText(R.string.old_design_title)
            binding.description.setText(R.string.old_design_description)
            binding.switchNewDesign.setText(R.string.switch_old_design)
            binding.designImage.load(R.drawable.old_design_s23)
        } else {
            binding.title.setText(R.string.new_design_title)
            binding.description.setText(R.string.new_design_description)
            binding.switchNewDesign.setText(R.string.switch_new_design)
            binding.designImage.load(R.drawable.new_design_s23)
        }
    }

    private fun initOnClickListeners() {
        binding.switchNewDesign.setOnClickListener {
            if (getFromPaper(Const.NEW_DESIGN, "N") == "Y") {
                saveToPaper(Const.NEW_DESIGN, "N")
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra(Const.NEW_DESIGN, false)
                requireActivity().finishAffinity()
                startActivity(intent)
            } else {
                saveToPaper(Const.NEW_DESIGN, "Y")
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra(Const.NEW_DESIGN, true)
                requireActivity().finishAffinity()
                startActivity(intent)
            }
        }
    }

}