package uz.fido.universaldigital.ui.fragments.services.goal.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.universaldigital.databinding.GoalWeekBottomSheetBinding

class WeekGoalDialog(private val onclick:(String,Int)->Unit): BottomSheetDialogFragment() {


    private lateinit var binding:GoalWeekBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= GoalWeekBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.monday.setOnClickListener { onclick.invoke(binding.monday.text.toString(),1)  }
        binding.tuesday.setOnClickListener { onclick.invoke(binding.tuesday.text.toString(),2) }
        binding.wednesday.setOnClickListener { onclick.invoke(binding.wednesday.text.toString(),3) }
        binding.thursday.setOnClickListener { onclick.invoke(binding.thursday.text.toString(),4) }
        binding.friday.setOnClickListener { onclick.invoke(binding.friday.text.toString(),5) }
        binding.saturday.setOnClickListener { onclick.invoke(binding.saturday.text.toString(),6) }
        binding.sunday.setOnClickListener { onclick.invoke(binding.sunday.text.toString(),7) }
    }
}