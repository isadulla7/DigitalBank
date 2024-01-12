package uz.fido.universaldigital.ui.fragments.services.mib.addmib.fiz_mib

import BaseInfoDialog
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.mib.AddMibPassportRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentMibFizBinding
import uz.fido.universaldigital.ui.fragments.services.mib.MibViewModel
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class MibFizFragment : BaseFragment<FragmentMibFizBinding, MibViewModel>
    (FragmentMibFizBinding::inflate, MibViewModel::class.java) {

    companion object {
        const val FIZ = "fiz"
        const val YOU = "you"
    }

    private lateinit var baseInfoDialog: BaseInfoDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textWatchers()
        onClickView()
    }

    private fun textWatchers() {
        Observable.combineLatest(
            binding.etPassportSerial.textChanges(),
            binding.etPassportNumber.textChanges(),
            BiFunction(this::isValid)
        ).doOnNext {
            binding.btnContinue.isEnabled(it)
        }
            .subscribe()
    }

    private fun isValid(
        passportSerial: CharSequence,
        passportNumber: CharSequence
    ) =
        (passportSerial.toString().length == 2
                && passportNumber.toString().length == 7)


    private fun onClickView() {
        binding.btnContinue.setOnClickListener {
            getMibInfo()
        }
    }

    private fun getMibInfo() {
        val addMibPasswordRequest = AddMibPassportRequest(
            client_type = FIZ,
            doc_value = binding.etPassportSerial.text.toString()
                .uppercase() + binding.etPassportNumber.text.toString()
        )
        binding.btnContinue.setProgress(true)
        viewModel.addMibPassport(
            getClientToken(),
            addMibPasswordRequest
        ).observe(viewLifecycleOwner) { resources ->
            binding.btnContinue.setProgress(false)
            when (resources.status) {
                Status.SUCCESS -> {
                    Navigation.findNavController(requireView())
                        .popBackStack(R.id.mibFragment, false)
                }

                Status.ERROR -> {
                    showErrorDialog()
                }
            }

        }
    }

    private fun showErrorDialog() {
        baseInfoDialog =
            BaseInfoDialog(getString(R.string.invalit_data), getString(R.string.make_sure_all))
        baseInfoDialog.show(childFragmentManager, "")
    }

}