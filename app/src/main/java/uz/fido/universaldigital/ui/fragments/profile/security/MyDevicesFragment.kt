package uz.fido.universaldigital.ui.fragments.profile.security

import TerminateSessionDialog
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import uz.fido.universaldigital.ui.fragments.profile.dialog.DeviceDialog
import uz.fido.utils.const.Const
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.context.AppSignatureHelper
import uz.fido.utils.utility.context.getDeviceIds
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientId
import uz.fido.utils.utility.user.getClientPhoneNumber
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class MyDevicesFragment : BaseFragment<FragmentMyDevicesBinding, MyDevicesViewModel>(
    FragmentMyDevicesBinding::inflate, MyDevicesViewModel::class.java
), BaseInterface {

    private lateinit var terminateSessionDialog: TerminateSessionDialog
    private var devicesAdapter: DevicesAdapter? = null
    private var userDevices: UserDevices? = null
    private var list = ArrayList<UserDevices>()
    private lateinit var deviceDialog:DeviceDialog

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnAdditionalBtnClickListener {
            if (list.isNotEmpty()) {
                val first=list[0]
                first?.my_device_code=requireActivity().getDeviceIds()
                terminateSessionRequest(first, "deleteAll")
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
            devicesAdapter = DevicesAdapter(list, this@MyDevicesFragment,requireContext())
            adapter = devicesAdapter
        }
    }

    override fun terminateSessionType(type: String) {
        super<BaseFragment>.terminateSessionType(type)

    }

    private fun getActiveSessions() {
        binding.currentDevice.visibility = View.GONE
        binding.myDevice.visibility = View.GONE
        binding.myDeviceTitle.visibility = View.GONE
        binding.otherDeviceTitle.visibility = View.GONE
        val skeleton = showSkeleton(
            binding.trustedDevices, devicesAdapter!!, R.layout.shimmer_item_my_devices, 4
        )
        viewModel.getActiveSessions(
            getClientToken(), GetUserDevicesRequest(
                user_id = getClientId()
            )
        ).observe(viewLifecycleOwner) { it ->
            skeleton.hide()
            binding.currentDevice.visibility = View.VISIBLE
            when (it.status) {
                Status.SUCCESS -> {
                    binding.myDevice.visibility = View.VISIBLE
                    binding.myDeviceTitle.visibility = View.VISIBLE
                    binding.otherDeviceTitle.visibility = View.VISIBLE
                    val data = it.data?.user_devices
                    list.clear()
                    data?.forEach {
                        Log.d("TAG", "getActiveSessions: ${requireContext().getDeviceIds()}")
                        if (it.device_code != requireContext().getDeviceIds()) list.add(it)
                        else {
                            binding.deviceName.text =
                                it.device_name + ", " + if (it.device_type == "A") "Android" else "iOS"
                            binding.lastSeen.text =
                                it.last_seen_date /*+ " - " + it.city + ", " + it.country*/
                        }
                    }
                    initRecyclerView()
                    if(list.isEmpty()){
                        binding.emptyView.visibility=View.VISIBLE
                    }else{
                        binding.emptyView.visibility=View.GONE
                    }
                }

                Status.ERROR -> {
                    binding.emptyView.visibility=View.VISIBLE
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun terminateSessionRequest(item: UserDevices, type: String) {
      //  terminateSessionDialog.dismiss()
        showProgress()
        viewModel.checkDevice(
            getClientToken(), CheckDeviceRequest(
                device_type = item.device_type,
                selected_device_code = item.device_code,
                user_id = getClientId(),
                app_key_hash = AppSignatureHelper(requireContext()).appKeyHash,
                current_device_code = requireActivity().getDeviceIds(),
                phone_number = getClientPhoneNumber()
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
        userDevices = item
        userDevices?.my_device_code=requireActivity().getDeviceIds()
        super<BaseFragment>.terminateSession(item)
        deviceDialog= DeviceDialog(item,object :BaseInterface{
            override fun deviceDelete() {
                super.deviceDelete()
                deviceDialog.dismiss()
                terminateSessionRequest(userDevices!!, "delete")
            }

            override fun deviceState() {
                super.deviceState()
                deviceDialog.dismiss()
                val state=if (item.status=="A")"deactivate" else "activate"
                terminateSessionRequest(userDevices!!, state)
            }

            override fun deviceDeleteAll() {
                super.deviceDeleteAll()
                deviceDialog.dismiss()
                terminateSessionRequest(userDevices!!, "deleteAll")
            }
        })
        deviceDialog.show(childFragmentManager,"")


     /*   terminateSessionDialog = TerminateSessionDialog("") {
            terminateSessionRequest(userDevices!!, "delete")
        }
        terminateSessionDialog.show(childFragmentManager, "TAG")*/
    }

}