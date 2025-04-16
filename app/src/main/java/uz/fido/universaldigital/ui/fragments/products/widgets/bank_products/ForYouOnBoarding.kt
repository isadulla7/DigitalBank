package uz.fido.universaldigital.ui.fragments.products.widgets.bank_products

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.databinding.FragmentAppFunctionsBinding
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.utils.R

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
                    "android.resource://" + requireContext().packageName + "/" + uz.fido.universaldigital.R.raw.video_service_transfer
                initStoriesTimer(10000L)
                setDetails(
                    requireContext().getString(uz.fido.universaldigital.R.string.for_you_p2p_title),
                    requireContext().getString(
                        uz.fido.universaldigital.R.string.for_you_p2p_description
                    )
                )
            }

            2 -> {
                uri =
                    "android.resource://" + requireContext().packageName + "/" + uz.fido.universaldigital.R.raw.video_service_conversion
                initStoriesTimer(20000L)
                setDetails(
                    requireContext().getString(uz.fido.universaldigital.R.string.for_you_conversion_title),
                    requireContext().getString(
                        uz.fido.universaldigital.R.string.for_you_conversion_description
                    )
                )
            }

            3 -> {
                uri =
                    "android.resource://" + requireContext().packageName + "/" + uz.fido.universaldigital.R.raw.video_service_target
                initStoriesTimer(15000L)
                setDetails(
                    requireContext().getString(uz.fido.universaldigital.R.string.for_you_target_title),
                    requireContext().getString(
                        uz.fido.universaldigital.R.string.for_you_target_description
                    )
                )
            }
        }
        binding.videoView.setVideoURI(uri.toUri())
        binding.videoView.start()
    }

    private fun initStoriesTimer(length: Long) {
        try {
            val countDownTimer = object : CountDownTimer(length, 100) {
                override fun onFinish() {
//                    if (isAdded) {
                    cancel()
                    dismissAllowingStateLoss()
//                    }
                }

                override fun onTick(p0: Long) {
                    binding.storiesWheel.setProgress(((length - p0) / (length / 100)).toInt(), true)
                }
            }
            countDownTimer.start()
        } catch (e: Exception) {
            recordException(e, ::initStoriesTimer.name)
        }
    }

    private fun setDetails(title: String, description: String) {
        binding.serviceTitle.text = title
        binding.serviceDescription.text = description
    }

    override fun onPause() {
        super.onPause()
        binding.videoView.pause()
    }

}