package uz.fido.universaldigital.ui.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.profile.LogOutRequest
import uz.fido.universaldigital.BuildConfig
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMenuProfileBinding
import uz.fido.universaldigital.ui.dialogs.LogOutDialog
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.logOut
import uz.fido.universaldigital.ui.fragments.login.pin.PassCodeDialogFragment
import uz.fido.universaldigital.ui.fragments.profile.settings.NewDesignOnboardingPage
import uz.fido.universaldigital.ui.utils.extensions.isUserIdentified
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.const.Const
import uz.fido.utils.device.GetDeviceInfo
import uz.fido.utils.security.getFromSecureStore
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientToken

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
        if (getFromSecureStore(Const.PAPER_CLIENT_FULL_NAME).isNotEmpty() && getFromSecureStore(Const.PAPER_CLIENT_FULL_NAME).isNotBlank()) {
            binding.userName.text = getFromSecureStore(Const.PAPER_CLIENT_FULL_NAME, getString(R.string.your_phone_number))
        } else {
            binding.userName.text = getString(R.string.your_phone_number)
        }
        binding.userPhone.text = Format.phoneFormat(getFromSecureStore(Const.PAPER_CLIENT_PHONE))
        binding.version.text = getString(R.string.version, BuildConfig.VERSION_NAME) + "(${BuildConfig.VERSION_CODE})" + if (BuildConfig.DEBUG) "-DEBUG" else ""
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        binding.appMode.text = if (requireContext().getFromSecureStore(Const.NEW_DESIGN, "N") == "Y") "Pro" else "Lite"
    }

    private fun initUserIdentifyStatus() {
        if (isUserIdentified() || isUserIdentified) {
            binding.tvIdentifiedClient.visibility = View.VISIBLE
            binding.verifiedIcon.visibility = View.VISIBLE
            binding.gotoIdentification.visibility = View.GONE
            binding.noIdentificationDesc.visibility = View.GONE
        } else {
            binding.motionLayout.definedTransitions.forEach {
                it.isEnabled = false
            }
            binding.tvIdentifiedClient.visibility = View.GONE
            binding.verifiedIcon.visibility = View.GONE
            binding.gotoIdentification.visibility = View.VISIBLE
            binding.noIdentificationDesc.visibility = View.VISIBLE
        }
    }

    private fun initSetOnClickListeners() {
        with(binding) {
            appBar.setOnBackButtonClickListener { pop() }
            gotoIdentification.setOnClickListener { gotoWithSlide(R.id.mainIdentificationFragment2) }
            tvIdentifiedClient.setOnClickListener { gotoWithSlide(R.id.mainIdentificationFragment2) }
            security.setOnClickListener {
                PassCodeDialogFragment {
                    gotoWithSlide(R.id.securityFragment)
                }.show(childFragmentManager, "")
            }
            settings.setOnClickListener {
                val navController=  requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.findNavController()
                navController?.navigate(R.id.settingsFragment)

                /*gotoWithSlide(R.id.settingsFragment)*/ }
            aboutBank.setOnClickListener { gotoWithSlide(R.id.aboutBankFragment) }
            logOut.setOnClickListener { showLogOutDialog() }
            profile.setOnClickListener { gotoWithSlide(R.id.myDetailsFragment) }
            cheques.setOnClickListener { gotoWithSlide(R.id.savedChequesFragment) }
            newDesignState.setOnClickListener { NewDesignOnboardingPage().show(childFragmentManager, "") }
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

    private fun logOutRequest() {
        showProgress()
        val device = GetDeviceInfo(context = requireContext()).deviceInfo
        viewModel.logOutRequest(
            token = getClientToken(),
            logOutRequest = LogOutRequest(
                device_code = requireContext().getDeviceIds(),
                device_type = "A",
                fcm_token = requireContext().getFromSecureStore(Const.PAPER_FCM_TOKEN),
                phone_number = getFromSecureStore(Const.PAPER_CLIENT_PHONE),
                sim_iccd = device.simCcd,
                network_state = device.networkState,
                imei_data = device.imeiData,
                os_system_version_api = device.osSystemVersionApi,
                client_id = getClientId()
            )
        ).observe(viewLifecycleOwner) {
            hideProgress()
            requireActivity().logOut()
        }
    }

    private fun showLogOutDialog() {
        LogOutDialog { logOutRequest() }.show(childFragmentManager, "")
    }

}