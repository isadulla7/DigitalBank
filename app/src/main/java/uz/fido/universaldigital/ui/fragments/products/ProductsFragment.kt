package uz.fido.universaldigital.ui.fragments.products

import dagger.hilt.android.AndroidEntryPoint
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentPinCodeBinding
import uz.fido.universaldigital.databinding.FragmentProductsBinding

@AndroidEntryPoint
class ProductsFragment : BaseFragment<FragmentProductsBinding, ProductsViewModel>(
    FragmentProductsBinding::inflate, ProductsViewModel::class.java
) {

}