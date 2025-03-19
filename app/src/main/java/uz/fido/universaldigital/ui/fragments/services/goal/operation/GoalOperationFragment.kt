package uz.fido.universaldigital.ui.fragments.services.goal.operation

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.target.ChangeTargetStateRequest
import uz.fido.network.domain.model.target.GoalHistory
import uz.fido.network.domain.model.target.GoalModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentGoalOperationBinding
import uz.fido.universaldigital.ui.fragments.monitoring.filter.TransactionTypeDialog
import uz.fido.universaldigital.ui.fragments.services.goal.GoalViewModel
import uz.fido.universaldigital.ui.fragments.services.goal.adapter.GoalHistoryAdapter
import uz.fido.universaldigital.ui.fragments.services.goal.dialog.GoalOperationDialog
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.libs.skeleton.SkeletonScreen
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.math.RoundingMode

@AndroidEntryPoint
class GoalOperationFragment : BaseFragment<FragmentGoalOperationBinding, GoalViewModel>(
    FragmentGoalOperationBinding::inflate, GoalViewModel::class.java
), View.OnClickListener {

    private lateinit var goalModel: GoalModel
    private var skeletonScreen: SkeletonScreen? = null
    private var historiesAdapter: GoalHistoryAdapter? = null
    private lateinit var transactionTypeDialog: TransactionTypeDialog
    private lateinit var goalOperationDialog: GoalOperationDialog
    private var list = arrayListOf<GoalHistory>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goalModel = arguments?.serializable<GoalModel>("goal") as GoalModel

        initView()
        setTextGoalModel()
        getHistory()
        onCLick()
    }

    private fun onCLick() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setTitle(goalModel.aim_desc)
        binding.outcome.setOnClickListener(this)
        binding.income.setOnClickListener(this)
        binding.filter.setOnClickListener(this)
        binding.appBar.setOnAdditionalBtnClickListener {
            goalOperationDialog = GoalOperationDialog(goalModel.state) {
                goalOperationDialog.dismiss()
                getOperation(it)
            }
            goalOperationDialog.show(childFragmentManager, "")
        }
    }

    private fun getOperation(it: Int) {
        when (it) {
            1 -> {
                editGoal()
            }

            2 -> {
                changeState()
            }

            3 -> {
                deleteItem()
            }
        }
    }

    private fun deleteItem() {
        goto(R.id.deleteGoalFragment, bundleOf("goal" to goalModel))
    }

    private fun editGoal() {
        val bundle = Bundle()
        bundle.putSerializable("goal", goalModel)
        bundle.putString(Const.OPERATION, "edit")
        goto(R.id.createGoalFragment, bundle)
    }

    private fun changeState() {
        showProgress()
        viewModel.changeTargetState(getClientToken(), ChangeTargetStateRequest(goalModel.target_id)).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    if (goalModel.state == "A") {
                        goalModel.state = "P"
                        binding.status.visibility = View.VISIBLE
                    } else {
                        goalModel.state = "A"
                        binding.status.visibility = View.GONE
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun initView() {
        binding.rec.apply {
            setHasFixedSize(true)
            val linerLayoutManager = LinearLayoutManager(context)
            layoutManager = linerLayoutManager
            historiesAdapter = GoalHistoryAdapter(ArrayList())
            adapter = historiesAdapter
        }
    }

    private fun getHistory() {
        skeletonScreen = showSkeleton(binding.rec, historiesAdapter!!, R.layout.shimmer_item_account_history)
        viewModel.targetHistories(getClientToken(), ChangeTargetStateRequest(target_id = goalModel.target_id)).observe(viewLifecycleOwner) {
            skeletonScreen?.hide()
            when (it.status) {
                Status.SUCCESS -> {
                    val data = it.data!!.transact_list
                    list = data
                    historiesAdapter!!.setNewList(data)
                    if (data.isEmpty()) {
                        binding.rec.visibility = View.GONE
                        binding.emptyView.visibility = View.VISIBLE
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }


    private fun setTextGoalModel() {
        binding.tvName.text = goalModel.aim_desc
        binding.tvLeftAmount.text = getString(
            R.string.from_amount_credit,
            Format.formatAmount(Format.formatAmountFromTiynToInteger(goalModel.target_amount)) + " UZS"
        )

        binding.tvBalance.text =
            Format.formatAmount(Format.formatAmountFromTiynToInteger(goalModel.current_amount)) + " UZS"
        if (goalModel.state == "P") {
            binding.status.visibility = View.VISIBLE
        }
        val perc = calculatePercentage(goalModel)
        binding.progressView.max = 100
        binding.progressView.progress = if (perc > 0) perc else 1

    }

    private fun calculatePercentage(item: GoalModel): Int {
        return item.current_amount.toBigDecimal().multiply(100.toBigDecimal())
            .divide(item.target_amount.toBigDecimal(), 2, RoundingMode.HALF_UP).toInt()

    }

    private fun getFilterList(it: String) {
        when (it) {
            getString(R.string.write_offs) -> {
                val newList = arrayListOf<GoalHistory>()
                list.forEach {
                    if (it.amount.startsWith("-")) {
                        newList.add(it)
                    }
                }
                historiesAdapter!!.setNewList(newList)
                if (newList.isEmpty()) {
                    binding.rec.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    binding.rec.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                }
            }

            getString(R.string.enrollments) -> {
                val newList = arrayListOf<GoalHistory>()
                list.forEach {
                    if (!it.amount.startsWith("-")) {
                        newList.add(it)
                    }
                }
                historiesAdapter!!.setNewList(newList)
                if (newList.isEmpty()) {
                    binding.rec.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                } else {
                    binding.rec.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.income -> {
                goto(R.id.goalIncomeFragment, bundleOf("goal" to goalModel))
            }

            R.id.outcome -> {
                goto(R.id.goalOutComeFragment, bundleOf("goal" to goalModel))
            }

            R.id.filter -> {
                transactionTypeDialog = TransactionTypeDialog {
                    binding.filter.setImageResource(R.drawable.ic_filter_yes)
                    getFilterList(it)
                    transactionTypeDialog.dismiss()
                }
                transactionTypeDialog.show(childFragmentManager, "")
            }

        }
    }
}