package uz.fido.universaldigital.ui.fragments.profile.user_details

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
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
import uz.fido.universaldigital.ui.utils.extensions.recordException
import uz.fido.utils.const.Const
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.security.saveToSecureStore
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken
import java.security.SecureRandom

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, MenuProfileViewModel>(FragmentEditProfileBinding::inflate, MenuProfileViewModel::class.java) {

    private lateinit var storageReference: StorageReference
    private lateinit var storage: FirebaseStorage
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            openEditPhotoActivity(uri.toString())
        } else {
            //error
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initUserDetails()
        initSetOnClickListeners()
    }

    private fun initUserDetails() {
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        loadProfileImage()
        binding.apply {
            userName.setText(getFromSecureStore(Const.FIRST_NAME, getString(R.string.unknown)).fixQuestionMarks())
            surname.setText(getFromSecureStore(Const.LAST_NAME, getString(R.string.unknown)).fixQuestionMarks())
            mail.setText(getFromSecureStore(Const.EMAIL, getString(R.string.unknown)))
        }
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.profileImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
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
                saveToSecureStore(Const.FIRST_NAME, binding.userName.text.toString().uppercase())
                saveToSecureStore(Const.LAST_NAME, binding.surname.text.toString().uppercase())
                saveToSecureStore(Const.PAPER_CLIENT_FULL_NAME, binding.userName.text.toString().uppercase() + " " + binding.surname.text.toString().uppercase())
                saveToSecureStore(Const.EMAIL, binding.mail.text.toString().lowercase())
                pop()
            }
        }
    }

    private fun loadProfileImage() {
        if (getFromSecureStore(Const.PAPER_USER_PHOTO_PATH).isNotEmpty()) {
            Picasso.get()
                .load(getFromSecureStore(Const.PAPER_USER_PHOTO_PATH))
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

    private fun uploadImageToFirebase(filePath: Uri) {
        try {
            if (!this.isVisible) return
            val photoId = "profile_photo_${getClientId()}_${(SecureRandom().nextInt(99999 - 10000) + 10000)}"
            binding.progressBar.visibility = View.VISIBLE
            binding.saveButton.isEnabled = false
            binding.profileImage.alpha = 0.8f
            val ref = storageReference.child("images/$photoId")
            ref.putFile(filePath).addOnSuccessListener {
                if (this.isVisible) {
                    setProfilePhotoId(photoId)
                    binding.progressBar.visibility = View.GONE
                    binding.saveButton.isEnabled = true
                    binding.profileImage.alpha = 1f
                }
            }.addOnFailureListener { e ->
                if (this.isVisible) {
                    binding.progressBar.visibility = View.GONE
                    binding.saveButton.isEnabled = true
                    binding.profileImage.alpha = 1f
                }
                showSnackbar(e.localizedMessage?.toString() ?: "")
            }
        } catch (e: Exception) {
            recordException(e, ::uploadImageToFirebase.name)
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

    private val editPhotoIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val path = it.data?.getStringExtra(EditPhotoActivity.RESULT_IMAGE)
            saveToSecureStore(Const.PAPER_USER_PHOTO_PATH, path)
            Picasso.get().load(path).into(binding.profileImage)
            uploadImageToFirebase(path!!.toUri())
        }
    }

}