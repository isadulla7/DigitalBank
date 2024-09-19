package uz.fido.universaldigital.ui.fragments.profile.user_details

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import coil.load
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.edit_user.EditUserInfo
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentEditProfileBinding
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.universaldigital.ui.fragments.profile.edit_photo.EditPhotoActivity
import uz.fido.universaldigital.ui.utils.extensions.fixQuestionMarks
import uz.fido.universaldigital.ui.utils.extensions.getFromPaper
import uz.fido.universaldigital.ui.utils.extensions.saveToPaper
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken
import java.util.Random

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, MenuProfileViewModel>(FragmentEditProfileBinding::inflate, MenuProfileViewModel::class.java) {

    private var storageReference: StorageReference? = null
    private var storage: FirebaseStorage? = null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initUserDetails()
        initSetOnClickListeners()
    }

    private fun initUserDetails() {
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        loadProfileImage()
        binding.apply {
            userName.setText(getFromPaper(Const.FIRST_NAME, getString(R.string.unknown)).fixQuestionMarks())
            surname.setText(getFromPaper(Const.LAST_NAME, getString(R.string.unknown)).fixQuestionMarks())
            mail.setText(getFromPaper(Const.EMAIL, getString(R.string.unknown)))
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.profileImage.setOnClickListener { requestPermissionForImages() }
        binding.saveButton.setOnClickListener { if (canEditProfile()) editProfile() else toast(getString(R.string.fill_the_gaps)) }
    }

    private fun canEditProfile(): Boolean {
        return binding.userName.text.toString().isNotEmpty() && binding.surname.text.toString().isNotEmpty()
    }

    private fun editProfile() {
        showProgress()
        viewModel.editUserInfo(
            getClientToken(), EditUserInfo(
                name = binding.userName.text.toString().uppercase(),
                surname = binding.surname.text.toString().uppercase(),
                email = binding.mail.text.toString().lowercase()
            )
        ).observe(viewLifecycleOwner) {
            hideProgress()
            if (it.status == Status.ERROR) {
                showSnackbar(it.message.toString())
            } else {
                saveToPaper(Const.FIRST_NAME, binding.userName.text.toString().uppercase())
                saveToPaper(Const.LAST_NAME, binding.surname.text.toString().uppercase())
                saveToPaper(Const.PAPER_CLIENT_FULL_NAME, binding.userName.text.toString().uppercase() + " " + binding.surname.text.toString().uppercase())
                saveToPaper(Const.EMAIL, binding.mail.text.toString().lowercase())
                pop()
            }
        }
    }

    private fun loadProfileImage() {
        if (getFromPaper(Const.PAPER_USER_PHOTO_PATH).isNotEmpty()) {
            Picasso.get()
                .load(getFromPaper(Const.PAPER_USER_PHOTO_PATH))
                .placeholder(R.drawable.ic_profile_image_empty)
                .error(R.drawable.ic_profile_image_empty)
                .into(binding.profileImage)
        } else {
            binding.profileImage.load(R.drawable.ic_profile_image_empty)
        }
    }

    private fun openEditPhotoActivity(path: String) {
        val intent = Intent(requireActivity(), EditPhotoActivity::class.java)
        intent.putExtra("uri", path)
        editPhotoIntent.launch(intent)
    }

    private fun requestPermissionForImages() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply { type = "image/*" }
        openGalleryIntent.launch(intent)
    }

    private fun uploadImageToFirebase(filePath: Uri) {
        val photoId = "profile_photo_${getClientId()}_${(Random().nextInt(99999 - 10000) + 10000)}"
        binding.progressBar.visibility = View.VISIBLE
        binding.profileImage.alpha = 0.8f
        val ref = storageReference!!.child("images/$photoId")
        ref.putFile(filePath).addOnSuccessListener {
            setProfilePhotoId(photoId)
            binding.progressBar.visibility = View.GONE
            binding.profileImage.alpha = 1f
        }.addOnFailureListener { e ->
            binding.progressBar.visibility = View.GONE
            binding.profileImage.alpha = 1f
            showSnackbar(e.localizedMessage.toString())
        }
    }

    private fun setProfilePhotoId(id: String) {
        if (view != null) {
            showProgress()
            viewModel.editUserInfo(getClientToken(), EditUserInfo(user_avatar = id)).observe(viewLifecycleOwner) {
                hideProgress()
                if (it.status == Status.ERROR) {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { approved ->
        if (approved) {
            pickImage()
        }
    }

    private val editPhotoIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val path = it.data?.getStringExtra(EditPhotoActivity.RESULT_IMAGE)
            saveToPaper(Const.PAPER_USER_PHOTO_PATH, path)
            Picasso.get().load(path).into(binding.profileImage)
            uploadImageToFirebase(path!!.toUri())
        }
    }

    private val openGalleryIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
            val path = it.data?.data.toString()
            openEditPhotoActivity(path)
        } else {
            toast(it.toString())
        }
    }

}