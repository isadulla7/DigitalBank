package uz.fido.universaldigital.base

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Annotation
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.sms.CheckSmsForPayment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.activities.security.VpnErrorActivity
import uz.fido.universaldigital.ui.dialogs.BaseInfoDialog
import uz.fido.universaldigital.ui.dialogs.OpenSettingsDialog
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsViewModel
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.extensions.logOut
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.const.ServerMessages.ERROR_CODE_VPN
import uz.fido.utils.const.ServerMessages.LOG_OUT
import uz.fido.utils.const.ServerMessages.NEED_IDENTIFIED
import uz.fido.utils.const.ServerMessages.getMeaningFulMessage
import uz.fido.utils.log.Log.d
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.user.getClientToken

abstract class BaseFragment<VB : ViewBinding, VM : AbstractViewModel>(
    inflate: Inflate<VB>, viewModelClass: Class<VM>
) : AbstractFragment<VB, VM>(inflate, viewModelClass) {

    protected val viewModel: VM get() = abstractViewModel
    private val smsViewModel: ConfirmSmsViewModel by viewModels()
    private var permissionInterface: PermissionInterface? = null
    private var alertDialog: AlertDialog? = null

    fun functionInProgress() {
        showSnackbar(
            getString(R.string.service_under_development), title = getString(R.string.info)
        )
    }

    fun showProgress(progressText: String? = null) = (activity as BaseActivity).showProgress(progressText)

    fun hideProgress() = (activity as BaseActivity).hideProgress()

    fun isInternetConnected(context: Context): Boolean {
        var result: Boolean

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = if (connectivityManager.activeNetwork != null) {
            connectivityManager.activeNetwork
        } else {
            result = false
            null
        }
        result = if (connectivityManager.getNetworkCapabilities(networkCapabilities) != null) {
            val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities)!!
            when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            false
        }
        if (!isDetached && !result) {
            showSnackbar(getString(R.string.error_no_internet_connection))
        }
        return result
    }

    fun showSnackbar(
        snackbarText: String,
        title: String? = null,
        buttonText: String? = null,
        onClickListener: (() -> Unit)? = null,
    ) {
        hideProgress()
        when (snackbarText) {
            ERROR_CODE_VPN -> openVpnErrorActivity() // Open VPN error activity directly
            NEED_IDENTIFIED -> openIdentifyFragment(snackbarText, title, buttonText, onClickListener) // Open identify fragment
            LOG_OUT -> handleLogoutMessage(title, buttonText, snackbarText)
            else -> handleGeneralMessage(title, buttonText, snackbarText, onClickListener)
        }
    }

    private fun handleLogoutMessage(
        title: String?,
        buttonText: String?,
        message: String
    ) {
        val meaningfulMessage = getMeaningFulMessage(message)
        if (meaningfulMessage.isNotEmpty() && view != null) {
            showBaseInfoDialog(title, buttonText, meaningfulMessage) {
                requireActivity().logOut()
            }
        }
    }

    private fun handleGeneralMessage(
        title: String?,
        buttonText: String?,
        message: String,
        onClickListener: (() -> Unit)?
    ) {
        val meaningfulMessage = getMeaningFulMessage(message)
        if (meaningfulMessage.isNotEmpty() && view != null) {
            showBaseInfoDialog(title, buttonText, meaningfulMessage, onClickListener)
        }
    }

    private fun openIdentifyFragment(
        snackbarText: String,
        title: String? = null,
        buttonText: String? = null,
        onClickListener: (() -> Unit)? = null,
    ) {
        try {
            gotoWithSlide(R.id.mainIdentificationFragment2, bundleOf("need_identification" to true))
        } catch (e: Exception) {
            showBaseInfoDialog(title, buttonText, snackbarText, onClickListener)
        }
    }

    private fun showBaseInfoDialog(
        title: String?, buttonText: String?, message: String, onClickListener: (() -> Unit)? = null
    ) {
        val dialog = BaseInfoDialog(title ?: getString(R.string.error), message, buttonText, onClickListener)
        dialog.show(childFragmentManager, "")
    }

    private fun openVpnErrorActivity() {
        startActivity(Intent(requireActivity(), VpnErrorActivity::class.java))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.d("onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        context?.d("onStart")
    }

    override fun onPause() {
        super.onPause()
        context?.d("onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context?.d("onDestroyView")
    }

    private fun openSettingsPage(description: String) {
        OpenSettingsDialog(description) {
            run {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", activity?.packageName, null)
                startActivity(intent)
                alertDialog?.dismiss()
            }
        }.show(childFragmentManager, "")
    }

    private val locationPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            if (!it.value) {
                openSettingsPage(getString(R.string.location_permission_description))
                return@registerForActivityResult
            }
        }
        permissionInterface?.locationPermissionGranted()
    }

    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (!it) {
            openSettingsPage(getString(R.string.camera_permission_description))
            return@registerForActivityResult
        }
        permissionInterface?.cameraPermissionGranted()
    }

    private val contactsPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            if (!it.value) {
                openSettingsPage(getString(R.string.contact_permission_description))
                return@registerForActivityResult
            }
        }
        permissionInterface?.contactsPermissionGranted()
    }

    fun checkForLocationPermissions(
        permissionInterface: PermissionInterface, isSignIn: Boolean? = false
    ): Boolean {
        if (context != null) {
            val listPermissionsNeeded = ArrayList<String>()
            val fineLocation = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            )
            val coarseLocation = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (fineLocation != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (listPermissionsNeeded.isNotEmpty() && isSignIn == false) {
                this.permissionInterface = permissionInterface
                locationPermission.launch(listPermissionsNeeded.toTypedArray())
                return false
            }
            return true
        } else return false
    }

    fun checkForCameraPermission(permissionInterface: PermissionInterface): Boolean {
        val listPermissionsNeeded = ArrayList<String>()
        val cameraStorage = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        if (cameraStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            this.permissionInterface = permissionInterface
            cameraPermission.launch(listPermissionsNeeded[0])
            return false
        }
        return true
    }

    fun checkForContactsPermission(permissionInterface: PermissionInterface?): Boolean {
        val listPermissionsNeeded = ArrayList<String>()
        val writeContact = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS)
        val readContact = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
        if (writeContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_CONTACTS)
        }
        if (readContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            this.permissionInterface = permissionInterface
            contactsPermission.launch(listPermissionsNeeded.toTypedArray())
            return false
        }
        return true
    }

    protected fun checkForSms(
        card: CardResponse, amount: String, serviceId: String, listener: (String, String) -> Unit
    ) {
        val model = CheckSmsForPayment(
            app_key_hash = AppSignatureHelper(requireContext()).appKeyHash, from_object_id = card.object_id, amount = amount, service_id = serviceId, device_code = requireContext().getDeviceIds()
        )
        smsViewModel.checkForSmsPaymentRequest(getClientToken(), model).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    listener.invoke(
                        it.data?.is_sms_confirm.toString(), it.data?.string_line.toString()
                    )
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    companion object {

        @JvmStatic
        protected fun checkForPaymentSms(
            card: CardResponse, smsControlLimit: String, amount: String
        ): Boolean {
            return if (card.safe_mode == "Y" || card.pay_with_sms == "Y") {
                true
            } else smsControlLimit != "-1" && Format.convertFromStringToBigDecimal(amount) > Format.convertFromStringToBigDecimal(
                smsControlLimit
            )
        }
    }

    fun SpannableString.setSpans(
        it: Annotation, clickableSpan: ClickableSpan, fullText: SpannedString, context: Context
    ) {
        this.apply {
            setSpan(
                clickableSpan, fullText.getSpanStart(it), fullText.getSpanEnd(it), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(context, uz.fido.utils.R.color.brandRedColor_50)
                ), fullText.getSpanStart(it), fullText.getSpanEnd(it), 0
            )
            setSpan(
                BackgroundColorSpan(ContextCompat.getColor(context, uz.fido.utils.R.color.white)), fullText.getSpanStart(it), fullText.getSpanEnd(it), 0
            )
            setSpan(UnderlineSpan(), fullText.getSpanStart(it), fullText.getSpanEnd(it), 0)
        }
    }


    fun SpannableString.setSpansForPrivacy(
        it: Annotation, clickableSpan: ClickableSpan, fullText: SpannedString, context: Context
    ) {
        this.apply {
            setSpan(
                clickableSpan, fullText.getSpanStart(it), fullText.getSpanEnd(it), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(context, uz.fido.utils.R.color.brandRedColor)
                ), fullText.getSpanStart(it), fullText.getSpanEnd(it), 0
            )
            setSpan(
                BackgroundColorSpan(ContextCompat.getColor(context, uz.fido.utils.R.color.white)), fullText.getSpanStart(it), fullText.getSpanEnd(it), 0
            )
            setSpan(UnderlineSpan(), fullText.getSpanStart(it), fullText.getSpanEnd(it), 0)
        }
    }

}
