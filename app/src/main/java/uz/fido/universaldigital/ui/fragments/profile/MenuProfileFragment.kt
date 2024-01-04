package uz.fido.universaldigital.ui.fragments.profile

import LogOutDialog
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
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Paper
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.edit_user.EditUserInfo
import uz.fido.network.domain.model.profile.LogOutRequest
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMenuProfileBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.logOut
import uz.fido.universaldigital.ui.fragments.profile.edit_photo.EditPhotoActivity
import uz.fido.universaldigital.ui.utils.extensions.isUserIdentified
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.const.Const
import uz.fido.utils.device.GetDeviceInfo
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken
import java.util.Random

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class MenuProfileFragment : BaseFragment<FragmentMenuProfileBinding, MenuProfileViewModel>(
    FragmentMenuProfileBinding::inflate, MenuProfileViewModel::class.java
), PermissionInterface {

    private var storageReference: StorageReference? = null
    private var storage: FirebaseStorage? = null
    private var isUserIdentified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isUserIdentified = it.getBoolean(Const.USER_IDENTIFIED, false)
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initSetOnClickListeners()
        initUserIdentifyStatus()
        initDetails()
    }

    private fun initDetails() {
        loadProfileImage()
        if (Paper.book().read(Const.PAPER_CLIENT_FULL_NAME, "").isNotEmpty() &&
            Paper.book().read(Const.PAPER_CLIENT_NAME, "").isNotEmpty() &&
            Paper.book().read(Const.PAPER_CLIENT_SURNAME, "").isNotEmpty()
        ) {
            binding.userName.text = Paper.book().read(Const.PAPER_CLIENT_FULL_NAME, "")
            binding.tvShortName.text =
                (Paper.book().read(Const.PAPER_CLIENT_NAME, "").first().toString() + Paper.book()
                    .read(Const.PAPER_CLIENT_SURNAME, "").first().toString())
        } else {
            binding.userName.text = getString(R.string.your_phone_number)
        }
        binding.userPhone.text = Format.phoneFormat(Paper.book().read(Const.PAPER_CLIENT_PHONE, ""))
        binding.version.text = getString(R.string.version, BuildConfig.VERSION_NAME)
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
    }

    private fun initUserIdentifyStatus() {
        if (isUserIdentified() || isUserIdentified) {
            binding.tvIdentifiedClient.visibility = View.VISIBLE
            binding.gotoIdentification.visibility = View.GONE
            binding.noIdentificationDesc.visibility = View.GONE
        } else {
            binding.tvIdentifiedClient.visibility = View.GONE
            binding.gotoIdentification.visibility = View.VISIBLE
            binding.noIdentificationDesc.visibility = View.VISIBLE
        }
    }

    private fun initSetOnClickListeners() {
        binding.apply {
            appBar.setOnBackButtonClickListener { pop() }
            gotoIdentification.setOnClickListener { gotoWithSlide(R.id.mainIdentificationFragment2) }
            tvIdentifiedClient.setOnClickListener { gotoWithSlide(R.id.mainIdentificationFragment2) }
            security.setOnClickListener { gotoWithSlide(R.id.securityFragment) }
            settings.setOnClickListener { gotoWithSlide(R.id.settingsFragment) }
            aboutBank.setOnClickListener { gotoWithSlide(R.id.aboutBankFragment) }
            appBar.setOnAdditionalBtnClickListener { showLogOutDialog() }
            profile.setOnClickListener { gotoWithSlide(R.id.myDetailsFragment) }
            profileImage.setOnClickListener { requestPermissionForImages() }
        }
    }

    private fun loadProfileImage() {
        if (Paper.book().read(Const.PAPER_USER_PHOTO_PATH, "").isNotEmpty()) {
            Glide.with(requireContext()).load(Paper.book().read(Const.PAPER_USER_PHOTO_PATH) ?: "")
                .error(R.drawable.ic_profile_image_empty)
                .into(binding.profileImage)
        }
    }

    private fun logOutRequest() {
        showProgress()
        val device = GetDeviceInfo(context = requireContext()).deviceInfo
        viewModel.logOutRequest(
            token = getClientToken(), logOutRequest = LogOutRequest(
                device_code = requireContext().getDeviceIds(),
                device_type = "A",
                fcm_token = Paper.book().read(Const.PAPER_FCM_TOKEN) ?: "",
                phone_number = Paper.book().read(Const.PAPER_CLIENT_PHONE),
                sim_iccd = device.sim_iccd,
                network_state = device.network_state,
                imei_data = device.imei_data,
                os_system_version_api = device.os_system_version_api,
                client_id = getClientId()
            )
        ).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    requireActivity().logOut()
                }

                else -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun showLogOutDialog() {
        LogOutDialog {
            logOutRequest()
        }.show(childFragmentManager, "")
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
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
            }
        openGalleryIntent.launch(intent)
    }

    private fun uploadImageToFirebase(filePath: Uri) {
        val photoId = "profile_photo_${getClientId()}_${(Random().nextInt(99999 - 10000) + 10000)}"
        binding.progressBar.visibility = View.VISIBLE
        binding.profileImage.alpha = 0.5f
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
            viewModel.editUserInfo(getClientToken(), EditUserInfo(user_avatar = id))
                .observe(viewLifecycleOwner) {
                    hideProgress()
                    if (it.status == Status.ERROR) {
                        showSnackbar(it.message.toString())
                    }
                }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            pickImage()
        }
    }

    private val editPhotoIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val path = it.data?.getStringExtra(EditPhotoActivity.RESULT_IMAGE)
                Paper.book().write(Const.PAPER_USER_PHOTO_PATH, path)
                Picasso.get().load(path).into(binding.profileImage)
                uploadImageToFirebase(path!!.toUri())
            }
        }

    private val openGalleryIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data?.data != null) {
                val path = it.data?.data.toString()
                openEditPhotoActivity(path)
            }
        }

}