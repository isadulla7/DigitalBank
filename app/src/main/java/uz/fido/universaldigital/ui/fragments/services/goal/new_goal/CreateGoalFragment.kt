package uz.fido.universaldigital.ui.fragments.services.goal.new_goal

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function5
import io.reactivex.rxjava3.functions.Function6
import kotlinx.android.synthetic.main.fragment_create_goal.btn_continue
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.target.EditGoalRequest
import uz.fido.network.domain.model.target.GoalModel
import uz.fido.network.domain.model.target.SetTargetRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentCreateGoalBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.goal.GoalViewModel
import uz.fido.universaldigital.ui.fragments.services.goal.dialog.DialogGoalTermType
import uz.fido.universaldigital.ui.fragments.services.goal.dialog.WeekGoalDialog
import uz.fido.universaldigital.ui.fragments.services.loan.dialog.LoanMonthDialog
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.format.Format.Companion.sendFormat
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import java.math.RoundingMode
import kotlin.math.max

@AndroidEntryPoint
class CreateGoalFragment:BaseFragment<FragmentCreateGoalBinding,GoalViewModel>(
    FragmentCreateGoalBinding::inflate,GoalViewModel::class.java
), View.OnClickListener, (Int, String) -> Unit {

    private lateinit var loanMonthDialog: LoanMonthDialog
    private lateinit var dialogGoalTermType: DialogGoalTermType
   private var  durationType = "W"
    private var numberOfDay = "1"
    private var icAutoPaymentCurrent=true
    private var targetTerm = 1
    private var operation=""
    private lateinit var goalModel: GoalModel
    private lateinit var weekGoalDialog:WeekGoalDialog
    private var isCurrentWeekMonth="week"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         arguments?.let {
             operation=it.getString(Const.OPERATION).toString()
             if (operation=="edit"){
               goalModel=it.serializable<GoalModel>("goal") as GoalModel
                 setEditText()
             }
         }
        checkRadioButtons()
        rxBinding()
        onCLickView()

    }

    private fun setEditText() {
          binding.appBar.setTitle(getString(R.string.change_target))
          binding.etName.setText(goalModel.aim_desc)
        binding.etMaxAmount.setText(Format.formatAmountFromTiynToInteger(goalModel.target_amount))
        binding.edYear.setText(if (goalModel.fixed_duration != 0) "${goalModel.fixed_duration} ${getString(
            uz.fido.utils.R.string.month)}" else getString(R.string.unlimited))
        if (goalModel.fixed_duration==0){
            binding.autoPaymentLayout.visibility = View.VISIBLE
            binding.autopaymentAmount.setText(Format.formatAmountFromTiynToInteger(goalModel.amount))
            targetTerm = 0
        }
        binding.anInitialFree.setText(Format.formatAmountFromTiynToInteger(goalModel.start_amount))
        binding.etFireprool.setText(Format.formatAmountFromTiynToInteger(goalModel.decreasing_amount))

        if (goalModel.set_duration_type == "M") {
            binding.monthRadio.isChecked = true
            binding.weekRadio.isChecked = false
            isCurrentWeekMonth="month"
            durationType = "M"
            binding.etDaysWeek.setText("1")
        } else {
            binding.weekRadio.isChecked = true
            binding.monthRadio.isChecked = false
            isCurrentWeekMonth="week"
            durationType = "W"
            binding.etDaysWeek.setText(week(goalModel.set_duration.toInt()))
        }

    }

    fun week(day:Int):String{
       return when (day) {
            1 -> getString(R.string.monday)
            2 -> getString(R.string.tuesday)
            3 -> getString(R.string.wednesday)
            4 -> getString(R.string.thursday)
            5 -> getString(R.string.friday)
            6 -> getString(R.string.saturday)
            else -> getString(R.string.sunday)
        }
    }

    private fun okayAutoPayment() {
        if (!icAutoPaymentCurrent){
            binding.iconAuto.setImageResource(R.drawable.check_construktor)
            icAutoPaymentCurrent=true

        }else{
            binding.iconAuto.setImageResource(R.drawable.check_box_color)
            icAutoPaymentCurrent=false
        }
    }

    private fun onCLickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.edYear.setOnClickListener(this)
        binding.iconYear.setOnClickListener(this)
        binding.etDaysWeek.setOnClickListener(this)
        binding.iconDaysWeek.setOnClickListener(this)
        binding.iconAuto.setOnClickListener(this)
        binding.btnContinue.setOnClickListener {
            if (operation=="create")
            openConfirm()
            else editConfirm()
        }
    }

    private fun editConfirm() {
        val editGoalRequest = EditGoalRequest(
            target_id = goalModel.target_id,
            aim_desc = binding.etName.editableText.toString(),
            duration = targetTerm.toString(),
            target_amount = Format.formatAmountToTiyn(binding.etMaxAmount.editableText.toString().replace(" ", "").trim()),
            set_duration = numberOfDay,
            set_duration_type = durationType,
            image_name = "",
            confirmed = if (icAutoPaymentCurrent) "Y" else "N",
            decreasing_amount = Format.formatAmountToTiyn(binding.etFireprool.editableText.toString().replace(" ", "").trim()),
            state = goalModel.state,
            from_objects = arrayListOf()
        )
        goto(R.id.confirmGoalFragment, bundleOf(
            Const.OPERATION to "edit",
            "model" to editGoalRequest,
                  "week_day" to binding.etDaysWeek.text.toString(),
                "time" to binding.edYear.text.toString())
        )
    }

    private fun openConfirm() {
        val targetAmount = binding.etMaxAmount.editableText.toString().trim().replace(" ", "")
        val firstPayment = binding.anInitialFree.editableText.toString().trim().replace(" ", "")
        if (targetTerm != 0 && targetAmount.isNotEmpty()) {
            Log.d("TAG", "openConfirm:${targetAmount.toBigDecimal()} ")
            Log.d("TAG", "openConfirm:${targetTerm} ")
            val amount = (targetAmount.toBigDecimal()
                .minus(firstPayment.toBigDecimal())).divide(targetTerm.toBigDecimal(), 2, RoundingMode.HALF_UP).toString()
            binding.autopaymentAmount.setText(amount.dropLast(3))
            Log.d("TAG", "openConfirm:${amount.dropLast(3)} ")

        }
        Log.d("TAG", "openConfirm:${binding.autopaymentAmount.editableText.toString()} ")
        val bundle=Bundle()
        bundle.putString(Const.OPERATION,"create")
        bundle.putSerializable("target",getTargetRequest())
        bundle.putString("week_day",binding.etDaysWeek.text.toString())
        bundle.putString("time",binding.edYear.text.toString())
       gotoWithSlide(R.id.confirmGoalFragment,bundle)
    }

    fun getTargetRequest()=SetTargetRequest(
        depType = "",
        depId = "",
        fund_object_value = "",
        fund_object_type = "KL",
        aim_desc =binding.etName.text.toString(),
        duration = targetTerm.toString(),
        target_amount = Format.formatAmountToTiyn(binding.etMaxAmount.editableText.toString().replace(" ", "").trim()),
        amount = sendFormat(
            binding.autopaymentAmount.editableText.toString().trim().replace(" ", "")
        ),
        set_duration = numberOfDay,
        set_duration_type = durationType,
        confirmed = if (icAutoPaymentCurrent) "Y" else "N",
        decreasing_amount = Format.formatAmountToTiyn(binding.etFireprool.editableText.toString().replace(" ", "").trim()),
        from_objects = arrayListOf(),
        image_name = "goal_image_1",
        start_amount = Format.formatAmountToTiyn(binding.anInitialFree.editableText.toString().replace(" ", "").trim()))

    private fun rxBinding() {
        Observable.combineLatest(
            binding.etName.textChanges(),
            binding.etMaxAmount.textChanges(),
            binding.edYear.textChanges(),
            binding.anInitialFree.textChanges(),
            binding.etFireprool.textChanges(),
            binding.autopaymentAmount.textChanges(),
            Function6(this::isValit)
        ).doOnNext {
            binding.btnContinue.isEnabled(it)
        }
            .subscribe()
    }

    private fun isValit(
        name: CharSequence,
        maxAmount: CharSequence,
        month: CharSequence,
        anInitialFree: CharSequence,
        fireprool: CharSequence,
        autoPaymentAmount: CharSequence
    ): Boolean {
        val checkName=name.isNotEmpty() && month.isNotEmpty()
        val checkMaxAmount=maxAmount.isNotEmpty() && maxAmount.toString().replace(" ","").toInt()>499
        val checkFireprool=anInitialFree.isNotEmpty() && anInitialFree.toString().replace(" ", "").toLong() > 499
                && fireprool.isNotEmpty() && fireprool.toString().replace(" ", "").toLong() > 499



        return checkName && checkMaxAmount && checkFireprool
    }



    private fun checkRadioButtons() {
        binding.monthRadio.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                isCurrentWeekMonth="month"
                durationType = "M"
                binding.etDaysWeek.setText("1")
            }
        }

        binding.weekRadio.setOnCheckedChangeListener { compoundButton, b ->
           if (b){
               isCurrentWeekMonth="week"
               durationType = "W"
               binding.etDaysWeek.setText(getString(R.string.monday))

           }
        }
    }




    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.ed_year,R.id.icon_year->{
             openMonthDialog()
            }
            R.id.et_days_week,R.id.icon_days_week->{
                openDialog()
            }
            R.id.icon_auto->{
                okayAutoPayment()
            }
        }
    }

    private fun openMonthDialog() {
      dialogGoalTermType= DialogGoalTermType(){ number, name->
          numberOfDay=number.toString()
          binding.edYear.setText(name.toString())
          if (number==6) {
              binding.autoPaymentLayout.visibility=View.VISIBLE
              targetTerm = 0
              binding.btnContinue.isEnabled(false)

          }else{
              binding.autoPaymentLayout.visibility=View.GONE
              targetTerm = number * 6
          }
      }
      dialogGoalTermType.show(childFragmentManager,"")
    }

    private fun openDialog() {
        if (isCurrentWeekMonth=="week"){
            weekOpenDialog()
        }else{
           monthOpenDialog()
        }
    }

    private fun monthOpenDialog() {
        loanMonthDialog = LoanMonthDialog(this, "", "day", 0, requireContext())
        loanMonthDialog.show(childFragmentManager, "")
    }

    private fun weekOpenDialog() {
      weekGoalDialog =WeekGoalDialog(){day,number->
          weekGoalDialog.dismiss()
            numberOfDay = number.toString()
           binding.etDaysWeek.setText(day)
       }
        weekGoalDialog.show(childFragmentManager,"")
    }

    override fun invoke(count: Int, type: String) {
        when(type){
            "day"->{
                numberOfDay=count.toString()
                loanMonthDialog.dismiss()
                binding.etDaysWeek.setText(count.toString())
            }
        }
    }
}