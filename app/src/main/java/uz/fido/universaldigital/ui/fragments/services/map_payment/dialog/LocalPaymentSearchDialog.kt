package uz.fido.universaldigital.ui.fragments.services.map_payment.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.network.domain.model.payment.location.LocalPaymentType
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentDialogSearchBinding
import uz.fido.universaldigital.ui.fragments.services.map_payment.adapter.LocalPaymentAdapter


class LocalPaymentSearchDialog(
    private val localPayment: ArrayList<LocalPayment>,
    private val localPaymentTypeList: ArrayList<LocalPaymentType>,
    private var setOnClick: (LocalPayment) -> Unit
) : DialogFragment(), BaseInterface {

    private val localPaymentAdapter by lazy {
        LocalPaymentAdapter(
            ArrayList(),
            requireContext(),
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppBottomSheetDialogThemetwo);
    }

    private lateinit var binding: FragmentDialogSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showKeyboard()
        recyclerView()
        onClickView()
    }

    private fun onClickView() {
        binding.appBar.setOnBackButtonClickListener {
            closeKeyboard()
            dismiss()
        }
    }

    private fun recyclerView() {
        createRecyclerView()
        getSearchList()
    }

    private fun setAdapter(list: List<LocalPayment>) {
        val arrayList = arrayListOf<LocalPayment>()
        arrayList.addAll(list)
        localPaymentAdapter.setList(arrayList)
    }

    private fun getSearchList() {
        binding.search.addTextChangedListener { local ->
            if (local!!.length > 1) {
                val list = localPayment.filter {
                    it.sv_merchant_name.toLowerCase().startsWith(local.toString().toLowerCase())
                }
                setAdapter(list)
            } else setAdapter(listOf())
        }
    }

    override fun openLocalPayment(localPayment: LocalPayment) {
        super.openLocalPayment(localPayment)
        closeKeyboard()
        setOnClick.invoke(localPayment)
    }


    private fun createRecyclerView() {
        binding.recSearch.apply {
            adapter = localPaymentAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun showKeyboard() {
        binding.search.requestFocus()
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        inputMethodManager.showSoftInput(binding.search, 0)
    }

    fun closeKeyboard() {
        val inputMethodManager: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
}