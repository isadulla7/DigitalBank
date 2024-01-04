package uz.fido.universaldigital.ui.fragments.services.mib.addmib.you_mib

import BaseInfoDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.mib.AddMibPassportRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentAddMibBinding
import uz.fido.universaldigital.databinding.FragmentMibYouBinding
import uz.fido.universaldigital.ui.fragments.services.mib.MibViewModel
import uz.fido.universaldigital.ui.fragments.services.mib.addmib.fiz_mib.MibFizFragment
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.MonitoringViewPagerAdapter
import uz.fido.utils.utility.user.getClientToken
import java.util.ArrayList


@AndroidEntryPoint
class MibYouFragment:BaseFragment<FragmentMibYouBinding, MibViewModel>
    (FragmentMibYouBinding::inflate, MibViewModel::class.java){
    private lateinit var baseInfoDialog: BaseInfoDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         textWatcher()
         onclickView()
    }

    private fun onclickView() {
        binding.btnContinue.setOnClickListener {
             getMibInfo()
        }
    }

    private fun textWatcher() {
        binding.etInnMib.addTextChangedListener {
            binding.btnContinue.isEnabled(it.toString().length==9)
        }
    }

    private fun getMibInfo() {
        val  addMibPasswordRequest=createAddMinPasspost()
        binding.btnContinue.setProgress(true)
        viewModel.addMibPassport(
            getClientToken(),
            addMibPasswordRequest
        ).observe(viewLifecycleOwner){resources->
            binding.btnContinue.setProgress(false)
            when(resources.status){
                Status.SUCCESS->{
                    Navigation.findNavController(requireView()).popBackStack(R.id.mibFragment,false)
                }
                Status.ERROR->{
                    showErrorDialog()
                }
            }

        }
    }

    private fun createAddMinPasspost()=
        AddMibPassportRequest(
            client_type = MibFizFragment.YOU,
            doc_value = binding.etInnMib.text.toString())


    private fun showErrorDialog() {
        baseInfoDialog=BaseInfoDialog(getString(R.string.invalit_data),getString(R.string.make_sure_all))
        baseInfoDialog.show(childFragmentManager,"")
    }
}