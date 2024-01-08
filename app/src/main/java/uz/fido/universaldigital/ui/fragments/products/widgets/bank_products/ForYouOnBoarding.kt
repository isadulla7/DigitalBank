package uz.fido.universaldigital.ui.fragments.products.widgets.bank_products

import android.net.Uri
import android.os.Bundle
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
        initStoriesData()
        initSetOnClickListeners()
    }

    private fun initSetOnClickListeners() {
//        binding.close.setOnClickListener { dismiss() }
    }


    private fun initStoriesView() {
        val uri =
            "android.resource://" + requireContext().packageName + "/" + uz.fido.universaldigital.R.raw.service_conversion_mini
        binding.videoView.setVideoURI(Uri.parse(uri))
        binding.videoView.setOnCompletionListener {

        }
        binding.videoView.start()
    }

    private fun initStoriesData() {

    }

}