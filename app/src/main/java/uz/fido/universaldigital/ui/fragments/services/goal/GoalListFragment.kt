package uz.fido.universaldigital.ui.fragments.services.goal

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.target.GoalModel
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentGoalListBinding
import uz.fido.universaldigital.ui.fragments.services.goal.adapter.GoalListAdapter
import uz.fido.utils.const.Const
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class GoalListFragment : BaseFragment<FragmentGoalListBinding, GoalViewModel>(
    FragmentGoalListBinding::inflate, GoalViewModel::class.java
) {

    private lateinit var goalAdapter: GoalListAdapter
    private var goalList = ArrayList<GoalModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goalAdapter = GoalListAdapter(goalList) {
            gotoWithSlide(R.id.goalOperationFragment, bundleOf("goal" to it))
        }
        recyclerView()
        onClickView()
        getTargetList()
    }

    private fun recyclerView() {
        binding.rec.apply {
            adapter = goalAdapter
        }
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            gotoWithSlide(R.id.createGoalFragment, bundleOf(Const.OPERATION to "create"))
        }
    }

    private fun getTargetList() {
        val skeletonScreen = showSkeleton(binding.rec, goalAdapter, R.layout.shimmer_item_goal, 5)
        viewModel.getTargetList(getClientToken()).observe(viewLifecycleOwner) {
            skeletonScreen.hide()
            when (it.status) {
                Status.SUCCESS -> {
                    goalList = it.data?.user_target_list ?: arrayListOf()
                    if (goalList.isEmpty()) binding.empty.visibility = View.VISIBLE else View.GONE
                    goalAdapter.setList(goalList)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }

    }
}