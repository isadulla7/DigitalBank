package uz.fido.universaldigital.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class AbstractFragment<VB : ViewBinding, VM : AbstractViewModel>(
    private val inflate: Inflate<VB>,
    private val viewModelClass: Class<VM>
) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!
    protected val abstractViewModel: VM by lazy { ViewModelProvider(this)[viewModelClass] }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        initObserver()
        onInit(inflater, container, savedInstanceState)
        onInit(savedInstanceState)
        return binding.root
    }

    private fun initObserver() {
        //noop
    }

    open fun onInit(savedInstanceState: Bundle?) {
        //noop
    }

    open fun onInit(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) {
        //noop
    }

    fun toast(string: String) {
        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}