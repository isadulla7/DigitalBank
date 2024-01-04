package uz.fido.universaldigital.ui.fragments.services.order_card.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.branches.Branches
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ChooseCountryDialogBinding
import uz.fido.universaldigital.ui.fragments.services.order_card.adapters.ChooseBranchAdapter

class ChooseBranchDialog(
    private var baseInterface: BaseInterface,
    var country: List<Branches>
) : BottomSheetDialogFragment() {

    private lateinit var binding: ChooseCountryDialogBinding
    private var adapter: ChooseBranchAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChooseCountryDialogBinding.inflate(inflater, container, false)
        adapter =
            ChooseBranchAdapter(requireContext(), baseInterface, country as ArrayList<Branches>)
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.countryList.adapter = adapter
        binding.countryList.layoutManager = layoutManager
        binding.title.text = getString(R.string.choose_nearest_filial)
        adapter?.notifyDataSetChanged()
        return binding.root
    }
}