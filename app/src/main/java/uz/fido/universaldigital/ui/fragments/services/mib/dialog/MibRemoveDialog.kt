package uz.fido.universaldigital.ui.fragments.services.mib.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.fido.universaldigital.databinding.DialogMibRemoveBinding

class MibRemoveDialog : DialogFragment() {

    private lateinit var binding: DialogMibRemoveBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMibRemoveBinding.inflate(inflater, container, false)
        return binding.root
    }

}