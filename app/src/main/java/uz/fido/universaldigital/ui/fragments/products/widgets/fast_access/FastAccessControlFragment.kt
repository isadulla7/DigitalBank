package uz.fido.universaldigital.ui.fragments.products.widgets.fast_access

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import io.paperdb.Paper
import kotlinx.coroutines.launch
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentFastAccessControlBinding
import uz.fido.universaldigital.ui.fragments.products.adapter.FastAccessHiddenAdapter
import uz.fido.universaldigital.ui.fragments.products.adapter.FastAccessVisibleAdapter
import uz.fido.universaldigital.ui.fragments.products.model.FastAccessOperation
import uz.fido.universaldigital.ui.utils.extensions.getFastAccessOperationList
import uz.fido.utils.const.Const
import uz.fido.utils.utility.fragment.pop

class FastAccessControlFragment : BaseSimpleFragment<FragmentFastAccessControlBinding>(
    FragmentFastAccessControlBinding::inflate
), BaseInterface {

    private var visibleList = ArrayList<FastAccessOperation>()
    private var hiddenList = ArrayList<FastAccessOperation>()

    private var mainWidgetSettingsAdapter: FastAccessVisibleAdapter? = null
    private var mainWidgetsHiddenAdapter: FastAccessHiddenAdapter? = null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        initList()
    }

    private fun initList() {
        val checkList= getFastAccessOperationList(requireContext())
        visibleList.clear()
        hiddenList.clear()
        val list: ArrayList<FastAccessOperation> = Paper.book().read(Const.FAST_ACCESS)
        list.forEach {
            viewLifecycleOwner.lifecycleScope.launch {
                val newItem=checkList.firstOrNull {item-> item.id==it.id }
                it.name=newItem?.name?:it.name
            }
            if (it.isVisible) {
                visibleList.add(it)
            } else {
                hiddenList.add(it)
            }
        }
        binding.recyclerWidgets.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            mainWidgetSettingsAdapter = FastAccessVisibleAdapter(visibleList) { position ->
                if (visibleList.size > 3) {
                    val item = visibleList[position]
                    hiddenList.add(item)
                    visibleList.removeAt(position)
                    mainWidgetSettingsAdapter?.notifyDataSetChanged()
                    mainWidgetsHiddenAdapter?.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.min_element_size_3),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            adapter = mainWidgetSettingsAdapter
        }
        binding.recyclerHiddenWidgets.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            mainWidgetsHiddenAdapter = FastAccessHiddenAdapter(hiddenList) { position ->
                val item = hiddenList[position]
                visibleList.add(item)
                hiddenList.removeAt(position)
                mainWidgetSettingsAdapter?.notifyDataSetChanged()
                mainWidgetsHiddenAdapter?.notifyDataSetChanged()
            }
            adapter = mainWidgetsHiddenAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appbar.setOnBackButtonClickListener { pop() }
        binding.buttonSave.setOnClickListener {
            val newList = ArrayList<FastAccessOperation>()
            for (i in 0 until visibleList.size) {
                newList.add(
                    FastAccessOperation(
                        name = visibleList[i].name,
                        id = visibleList[i].id,
                        isVisible = true,
                        order = i,
                        icon = visibleList[i].icon
                    )
                )
            }
            for (i in 0 until hiddenList.size) {
                newList.add(
                    FastAccessOperation(
                        name = hiddenList[i].name,
                        id = hiddenList[i].id,
                        isVisible = false,
                        order = i,
                        icon = hiddenList[i].icon
                    )
                )
            }
            Paper.book().write(Const.FAST_ACCESS, newList)
            pop()
        }
    }

}