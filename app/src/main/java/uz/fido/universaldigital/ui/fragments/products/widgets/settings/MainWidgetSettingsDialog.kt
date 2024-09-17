package uz.fido.universaldigital.ui.fragments.products.widgets.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.paperdb.Paper
import uz.fido.network.domain.model.widget.MainWidget
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.DialogHomeWidgetSettingsBinding
import uz.fido.universaldigital.ui.fragments.products.adapter.HomeWidgetsHiddenAdapter
import uz.fido.universaldigital.ui.fragments.products.adapter.HomeWidgetsVisibleAdapter
import uz.fido.utils.const.Const
import uz.fido.utils.utility.view.recycler_view_drag.EditItemTouchHelperCallbackWidgets
import uz.fido.utils.utility.view.recycler_view_drag.OnStartDragListener

@SuppressLint("NotifyDataSetChanged")
class MainWidgetSettingsDialog(private val baseInterface: BaseInterface) :
    BottomSheetDialogFragment(), BaseInterface, OnStartDragListener {

    private lateinit var binding: DialogHomeWidgetSettingsBinding

    private var visibleList = ArrayList<MainWidget>()
    private var hiddenList = ArrayList<MainWidget>()

    private var mainWidgetSettingsAdapter: HomeWidgetsVisibleAdapter? = null
    private var mainWidgetsHiddenAdapter: HomeWidgetsHiddenAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogHomeWidgetSettingsBinding.inflate(inflater, container, false)
        this.isCancelable = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visibleList.clear()
        hiddenList.clear()
        val langList=getAllWidgetList()
        val list: ArrayList<MainWidget> = Paper.book().read(Const.MAIN_WIDGETS)
        list.forEach {
            val widget= langList.firstOrNull { item->item.id==it.id }
            if (it.is_visible) {
                it.name=widget?.name?:it.name
                visibleList.add(it)
            } else {
                it.name=widget?.name?:it.name
                hiddenList.add(it)
            }
        }
        binding.buttonSave.setOnClickListener {
            val newList = ArrayList<MainWidget>()
            for (i in 0 until visibleList.size) {
                newList.add(
                    MainWidget(
                        name = visibleList[i].name,
                        id = visibleList[i].id,
                        is_visible = true,
                        order = i
                    )
                )
            }
            for (i in 0 until hiddenList.size) {
                newList.add(
                    MainWidget(
                        name = hiddenList[i].name,
                        id = hiddenList[i].id,
                        is_visible = false,
                        order = i
                    )
                )
            }
            Paper.book().write(Const.MAIN_WIDGETS, newList)
            baseInterface.updateWidgetList(newList)
            dismiss()
        }
        binding.recyclerWidgets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            mainWidgetSettingsAdapter = HomeWidgetsVisibleAdapter(
                visibleList,
                this@MainWidgetSettingsDialog
            )
            adapter = mainWidgetSettingsAdapter
            val callback = EditItemTouchHelperCallbackWidgets(mainWidgetSettingsAdapter!!)
            val mItemTouchHelper = ItemTouchHelper(callback)
            mItemTouchHelper.attachToRecyclerView(this)
        }
        binding.recyclerHiddenWidgets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            mainWidgetsHiddenAdapter =
                HomeWidgetsHiddenAdapter(hiddenList, this@MainWidgetSettingsDialog)
            adapter = mainWidgetsHiddenAdapter
        }
    }

    private fun getAllWidgetList():ArrayList<MainWidget> {
        val newList= arrayListOf<MainWidget>()
        val names = resources.getStringArray(R.array.main_widgets)
        val ids = resources.getIntArray(R.array.main_widgets_ids)
        for (i in names.indices) {
            newList.add(
                MainWidget(
                    id = ids[i], name = names[i], false, order = i + 1
                )
            )
        }
        return newList
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog
                .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            setupFullHeight(bottomSheet)
            if (bottomSheet != null) {
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = false
            }
        }
        return bottomSheetDialog
    }

    private fun setupFullHeight(bottomSheet: FrameLayout?) {
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {}

    override fun updateWidgetList(list: ArrayList<MainWidget>) {}

    override fun addToHiddenWidgets(position: Int) {
        if (visibleList.size > 2) {
            val item = visibleList[position]
            hiddenList.add(item)
            visibleList.removeAt(position)
            mainWidgetSettingsAdapter?.notifyDataSetChanged()
            mainWidgetsHiddenAdapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.min_widget_size),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun addToVisibleWidgets(position: Int) {
        val item = hiddenList[position]
        visibleList.add(item)
        hiddenList.removeAt(position)
        mainWidgetSettingsAdapter?.notifyDataSetChanged()
        mainWidgetsHiddenAdapter?.notifyDataSetChanged()
    }

}