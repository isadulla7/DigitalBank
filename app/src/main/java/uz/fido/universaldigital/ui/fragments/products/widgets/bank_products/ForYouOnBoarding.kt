package uz.fido.universaldigital.ui.fragments.products.widgets.bank_products

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.utils.R
import uz.fido.universaldigital.databinding.FragmentAppFunctionsBinding

@AndroidEntryPoint
class ForYouOnBoarding(private var currentItem: Int) : DialogFragment() {

    private lateinit var binding: FragmentAppFunctionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppFunctionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStoriesView()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
        binding.close.setOnClickListener { dismiss() }
    }

    private fun initStoriesView() {
        var uri = ""
        when (currentItem) {
            1 -> {
                uri =
                    "android.resource://" + requireContext().packageName + "/" + uz.fido.universaldigital.R.raw.service_transer_mini
                initStoriesTimer(10000L)
            }

            2 -> {
                uri =
                    "android.resource://" + requireContext().packageName + "/" + uz.fido.universaldigital.R.raw.service_conversion_mini
                initStoriesTimer(20000L)
            }

            3 -> {
                uri =
                    "android.resource://" + requireContext().packageName + "/" + uz.fido.universaldigital.R.raw.service_target_mini
                initStoriesTimer(15000L)
            }
        }
        binding.videoView.setVideoURI(Uri.parse(uri))
        binding.videoView.start()
    }

    private fun initStoriesTimer(length: Long) {
        val countDownTimer = object : CountDownTimer(length, 100) {
            override fun onFinish() {
                cancel()
                dismiss()
            }

            override fun onTick(p0: Long) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.storiesWheel.setProgress(((length - p0) / (length / 100)).toInt(), true)
                } else {
                    binding.storiesWheel.progress = ((length - p0) / (length / 100)).toInt()
                }
            }
        }
        countDownTimer.start()
    }

}