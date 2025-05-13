package uz.fido.universaldigital.ui.fragments.products.cards.card_operations.card_limits.set_limits

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.limits.LimitDeleteRequest
import uz.fido.network.domain.model.limits.SvLimit
import uz.fido.network.domain.model.limits.SvSetCardLimitRequest
import uz.fido.network.domain.model.limits.gl.GlSetCardLimitRequest
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentSetCardLimitsBinding
import uz.fido.universaldigital.ui.dialogs.ReferenceDialog
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.services.deposit.step_deposit.BasicSuccessFragment
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.const.CardConst
import uz.fido.utils.const.CardConst.UZCARD
import uz.fido.utils.const.Const
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class SetCardLimitsFragment : BaseFragment<FragmentSetCardLimitsBinding, MenuProductsViewModel>(
    FragmentSetCardLimitsBinding::inflate, MenuProductsViewModel::class.java
), View.OnClickListener {

    private lateinit var operationType: String
    private lateinit var myCalendar: Calendar
    private lateinit var card: CardResponse

    private var limitTypes = ArrayList<AllServiceLists>()
    private var svLimit: SvLimit? = null
    private var limitId: String? = null
    private var referenceDialog: ReferenceDialog? = null
    private var buttonOperation = "save"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            card = it.serializable<CardResponse>(Const.CARD) as CardResponse
            operationType = it.getString(Const.OPERATION).toString()
            svLimit = it.serializable("model") as SvLimit?
        }
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        init()
    }

    private fun init() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnAdditionalBtnClickListener {
            deleteSvCardLimit()
        }
        binding.continueButton.setOnClickListener { addLimitRequest() }
        binding.limitType.setOnClickListener(this)
        binding.endDate.setOnClickListener(this)
        binding.startDate.setOnClickListener(this)
        myCalendar = Calendar.getInstance()
        if (operationType == "set") {
            binding.appBar.setAdditionalBtnVisibility(false)
        } else {
            binding.appBar.setAdditionalBtnVisibility(true)
        }
        fetchLimitParams()
        checkEditTexts()
    }

    private fun fetchLimitParams() {
        if (card.object_type == UZCARD) {
            getSvLimitParams()
            binding.startDateLayout.visibility = View.GONE
            binding.endDateLayout.visibility = View.GONE
        } else {
            binding.startDateLayout.visibility = View.VISIBLE
            getGlLimitParams()
        }
    }

    private fun checkEditTexts() {
        val editTexts = listOf(
            binding.limitType, binding.endDate, binding.etAmount
        )
        for (editText in editTexts) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    val et1 = binding.limitType.text.toString()
                    val et3 = binding.endDate.text.toString()
                    val et2 = binding.startDate.text.toString()
                    val et4 = binding.etAmount.text.toString()
                    if (card.object_type == CardConst.HUMO_CARD) {
                        binding.continueButton.isEnabled(et1.isNotEmpty() && et3.isNotEmpty() && et4.isNotEmpty() && et2.isNotEmpty())
                    } else {
                        binding.continueButton.isEnabled(et1.isNotEmpty() && et4.isNotEmpty())
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun getGlLimitParams() {
        viewModel.getGlLimitParams(getClientToken()).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data!!
                    limitTypes = response.humo_limit_types
                    if (operationType == "edit") {
                        limitTypes.forEach { type ->
                            if (type.code == svLimit?.limitId) {
                                binding.limitType.setText(type.name)
                            }
                        }
                        limitId = svLimit?.limitId.toString()
                        binding.etAmount.setText(Format.formatAmountFromTiynToInteger(svLimit?.limitAmount.toString()))
                        binding.endDate.setText(requireContext().getString(R.string.used_amount) + " : " + svLimit?.usedAmount + " UZS")
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun setGlCardLimit() {
        myCalendar = Calendar.getInstance()
        val request = GlSetCardLimitRequest(
            date_to = binding.endDate.text.toString().replace(" ", "").replace("-", "")
                .replace(":", ""),
            limit_amount = Format.formatAmountToTiyn(
                binding.etAmount.text.toString().replace(" ", "")
            ),
            limit_id = limitId!!,
            object_id = card.object_id,
            date_from = binding.startDate.text.toString().replace(" ", "").replace("-", "").replace(":", ""),
            limit_name = binding.limitType.text.toString()
        )
        viewModel.setGlCardLimit(getClientToken(), request).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val bundle = Bundle()
                    bundle.putString(Const.OPERATION, BasicSuccessFragment.LIMIT)
                    gotoWithSlide(R.id.basicSuccessFragment, bundle)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun getSvLimitParams() {
        viewModel.getSvLimitParams(getClientToken()).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val response = it.data!!
                    limitTypes = response.limit_id
                    if (operationType == "edit") {
                        limitTypes.forEach { type ->
                            if (type.code == svLimit?.limitId) {
                                binding.limitType.setText(type.name)
                            }
                        }
                        limitId = svLimit?.limitId.toString()
                        binding.etAmount.setText(Format.formatAmountFromTiynToInteger(svLimit?.limitAmount.toString()))
                        binding.endDate.setText(requireContext().getString(R.string.used_amount) + " : " + svLimit?.usedAmount + " UZS")
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun setSvCardLimit() {
        val request = SvSetCardLimitRequest(
            limit_amount = Format.formatAmountToTiyn(
                binding.etAmount.text.toString().replace(" ", "")
            ),
            limit_id = limitId!!,
            object_id = card.object_id
        )
        viewModel.setSvCardLimit(getClientToken(), request).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val bundle = Bundle()
                    bundle.putString(Const.OPERATION, BasicSuccessFragment.LIMIT)
                    gotoWithSlide(R.id.basicSuccessFragment, bundle)
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun deleteSvCardLimit() {
        showProgress()
        val request = LimitDeleteRequest(
            limit_id = limitId!!,
            object_id = card.object_id
        )
        viewModel.deleteSvCardLimit(getClientToken(), request).observe(viewLifecycleOwner) {
            hideProgress()
            when (it.status) {
                Status.SUCCESS -> {
                    if (buttonOperation == "edit") {
                        setSvCardLimit()
                    } else {
                        showSnackbar(getString(R.string.limit_deletec), getString(R.string.successfully))
                        Handler(Looper.getMainLooper()).postDelayed({
                            pop()
                        }, 500)
                    }
                }

                Status.ERROR -> {
                    showSnackbar(it.message.toString())
                }
            }
        }
    }

    private fun datePicker(isStart: Boolean) {
        DatePickerDialog(
            requireContext(),
            if (isStart) startDatePicker else datePick,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private val datePick = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val myFormat = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.endDate.setText(sdf.format(myCalendar.time))
    }

    private val startDatePicker = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val myFormat = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.startDate.setText(sdf.format(myCalendar.time))
    }

    private fun continueButtonClicked() {
        if (card.object_type == UZCARD) {
            if (buttonOperation == "edit") {
                deleteSvCardLimit()
            } else {
                setSvCardLimit()
            }
        } else {
            setGlCardLimit()
        }
    }

    private fun addLimitRequest() {
        if (operationType == "edit") {
            buttonOperation = "edit"
        }
        if (limitId == null) {
            return
        }
        if (binding.etAmount.text.toString().isEmpty() || binding.etAmount.text.toString()
                .startsWith("0")
        ) {
            return
        }
        continueButtonClicked()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.limit_type -> {
                referenceDialog = ReferenceDialog(object : BaseInterface {
                    override fun setToEditText(allServiceLists: AllServiceLists, tag: String) {
                        binding.limitType.setText(allServiceLists.name)
                        limitId = allServiceLists.code
                        referenceDialog?.dismiss()
                    }
                }, limitTypes, "")
                referenceDialog?.show(childFragmentManager, "")
            }

            R.id.end_date -> {
                datePicker(false)
            }

            R.id.start_date -> {
                datePicker(true)
            }
        }
    }

}