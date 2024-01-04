package uz.fido.universaldigital.base

import BaseInfoDialog
import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.sms.CheckSmsForPayment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.activities.VpnErrorActivity
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsViewModel
import uz.fido.utils.app.AppSignatureHelper
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.const.ServerMessages.ERROR_CODE_VPN
import uz.fido.utils.const.ServerMessages.getMeaningFulMessage
import uz.fido.utils.log.Log.d
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.user.getClientToken

abstract class BaseSimpleFragment<VB : ViewBinding>(
    inflate: Inflate<VB>,
) : SimpleAbstractFragment<VB>(inflate) {

    val currentActivity get() = activity as BaseActivity
    private val smsViewModel: ConfirmSmsViewModel by viewModels()
    private var permissionInterface: PermissionInterface? = null
    private var alertDialog: AlertDialog? = null

    fun showProgress(progressText: String? = null) =
        (activity as BaseActivity).showProgress(progressText)

    fun hideProgress() = (activity as BaseActivity).hideProgress()

    fun isCurrentThemeDark(): Boolean {
        return (activity as BaseActivity).isCurrentThemeDark()
    }

    fun functionInProgress() {
        showSnackbar(getString(R.string.service_under_development), title = getString(R.string.info))
    }

    fun showSnackbar(
        snackbarText: String,
        title: String? = null,
        buttonText: String? = null,
        okClickListener: (() -> Unit)? = null
    ) {
        hideProgress()
        var message = snackbarText
        if (message == ERROR_CODE_VPN) {
            openVpnErrorActivity()
        } else {
            message = getMeaningFulMessage(message)
            if (message.isNotEmpty() && view != null) {
                showBaseInfoDialog(title, buttonText, message, okClickListener)
            }
        }
    }

    private fun showBaseInfoDialog(
        title: String?, buttonText: String?, message: String, okClickListener: (() -> Unit)?
    ) {
        val dialog =
            BaseInfoDialog(title ?: getString(R.string.error), message, buttonText, okClickListener)
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
        val builder = AlertDialog.Builder(requireContext(), R.style.MyDialogTheme)
        builder.setTitle(getString(R.string.permission))
        builder.setMessage(description)
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.enter_settings)) { _: DialogInterface?, _: Int ->
            run {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", activity?.packageName, null)
                startActivity(intent)
                alertDialog?.dismiss()
            }
        }
        builder.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->
            run {
                alertDialog?.dismiss()
            }
        }
        alertDialog = builder.create()
        alertDialog?.show()
        alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)!!
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.brandBlueColor))
        alertDialog?.getButton(AlertDialog.BUTTON_NEGATIVE)!!
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.brandRedColor))
    }

    private val locationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                if (!it.value) {
                    openSettingsPage(getString(R.string.location_permission_description))
                    return@registerForActivityResult
                }
            }
            permissionInterface?.locationPermissionGranted()
        }

    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) {
                openSettingsPage(getString(R.string.camera_permission_description))
                return@registerForActivityResult
            }
            permissionInterface?.cameraPermissionGranted()
        }

    private val contactsPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
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
        val cameraStorage =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
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
        val writeContact =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS)
        val readContact =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
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

    protected fun checkForSms(
        card: CardResponse, amount: String, serviceId: String, listener: (String, String) -> Unit
    ) {
        val model = CheckSmsForPayment(
            app_key_hash = AppSignatureHelper(requireContext()).appKeyHash,
            from_object_id = card.object_id,
            amount = amount,
            service_id = serviceId,
            device_code = requireContext().getDeviceIds()
        )
        smsViewModel.checkForSmsPaymentRequest(getClientToken(), model)
            .observe(viewLifecycleOwner) {
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

}