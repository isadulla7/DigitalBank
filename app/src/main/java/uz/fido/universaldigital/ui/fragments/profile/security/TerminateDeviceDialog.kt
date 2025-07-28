package uz.fido.universaldigital.ui.fragments.profile.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.sessions.UserDevices
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogDiveceBinding

class TerminateDeviceDialog(
    private val item: UserDevices,
    private var baseInterface: BaseInterface,
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDiveceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDiveceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvState.text = if (item.status != "A") getString(R.string.activ_dialog) else getString(R.string.ne_active_dialog)
        binding.tvDelete.setOnClickListener {
            baseInterface.deviceDelete()
        }
        binding.tvState.setOnClickListener {
            baseInterface.deviceState()
        }
    }
}