package uz.fido.universaldigital.ui.fragments.services.applications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.applications.ApplicationStatus
import uz.fido.network.domain.model.applications.GetProductDetailsRequest
import uz.fido.network.domain.model.applications.OrderCardApp
import uz.fido.network.domain.model.applications.ProductDetailsResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.fragments.products.UtilsViewModel
import uz.fido.universaldigital.databinding.DialogAppDetailsBinding
import uz.fido.universaldigital.ui.fragments.services.applications.adapter.AppDetailsAdapter
import uz.fido.utils.utility.user.getClientToken

@AndroidEntryPoint
class AppDetailsDialog(
    private var application: OrderCardApp
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogAppDetailsBinding
    private lateinit var applicationStatusAdapter: AppDetailsAdapter
    private lateinit var details: ProductDetailsResponse

    private val utilsViewModel: UtilsViewModel by activityViewModels()
    private var statusList = ArrayList<ApplicationStatus>()

    private var statusDate2 = ""
    private var statusDate3 = ""
    private var statusDate4 = ""
    private var statusDate5 = ""
    private var expireDate = ""
    private var cardNumber = ""
    private var errorText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DialogAppDetailsBinding.inflate(inflater, container, false)
        binding.settingsText.text = application.module_product
        initStatuses()
        return binding.root
    }

    private fun initStatuses() {
        utilsViewModel.getProductDetails(
            getClientToken(), GetProductDetailsRequest(application.application_id.toString())
        ).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data
                    details = response!!
                    initList()
                }

                Status.ERROR -> {
                }
            }
        }
    }

    private fun initList() {
        statusList.clear()
        when (application.state_id) {
            1 -> {
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.new_application),
                        isEnable = true,
                        create_date = application.create_date
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.application_received),
                        isEnable = true,
                        create_date = application.create_date
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.in_processing), isEnable = false
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.completed), isEnable = false
                    )
                )
            }

            2 -> {
                details.status_list.forEach {
                    when (it.state_id) {
                        2 -> statusDate2 = it.create_date.toString()
                    }
                }
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.new_application),
                        create_date = application.create_date,
                        isEnable = true,
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.application_received),
                        create_date = application.create_date,
                        isEnable = true
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.in_processing),
                        create_date = statusDate2,
                        isEnable = true
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.completed), isEnable = false
                    )
                )

            }

            3, 4 -> {
                details.status_list.forEach {
                    when (it.state_id) {
                        3, 4 -> {
                            errorText = it.err_msg.toString()
                            statusDate3 = it.create_date.toString()
                        }
                    }
                }
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.new_application),
                        create_date = application.create_date,
                        isEnable = true
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.application_received), isEnable = true
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.in_processing), isEnable = true
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.completed),
                        create_date = application.create_date,
                        isEnable = true,
                        state_id = -100,
                        err_msg = errorText,
                        status = getString(R.string.cancelled)
                    )
                )
            }

            5 -> {
                cardNumber = details.app_detail.card_number
                expireDate = details.app_detail.date_expery
                details.status_list.forEach {
                    when (it.state_id) {
                        5 -> statusDate5 = it.create_date.toString()
                        4 -> statusDate4 = it.create_date.toString()
                        2 -> statusDate2 = it.create_date.toString()
                    }
                }
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.new_application),
                        create_date = application.create_date,
                        isEnable = true
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.application_received),
                        create_date = application.create_date,
                        isEnable = true
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.in_processing),
                        create_date = statusDate2,
                        isEnable = true
                    )
                )
                statusList.add(
                    ApplicationStatus(
                        state_name = getString(R.string.completed),
                        create_date = statusDate5,
                        isEnable = true,
                        cardNumber = cardNumber,
                        cardExpiry = expireDate,
                        status = getString(R.string.successfully),
                        state_id = 100
                    )
                )
            }
        }

        applicationStatusAdapter = AppDetailsAdapter(statusList)
        binding.statusList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = applicationStatusAdapter
        }
    }

}