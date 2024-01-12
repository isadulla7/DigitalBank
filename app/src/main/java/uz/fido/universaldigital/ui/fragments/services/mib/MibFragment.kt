package uz.fido.universaldigital.ui.fragments.services.mib

import BaseDeleteDialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.mib.DeleteMibAccount
import uz.fido.network.domain.model.mib.Mib
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMainMibBinding
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibListAdapter
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MibFragment : BaseFragment<FragmentMainMibBinding, MibViewModel>
    (FragmentMainMibBinding::inflate, MibViewModel::class.java) {

    private lateinit var mibList: ArrayList<Mib>
    private val mibListAdapter by lazy { MibListAdapter(requireContext(), this) }
    private var isLongCLick = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onclickView()
        createRecycler()
        getMibList()
    }

    private fun onclickView() {
        binding.btnContinue.setOnClickListener {   goto(R.id.mibAddFragment) }
        binding.toolbar.setOnBackButtonClickListener { pop() }
        binding.toolbar.setOnAdditionalBtnClickListener { addButtonClickEvent() }
    }

    private fun getMibList() {
        val skeletonScreen=showSkeleton(binding.mibRec,mibListAdapter,R.layout.shimmer_item_mib,3)
        viewModel.getMibPassportList(
            getClientToken()
        ).observe(viewLifecycleOwner) { resources ->
            skeletonScreen.hide()
            when (resources.status) {
                Status.SUCCESS -> {
                    val response = resources.data?.passports ?: ArrayList()
                    mibList = response
                    setAdapter()
                    emptyListVisibility()
                }

                Status.ERROR -> {
                    mibList.clear()
                    binding.layoutEmpty.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun emptyListVisibility() {
        binding.layoutEmpty.isVisible = mibList.isEmpty()
    }

    private fun setAdapter() {
        mibListAdapter.submitList(mibList)
    }

    private fun createRecycler() {
        mibList = arrayListOf()
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.mibRec.apply {
            adapter = mibListAdapter
            layoutManager = gridLayoutManager
        }
    }

    override fun openMibInfo(mib: Mib, position: Int) {
        super.openMibInfo(mib, position)
        if (isLongCLick) {
            isCurrentRemove(position)
        } else
            this.goto(R.id.mibDetailsFragment, bundleOf("mib" to mib))
    }

    private fun isCurrentRemove(position: Int) {
        if (mibList[position].isCurrent) {
            mibList[position].isCurrent = false
            updateMibList()
        } else {
            mibList[position].isCurrent = true
            updateMibList()
        }
        isVisibleRemoveIcon()
    }

    override fun openMibInfoLongClick(mib: Mib, position: Int) {
        super.openMibInfoLongClick(mib, position)
        mibList[position].isCurrent = true
        isLongCLick = true
        updateMibList()
        isVisibleRemoveIcon()
    }

    private fun updateMibList() {
        mibListAdapter.submitList(mibList)
        mibListAdapter.notifyDataSetChanged()
    }

    private fun isVisibleRemoveIcon() {
        var isRemove = false
        mibList.forEach {
            if (it.isCurrent) {
                isRemove = true
            }
        }
        isLongCLick = isRemove
        isIconRemove(isRemove)
        binding.layoutEmpty.isVisible = mibList.isEmpty()
    }

    private fun isIconRemove(remove: Boolean) {
        if (remove) binding.toolbar.setAdditionalIcon(R.drawable.delete_icon)
        else binding.toolbar.setAdditionalIcon(R.drawable.add_icon_mib)
    }

    private fun addButtonClickEvent() {
        if (isLongCLick) {
            val deleteList = getDeleteList()
            val dialog = BaseDeleteDialog(
                getString(R.string.delete_title),
                getString(R.string.delete_text)
            ) {
                deleteList.forEach {
                    viewModel.deleteMibAccount(
                        getClientToken(), DeleteMibAccount(
                            client_type = it.client_type.toString(),
                            doc_value = it.doc_value.toString()
                        )
                    ).observe(viewLifecycleOwner) { resources ->
                        when (resources.status) {
                            Status.SUCCESS -> {
                                mibList.remove(it)
                                mibListAdapter.submitList(mibList)
                                mibListAdapter.notifyDataSetChanged()
                                isVisibleRemoveIcon()
                            }

                            Status.ERROR -> {
                               showSnackbar(resources.message.toString())
                            }
                        }
                    }
                }
            }
            dialog.show(childFragmentManager, "")
        } else {
            goto(R.id.mibAddFragment)
        }
    }

    private fun getDeleteList(): ArrayList<Mib> {
        val newList = arrayListOf<Mib>()
        mibList.forEach {
            if (it.isCurrent) {
                newList.add(it)
            }
        }
        return newList
    }
}