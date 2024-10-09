package uz.fido.universaldigital.ui.fragments.services.loan.loan_info

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.client_info.ClientDetailedInfo
import uz.fido.network.domain.model.my_id.MyIdGetAccessTokenResponse
import uz.fido.network.domain.model.my_id.Profile
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentLoanUserInfo1Binding
import uz.fido.universaldigital.ui.fragments.services.loan.LoanViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.universaldigital.ui.utils.keys.Keys
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.pop

@AndroidEntryPoint
class LoanUserInfo1Fragment : BaseFragment<FragmentLoanUserInfo1Binding, LoanViewModel>(
    FragmentLoanUserInfo1Binding::inflate, LoanViewModel::class.java
) {

    private lateinit var clientDetailedInfo: ClientDetailedInfo
    private var profile: Profile? = null

    companion object {
        const val CREDIT_ITEM = "credit_group"
        const val CLIENT_INFO = "client_info"
        const val CREDIT_PROGRESS_STEP = "credit_progress_step"
        const val CLIENT_MY_ID_INFO = "credit_my_id_info"
        const val RESULT_USER_INFO = "result_user_info"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgumentsItem()
        // myIdToPass()


    }

    private fun getArgumentsItem() {
        clientDetailedInfo = requireArguments().serializable<ClientDetailedInfo>(CLIENT_INFO) as ClientDetailedInfo
    }

    private val faceIdActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            getAccessToken(it?.data?.getStringExtra("code").toString())
        }
    }

    private fun getAccessToken(code: String) {
        showProgress()
        viewModel.getAccessToken(
            grant_type = "authorization_code",
            code = code,
            client_id = Keys.getMyIdClientId(),
            client_secret = Keys.getClientSecret(),
            redirect_url = "https://universalbank.uz/"
        ).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    getMeRequest(it.data!!)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }

            }
        }

    }

    private fun getMeRequest(data: MyIdGetAccessTokenResponse) {
        showProgress()
        viewModel.getMe("Bearer " + data.access_token).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    profile = it.data?.profile
                    if (profile != null) {
                        //  init()
                    } else {
                        pop()
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }
}