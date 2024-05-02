package uz.fido.universaldigital.ui.fragments.services.goal.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.DialogGoalOperationBinding

class GoalOperationDialog(
    private val state:String,
    private var onClick: (Int) -> Unit,
) : BottomSheetDialogFragment()  {

    private lateinit var binding: DialogGoalOperationBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogGoalOperationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (state=="P"){
            binding.pause.text = getString(R.string.restart)
        }
        binding.edit.setOnClickListener { onClick.invoke(1) }
        binding.pause.setOnClickListener { onClick.invoke(2) }
        binding.delete.setOnClickListener {  onClick.invoke(3)}
    }
}