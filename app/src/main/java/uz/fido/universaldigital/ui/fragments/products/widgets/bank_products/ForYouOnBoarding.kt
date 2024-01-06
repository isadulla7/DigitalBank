package uz.fido.universaldigital.ui.fragments.products.widgets.bank_products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.databinding.FragmentAppFunctionsBinding
import uz.fido.utils.R
import uz.fido.utils.log.Log

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
        val storage = FirebaseStorage.getInstance()

// Create a storage reference pointing to your video
        val storageRef =
            storage.reference.child("media/service_target_mini.mp4") // Replace with your video path

// Get the download URL for the video
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            // Use the URL to load the video into VideoView
            // Replace with your VideoView ID
            binding.videoView.setVideoURI(uri)
            binding.videoView.start() // Start playing the video
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.e("Firebase", "Failed to get download URL: ${exception.message}")
        }
    }

    private fun initStoriesData() {

    }

}