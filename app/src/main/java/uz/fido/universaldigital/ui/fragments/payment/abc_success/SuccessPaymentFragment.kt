package uz.fido.universaldigital.ui.fragments.payment.abc_success

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.payment.PaymentService
import uz.fido.network.domain.model.template.CreateTemplateRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentSuccessPaymentBinding
import uz.fido.universaldigital.ui.activities.MainActivity
import uz.fido.universaldigital.ui.fragments.payment.abc_confirm.ConfirmPaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.abc_dialog.AddTemplateDialog
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.payment.init_payment.PaymentFragment
import uz.fido.universaldigital.ui.fragments.payment.templates.TemplateTypes
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class SuccessPaymentFragment : BaseFragment<FragmentSuccessPaymentBinding, SuccessPaymentViewModel>(
    FragmentSuccessPaymentBinding::inflate, SuccessPaymentViewModel::class.java
) {

    private lateinit var addTemplateDialog: AddTemplateDialog
    private lateinit var mobileDBHelper: DatabaseHelper
    private lateinit var operation: String

    private var params: HashMap<String, String>? = null
    private var confirmPaymentOperation: String? = null
    private var paymentService: PaymentService? = null
    private var operationAmount = ""

    companion object {
        const val OPERATION_HOME_PAYMENT = "operation_home_payment"
        const val CONFIRM_PAYMENT_OPERATION = "operation"
        const val P2P_OVER_MY_CARDS = "p2p_over_my_cards"
        const val PAYMENT_KEY_VALUES = "key_values"
        const val CREDIT_PAYMENT = "credit_payment"
        const val CONVERSION = "conversion"
        const val PAYMENT = "payment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mobileDBHelper = DatabaseHelper(requireContext())
        operation = requireArguments().getString(Const.OPERATION).toString()
        operationAmount = requireArguments().getString(Const.OPERATION_AMOUNT).toString()
        arguments?.let {
            confirmPaymentOperation =
                it.getString(ConfirmPaymentFragment.CONFIRM_PAYMENT_OPERATION).toString()
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
        initSetOnClickListeners()
        onBackPressCallback()
    }

    private fun init() {
        when (operation) {
            P2P_OVER_MY_CARDS -> {
                binding.addToTemplate.visibility = View.GONE
                binding.repeat.visibility = View.GONE
            }

            PAYMENT -> {
                arguments?.let {
                    paymentService = Gson().fromJson(
                        it.getString(Const.PAYMENT_SERVICE), PaymentService::class.java
                    )
                    params =
                        it.serializable<HashMap<String, String>>(PAYMENT_KEY_VALUES) as HashMap<String, String>
                }
            }

            CREDIT_PAYMENT -> {
                paymentService = mobileDBHelper.getServiceByContractId("-2")
                binding.repeat.visibility = View.GONE
                binding.addToTemplate.visibility = View.GONE
            }

            OPERATION_HOME_PAYMENT -> {
                params =
                    arguments?.serializable<HashMap<String, String>>(PAYMENT_KEY_VALUES) as HashMap<String, String>
                binding.repeat.visibility = View.GONE
                binding.addToTemplate.visibility = View.GONE
                binding.amount.text = operationAmount
                "${Format.formatAmount(requireArguments().getString("amount"))} UZS"
            }
        }
        binding.amount.text = operationAmount
        setOperationTime()
    }

    private fun initSetOnClickListeners() {
        binding.gotoMainPage.setOnClickListener {
            gotoMainPage()
        }
        binding.addToTemplate.setOnClickListener {
            saveTemplate()
        }
        binding.repeat.setOnClickListener {
            pop()
        }
        binding.cheque.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("icon", paymentService?.icon_name)
            bundle.putString("transactId", requireArguments().getString("transactId"))
            bundle.putString("operation", CheckInfoPaymentFragment.OPERATION_PAYMENT)
            bundle.putString("amount", operationAmount)
            goto(R.id.checkInfoPaymentFragment2, bundle)
        }
    }

    private fun saveTemplate() {
        val templateGroupId = PaymentFragment.TemplateGroups.DEFAULT_TEMPLATES.toString()
        var templateType = TemplateTypes.DEFAULT.templateType
        if (confirmPaymentOperation == ConfirmPaymentFragment.OPERATION_REQUISITES) {
            templateType = TemplateTypes.REQUISITES.templateType
        }

        addTemplateDialog = AddTemplateDialog(getString(R.string.templates)) {
            addTemplateDialog.dismiss()
            if (params != null) {
                val model = CreateTemplateRequest(
                    name = it,
                    template_type = templateType,
                    service_type = requireArguments().getString("payment_group_name").toString(),
                    service_id = paymentService?.service_id.toString(),
                    template_group_id = templateGroupId,
                    payment_details = params!!
                )
                showProgress()
                viewModel.createTemplate(getClientToken(), model)
                    .observe(viewLifecycleOwner) { resources ->
                        hideProgress()
                        when (resources.status) {
                            Status.SUCCESS -> {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.successfully),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            Status.ERROR -> {
                                showSnackbar(resources.message.toString())
                            }
                        }
                    }

                addTemplateDialog.show(childFragmentManager, "")
            } else Toast.makeText(
                requireContext(),
                getString(R.string.error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun gotoMainPage() {
        startActivity(Intent(requireActivity(), MainActivity::class.java))
        requireActivity().finish()
    }

    private fun onBackPressCallback() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gotoMainPage()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setOperationTime() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy • HH:mm", Locale.US)
        val now = Calendar.getInstance().time
        binding.date.text = dateFormat.format(now)
    }

}