package uz.fido.universaldigital.ui.fragments.services.map_payment

import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.tabs.TabLayoutMediator
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.base_dialog_delete.dismiss
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.network.domain.model.payment.location.LocalPaymentType
import uz.fido.network.domain.model.payment.location.PaymentByLocationRequest
import uz.fido.network.domain.model.payment.location.PaymentByLocationResponse
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentPaymentBranchBinding
import uz.fido.universaldigital.ui.fragments.monitoring.adapter.MonitoringViewPagerAdapter
import uz.fido.universaldigital.ui.fragments.services.map_payment.dialog.LocalPaymentSearchDialog
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class PaymentBranchFragment : BaseFragment<FragmentPaymentBranchBinding, PaymentBranchViewModel>
    (FragmentPaymentBranchBinding::inflate, PaymentBranchViewModel::class.java) {

    val viewModels by activityViewModels<PaymentBranchViewModel>()

    private var clientPosition: LatLng? = LatLng(41.3775, 64.5853)
    private val df = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
    private var homeOnSpotPaymentList = ArrayList<LocalPayment>()
    private var localPaymentType = arrayListOf<LocalPaymentType>()
    private var positionType: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAppBar()
        viewModels.getLocalPaymentType().observe(viewLifecycleOwner) { typeList ->
            showProgress(getString(R.string.payment_by_location_loading))
            if (typeList.isEmpty()) {
                fetchLocalPaymentTypes()
            } else {
                getRoomListType(typeList)
            }
        }
    }

    private fun getRoomListType(typeList: List<LocalPaymentType>) {
        localPaymentType = arrayListOf()
        typeList.forEach { localPaymentType.add(it) }
        initViewPager(localPaymentType)
        getClientLocation()
    }

    private fun initAppBar() {
        binding.appBar.setAdditionalBtnVisibility(true)
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.appBar.setOnAdditionalBtnClickListener {
            var newLocalType = LocalPaymentType()
            val dialog = LocalPaymentSearchDialog(
                viewModels.localPayment.value!!
            ) { local ->
                localPaymentType.forEach {
                    if (it.id == local.type_id)
                        newLocalType = it
                }
                goto(
                    R.id.localPaymentFragment,
                    bundleOf("local_payment" to local, "type" to newLocalType)
                )
                dismiss
            }
            dialog.show(childFragmentManager, "TAG")
        }

    }

    private fun fetchLocalPaymentTypes() {
        viewModel.fetchLocalPaymentTypes(
            getClientToken()
        ).observe(viewLifecycleOwner) { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    val response = resources.data!!.local_payment_types
                    val newList = ArrayList<LocalPaymentType>()
                    newList.add(
                        0,
                        LocalPaymentType(
                            id = "-1",
                            name = getString(R.string.all),
                            order_number = "0"
                        )
                    )
                    newList.addAll(response)
                    localPaymentType = newList
                    newList.forEach {
                        viewModels.saveLocalPaymentType(it).observe(viewLifecycleOwner) {}
                    }

                    initViewPager(newList)
                    getClientLocation()
                }

                Status.ERROR -> {

                }
            }
        }
    }

    private fun initViewPager(newList: ArrayList<LocalPaymentType>) {
        viewModels.setLocalPaymentType(newList[0])
        val viewPagerAdapter = MonitoringViewPagerAdapter(requireActivity(), listFragment(newList))
        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = newList[position].name

        }.attach()
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                positionType = position
                viewModels.setLocalPaymentType(localPaymentType[position])
            }
        })
    }

    private fun listFragment(newList: ArrayList<LocalPaymentType>): ArrayList<Fragment> {
        val listFragment = ArrayList<Fragment>()
        newList.forEach {
            listFragment.add(PaymentByLocationListFragment())
        }
        return listFragment
    }

    @SuppressLint("MissingPermission")
    private fun getClientLocation() {
        val locationManager =
            requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        var location: Location? = null

        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f) { }
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) gotoLocation(location.latitude, location.longitude)
        } else if (isGPSEnabled) {
            if (location == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f) { }
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) gotoLocation(location.latitude, location.longitude)
            }
        } else {
            gotoLocation(clientPosition!!.latitude, clientPosition!!.longitude)
            binding.noLocation.visibility = View.VISIBLE
        }
    }

    private fun gotoLocation(latitude: Double, longitude: Double) {
        clientPosition = LatLng(latitude, longitude)
        viewModels.clientPosition.value = clientPosition
        if (!viewModels.isCurrent)
            viewModels.getLocalPayment().observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    getPaymentByList(0)
                } else getRoomList(it)
            } else {
            hideProgress()
            initViewPager(localPaymentType)
        }


    }

    private fun getRoomList(it: List<LocalPayment>) {
        val list = arrayListOf<LocalPayment>()
        it.forEach { list.add(it) }
        homeOnSpotPaymentList = list
        setDistance()
        homeOnSpotPaymentList.sortBy {
            it.distance?.toDouble() ?: 0.0
        }
        viewModels.setPaymentByLocation(paymentLocation = list)
        viewModels.isCurrent = true
        hideProgress()
        if (localPaymentType.isNotEmpty())
            initViewPager(localPaymentType)
    }

    private fun getPaymentByList(page: Int) {
        val paymentByLocationRequest = createLocationRequest(page)
        viewModel.fetchLocalPayments(
            getClientToken(),
            paymentByLocationRequest
        ).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val response = resource.data as PaymentByLocationResponse
                    homeOnSpotPaymentList.addAll(response.local_payments_onspot_list)
                    if (response.last == "true" && response.local_payments_onspot_list.isNotEmpty()) {
                        getPaymentByList(page + 1)
                    } else {
                        saveDbLocalPayment(homeOnSpotPaymentList)
                        setDistance()
                        viewModels.setPaymentByLocation(paymentLocation = homeOnSpotPaymentList)
                        viewModels.isCurrent = true
                        hideProgress()
                        if (localPaymentType.isNotEmpty())
                            initViewPager(localPaymentType)
                    }
                }

                Status.ERROR -> {

                }
            }

        }
    }

    private fun saveDbLocalPayment(homeOnSpotPaymentList: ArrayList<LocalPayment>) {
        homeOnSpotPaymentList.forEach {
            viewModels.saveLocalPayment(it).observe(viewLifecycleOwner) {}
        }
    }

    private fun setDistance() {
        homeOnSpotPaymentList.forEach {
            val targetLocation =
                LatLng(
                    java.lang.Double.parseDouble(it.x_coordinate),
                    java.lang.Double.parseDouble(it.y_coordinate)
                )
            val distance =
                (SphericalUtil.computeDistanceBetween(clientPosition, targetLocation) / 1000)
            val formattedDistance = (distance * 100).roundToInt() / 100.0
            it.distance = formattedDistance.toString()

        }
    }

    private fun createLocationRequest(page: Int): PaymentByLocationRequest {
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2000)
        return PaymentByLocationRequest(
            x_coordinate = clientPosition?.longitude.toString(),
            y_coordinate = clientPosition?.latitude.toString(),
            radius = "100",
            page_item_size = "50",
            page_number = page.toString(),
            type_id = "",
            text = "",
            last_date = df.format(cal.time)
        )
    }


}