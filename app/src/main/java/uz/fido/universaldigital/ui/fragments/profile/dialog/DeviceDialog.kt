package uz.fido.universaldigital.ui.fragments.profile.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.item_goal.view.status
import kotlinx.android.synthetic.main.item_quick_action.item
import uz.fido.network.domain.model.sessions.UserDevices
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DepositConstructorTimeBottomSheetBinding
import uz.fido.universaldigital.databinding.DialogDiveceBinding

class DeviceDialog (
    val item: UserDevices,
    private var baseInterface: BaseInterface,
): BottomSheetDialogFragment(){

    private lateinit var binding: DialogDiveceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DialogDiveceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding.tvState.text = if (item.online!="A") getString(R.string.active) else getString(R.string.ne_active)
        binding.tvDelete.setOnClickListener {
            baseInterface.deviceDelete()
        }
        binding.tvState.setOnClickListener {
            baseInterface.deviceState()
        }
        binding.tvDeleteAll.setOnClickListener {
            baseInterface.deviceDeleteAll()
        }
    }
}