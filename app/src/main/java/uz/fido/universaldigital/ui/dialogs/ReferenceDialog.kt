package uz.fido.universaldigital.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ListBottomSheetBinding
import uz.fido.universaldigital.ui.main_dialogs.adapters.ServicesAdapter

class ReferenceDialog(
    private var baseInterface: BaseInterface,
    private val list: List<AllServiceLists>,
    private var tags: String
) : BottomSheetDialogFragment() {

    private lateinit var binding: ListBottomSheetBinding
    private var layoutManager: LinearLayoutManager? = null
    private var adapter: ServicesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListBottomSheetBinding.inflate(inflater, container, false)
        adapter = ServicesAdapter(baseInterface, list as ArrayList<AllServiceLists>, tags)
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.list.adapter = adapter
        binding.list.layoutManager = layoutManager
        return binding.root
    }
}
