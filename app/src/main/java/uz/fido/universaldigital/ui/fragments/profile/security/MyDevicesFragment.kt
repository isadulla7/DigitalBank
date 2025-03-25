package uz.fido.universaldigital.ui.fragments.profile.security

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.sessions.CheckDeviceRequest
import uz.fido.network.domain.model.sessions.GetUserDevicesRequest
import uz.fido.network.domain.model.sessions.UserDevices
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentMyDevicesBinding
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment
import uz.fido.universaldigital.ui.fragments.login.confirm_sms.ConfirmSmsFragment.Companion.SMS_OPERATION_TERMINATE_SESSION
import uz.fido.utils.const.Const
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.context.AppSignatureHelper
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientPhoneNumber
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class MyDevicesFragment : BaseFragment<FragmentMyDevicesBinding, MyDevicesViewModel>(
    FragmentMyDevicesBinding::inflate, MyDevicesViewModel::class.java
), BaseInterface {

    private lateinit var devicesAdapter: DevicesAdapter
    private lateinit var terminateDeviceDialog: TerminateDeviceDialog

    private var list = ArrayList<UserDevices>()
    private var userDevice: UserDevices? = null

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.deleteAll.setOnClickListener {
            if (list.isNotEmpty()) {
                val first = list[0]
                first.my_device_code = requireActivity().getDeviceIds()
                terminateSessionRequest(first, OPERATION_DELETE_ALL)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initSetOnClickListeners()
        getActiveSessions()
    }

    private fun initRecyclerView() {
        binding.trustedDevices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            devicesAdapter = DevicesAdapter(list, this@MyDevicesFragment)
            adapter = devicesAdapter
        }
        binding.deviceName.text = android.os.Build.MODEL
        binding.lastSeen.text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
    }

    override fun terminateSessionType(type: String) {
        super<BaseFragment>.terminateSessionType(type)
    }

    private fun getActiveSessions() {
        binding.currentDevice.visibility = View.GONE
        binding.myDevice.visibility = View.GONE
        binding.myDeviceTitle.visibility = View.GONE
        binding.otherDeviceTitle.visibility = View.GONE
        binding.deleteAll.visibility = View.GONE
        val skeleton = showSkeleton(binding.trustedDevices, devicesAdapter, R.layout.shimmer_item_my_devices, 4)
        viewModel.getActiveSessions(getClientToken(), GetUserDevicesRequest(user_id = getClientId())).observe(viewLifecycleOwner) { it ->
            skeleton.hide()
            binding.currentDevice.visibility = View.VISIBLE
            when (it.status) {
                Status.SUCCESS -> {
                    binding.myDevice.visibility = View.VISIBLE
                    binding.myDeviceTitle.visibility = View.VISIBLE
                    binding.otherDeviceTitle.visibility = View.VISIBLE
                    binding.deleteAll.visibility = View.VISIBLE
                    val data = it.data?.user_devices
                    list.clear()
                    data?.forEach {
                        if (it.device_code != requireContext().getDeviceIds()) list.add(it)
                    }
                    initRecyclerView()
                    if (list.isEmpty()) {
                        binding.emptyView.visibility = View.VISIBLE
                    } else {
                        binding.emptyView.visibility = View.GONE
                    }
                }

                Status.ERROR -> {
                    binding.emptyView.visibility = View.VISIBLE
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun terminateSessionRequest(item: UserDevices, type: String) {
        showProgress()
        viewModel.checkDevice(
            getClientToken(), CheckDeviceRequest(
                user_id = getClientId(),
                app_key_hash = AppSignatureHelper(requireContext()).appKeyHash,
                phone_number = getClientPhoneNumber(),
                current_device_code = requireActivity().getDeviceIds()
            )
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    hideProgress()
                    goto(
                        R.id.confirmSmsFragment, bundle = bundleOf(
                            "user_device" to item,
                            Const.OPERATION to SMS_OPERATION_TERMINATE_SESSION,
                            "type" to type,
                            ConfirmSmsFragment.STRING_LINE to it.data?.string_line
                        )
                    )
                }

                Status.ERROR -> {
                    hideProgress()
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    override fun terminateSession(item: UserDevices) {
        userDevice = item
        userDevice?.my_device_code = requireActivity().getDeviceIds()
        super<BaseFragment>.terminateSession(item)
        terminateDeviceDialog = TerminateDeviceDialog(item, object : BaseInterface {
            override fun deviceDelete() {
                super.deviceDelete()
                terminateDeviceDialog.dismiss()
                terminateSessionRequest(userDevice!!, OPERATION_DELETE)
            }

            override fun deviceState() {
                super.deviceState()
                terminateDeviceDialog.dismiss()
                val state = if (item.status == "A") OPERATION_DEACTIVATE else OPERATION_ACTIVATE
                terminateSessionRequest(userDevice!!, state)
            }

            override fun deviceDeleteAll() {
                super.deviceDeleteAll()
                terminateDeviceDialog.dismiss()
                terminateSessionRequest(userDevice!!, OPERATION_DELETE_ALL)
            }
        })
        terminateDeviceDialog.show(childFragmentManager, "")
    }

    companion object {
        const val OPERATION_DELETE = "delete"
        const val OPERATION_DELETE_ALL = "deleteAll"
        const val OPERATION_ACTIVATE = "activate"
        const val OPERATION_DEACTIVATE = "deactivate"

    }

}