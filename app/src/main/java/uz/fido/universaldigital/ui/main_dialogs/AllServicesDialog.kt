package uz.fido.universaldigital.ui.main_dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.payment.AllServiceLists
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.ListBottomSheetBinding
import uz.fido.universaldigital.ui.main_dialogs.adapters.ServicesAdapter

class AllServicesDialog(
    private var baseInterface: BaseInterface,
    private val list: List<AllServiceLists>,
    private var type: String? = null,
    private var viewTag: String? = null,
    private var title: String? = null
) : BottomSheetDialogFragment() {

    private lateinit var binding: ListBottomSheetBinding
    private var adapter: ServicesAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var layoutManager2: GridLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = ListBottomSheetBinding.inflate(inflater, container, false)
        if (title != null) {
            binding.tvTitle.text = title
            binding.tvTitle.visibility = View.VISIBLE
        }
        adapter = ServicesAdapter(baseInterface, list as ArrayList<AllServiceLists>, viewTag ?: "")
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        layoutManager2 = GridLayoutManager(requireContext(), 6)
        if (type != null && type != "") {
            if (type == "auto_payment") {
                binding.list.setPadding(16, 0, 16, 0)
                binding.list.layoutManager = layoutManager2
            }
        } else {
            binding.list.layoutManager = layoutManager
        }
        binding.list.adapter = adapter
        return binding.root
    }
}