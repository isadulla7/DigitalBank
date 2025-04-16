package uz.fido.universaldigital.ui.fragments.services.mib.mib_info

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.mib.Mib
import uz.fido.network.domain.model.mib.MibDetail
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
        initSetOnClickListeners()
        initMibDetailRv()
        getMibDetailsInfo()
    }

    private fun initSetOnClickListeners() {
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    private fun getMibDetailsInfo() {
        val skeletonScreen =
            showSkeleton(binding.recMibDetail, mibDetailAdapter, R.layout.shimmer_item_deposit, 2)
        viewModel.getMibInfo(
            getClientToken(), MibInfoRequest(
                client_type = mib!!.client_type.toString(),
                doc_value = mib!!.doc_value.toString()
            )
        ).observe(viewLifecycleOwner) { resources ->
            skeletonScreen.hide()
            when (resources.status) {
                Status.SUCCESS -> {
                    val response = resources.data?.debets ?: arrayListOf()
                    detailList = response

                    mibDetailAdapter.submitList(detailList)
                    binding.layoutEmpty.isVisible = detailList.isEmpty()
                }

                Status.ERROR -> {
                    detailList = arrayListOf()
                    binding.layoutEmpty.isVisible = detailList.isEmpty()
                }
            }

        }
    }

    override fun openInfoMib(mibDetail: MibDetail) {
        super.openInfoMib(mibDetail)
        goto(R.id.mibInfoFragment, bundleOf("mib" to mib, "mib_detail" to mibDetail))
    }

    private fun initMibDetailRv() {
        binding.recMibDetail.apply {
            adapter = mibDetailAdapter
        }
    }
}