package uz.fido.universaldigital.ui.fragments.products.widgets.bank_products

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.databinding.FragmentBankProductOnboardingBinding
import uz.fido.universaldigital.ui.utils.extensions.bankProductsWithLargeImage
import uz.fido.universaldigital.ui.utils.extensions.getDrawable
import uz.fido.utils.R

@AndroidEntryPoint
class BankProductsOnBoarding(private var currentItem: Int) : DialogFragment() {

    private lateinit var binding: FragmentBankProductOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBankProductOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStoriesView()
        initStoriesData()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.close.setOnClickListener { dismiss() }
    }


    private fun initStoriesView() {
        val countDownTimer = object : CountDownTimer(7000L, 100) {
            override fun onFinish() {
                cancel()
                dismiss()
            }

            override fun onTick(p0: Long) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.storiesWheel.setProgress(((7000L - p0) / 70L).toInt(), true)
                } else {
                    binding.storiesWheel.progress = ((7000L - p0) / 70L).toInt()
                }
            }
        }
        countDownTimer.start()
    }

    private fun initStoriesData() {
        val item = bankProductsWithLargeImage(requireContext())[currentItem]
        binding.tvProductName.text = item.name
        binding.tvProductDesc.text = item.description
        binding.icIllustration.load(requireContext().getDrawable(item.icon))
    }

}