package uz.fido.universaldigital.ui.fragments.services.money_transfers.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uz.fido.network.domain.model.money_transfer.receive.Country
import uz.fido.universaldigital.R
import uz.fido.universaldigital.databinding.ChooseCountryDialogBinding
import uz.fido.universaldigital.ui.fragments.services.money_transfers.adapters.ChooseCountryAdapter
import java.util.Locale

class ChooseCountryDialog(
    var country: List<Country>,
    private var selectedCountry: (Country) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var binding: ChooseCountryDialogBinding
    private var adapter: ChooseCountryAdapter? = null
    private var layoutManager: LinearLayoutManager? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog)
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChooseCountryDialogBinding.inflate(inflater, container, false)
        adapter = ChooseCountryAdapter(country as ArrayList<Country>) {
            selectedCountry.invoke(it)
        }
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.countryList.adapter = adapter
        binding.countryList.layoutManager = layoutManager
        adapter?.notifyDataSetChanged()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty() && p0.toString().isNotEmpty()) {
                    binding.searchEditText.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_clear,
                        0
                    )
                } else {
                    binding.searchEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
                passSearch(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        binding.searchEditText.setOnTouchListener(View.OnTouchListener { _, event ->
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (binding.searchEditText.text.toString().isNotEmpty()) {
                    if (event.rawX >= binding.searchEditText.right - binding.searchEditText.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                        binding.searchEditText.setText("")
                        return@OnTouchListener true
                    }
                }
            }
            false
        })
    }

    private fun setWhiteNavigationBar(dialog: Dialog) {
        val window: Window? = dialog.window
        if (window != null) {
            val metrics = DisplayMetrics()
            window.windowManager.defaultDisplay.getMetrics(metrics)
            val dimDrawable = GradientDrawable()
            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(Color.WHITE)
            val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
            val windowBackground = LayerDrawable(layers)
            windowBackground.setLayerInsetTop(1, metrics.heightPixels)
            window.setBackgroundDrawable(windowBackground)
        }
    }

    private fun passSearch(str: String) {
        binding.countryList.setHasFixedSize(true)
        binding.countryList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val filteredList = ArrayList<Country>()
        if (str.isEmpty()) {
            binding.countryList.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = ChooseCountryAdapter(country as ArrayList<Country>) {
                    selectedCountry.invoke(it)
                }
            }
        } else {
            val list = country
            for (i in list.indices) {
                if (list[i].name.lowercase(Locale.getDefault()).contains(str.lowercase(Locale.getDefault()))) {
                    filteredList.add(list[i])
                }
            }
            binding.countryList.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = ChooseCountryAdapter(filteredList) {
                    selectedCountry.invoke(it)
                }
            }
        }
    }
}