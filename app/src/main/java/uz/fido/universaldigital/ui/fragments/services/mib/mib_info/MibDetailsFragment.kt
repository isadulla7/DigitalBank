package uz.fido.universaldigital.ui.fragments.services.mib.mib_info

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.mib.Mib
import uz.fido.network.domain.model.mib.MibDetail
import uz.fido.network.domain.model.mib.MibDetails
import uz.fido.network.domain.model.mib.MibInfoRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMibDetailsBinding
import uz.fido.universaldigital.ui.fragments.services.mib.MibViewModel
import uz.fido.universaldigital.ui.fragments.services.mib.adapter.MibDetailsAdapter
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.util.ArrayList

@AndroidEntryPoint
class MibDetailsFragment : BaseFragment<FragmentMibDetailsBinding, MibViewModel>
    (FragmentMibDetailsBinding::inflate, MibViewModel::class.java) {

    private val mibDetailAdapter by lazy { MibDetailsAdapter(requireContext(), this) }
    private var mib: Mib? = null
    private var detailList = arrayListOf<MibDetail>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mib = arguments?.serializable<Mib>("mib") as Mib
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onclickView()
        recyclerView()
    }

    private fun onclickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun recyclerView() {
        createRecyclerView()
        getMibDetailsInfo()
    }

    private fun getMibDetailsInfo() {
        val skeletonScreen = showSkeleton(binding.recMibDetail, mibDetailAdapter, R.layout.shimmer_item_deposit, 2)
        viewModel.getMibInfo(
            getClientToken(), MibInfoRequest(
                client_type = mib!!.client_type.toString(),
                doc_value = mib!!.doc_value.toString()
            )
        ).observe(viewLifecycleOwner) { resources ->
            skeletonScreen.hide()
            when (resources.status) {
                Status.SUCCESS -> {
                    val response = resources.data?.debets?: arrayListOf()
                    detailList = response

                    mibDetailAdapter.submitList(detailList)
                    emptyView()
                }

                Status.ERROR -> {
                    detailList = arrayListOf()
                    emptyView()
                }
            }

        }
    }

    override fun openInfoMib(mibDetail: MibDetail) {
        super.openInfoMib(mibDetail)
        goto(
            R.id.mibInfoFragment, bundleOf(
                "mib" to mib,
                "mib_detail" to mibDetail
            )
        )
    }

    fun emptyView() {
        if (detailList.isNotEmpty()) {
            binding.layoutEmpty.visibility = View.GONE
        } else {
            binding.layoutEmpty.visibility = View.VISIBLE
        }
    }

    private fun createRecyclerView() {
        binding.recMibDetail.apply {
            adapter = mibDetailAdapter
        }
    }
}