package uz.fido.universaldigital.ui.fragments.services.map_payment.local_payment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.network.domain.model.payment.location.LocalPayment
import uz.fido.network.domain.model.payment.location.LocalPaymentType
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.databinding.FragmentLocalPaymentBinding
import uz.fido.universaldigital.ui.fragments.services.map_payment.PaymentBranchViewModel
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.format.Format
import uz.fido.utils.utility.fragment.gotoWithSlide
import uz.fido.utils.utility.fragment.pop
import java.math.BigDecimal

@AndroidEntryPoint
class LocalPaymentFragment : BaseFragment<FragmentLocalPaymentBinding, PaymentBranchViewModel>
    (FragmentLocalPaymentBinding::inflate, PaymentBranchViewModel::class.java), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener {

    private val viewmodels by activityViewModels<PaymentBranchViewModel>()
    private var localPayment: LocalPayment? = null
    private var type: LocalPaymentType? = null
    private var clientPosition: LatLng? = null
    private var mMap: GoogleMap? = null

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MapsInitializer.initialize(requireContext());
        clientPosition = viewmodels.clientPosition.value
        arguments?.let {
            localPayment = it.serializable<LocalPayment>("local_payment") as LocalPayment?
            type = it.serializable<LocalPaymentType>("type") as LocalPaymentType?
        }

        setTextView()
        setOnClickView()
    }

    private fun setOnClickView() {
        binding.appBar.setOnBackButtonClickListener { pop() }
        binding.btnContinue.setOnClickListener {
            binding.btnContinue.setProgress(true)
            gotoWithSlide(
                R.id.confirmLocalPaymentFragment,
                bundleOf(
                    "model" to localPayment, "type" to type,
                    "amount" to binding.etAmount.text.toString().replace(" ", "").trim()
                )
            )

        }
        binding.tvPhone.setOnClickListener {
            val phone = "tel: ${localPayment!!.phone}"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(phone)
            startActivity(intent)

        }
    }

    private fun setTextView() {
        binding.appBar.setTitle(localPayment?.sv_merchant_name ?: "")
        binding.tvAddress.text = localPayment?.address ?: ""
        if (localPayment!!.phone.length > 8)
            binding.tvPhone.text = "${Format.toPhoneFormatUZ(localPayment?.phone.toString())}"
        binding.etAmount.addTextChangedListener { amount ->
            binding.btnContinue.isEnabled(amount.toString().length >= 3)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings.isCompassEnabled = false
        mMap!!.uiSettings.isMyLocationButtonEnabled = false

        mMap!!.setOnMarkerClickListener(this)
        currLocationMarker(zoom = 13f, isMove = false, addMarker = true)
        markerAdd()

    }

    private fun markerAdd() {
        if (localPayment != null) {
            val placeLocation = LatLng(
                localPayment!!.x_coordinate.toDouble(),
                localPayment!!.y_coordinate.toDouble()
            )
            val marker = mMap!!.addMarker(
                MarkerOptions().position(placeLocation).title(localPayment!!.sv_merchant_name)
            )

            marker!!.showInfoWindow()
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 13f))
        }


    }


    private fun currLocationMarker(zoom: Float, isMove: Boolean, addMarker: Boolean) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return
        } else {
            mMap!!.isMyLocationEnabled = true
            LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation.addOnSuccessListener(
                requireActivity()
            ) { location ->
                if (location != null && location.latitude != null && location.longitude != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    if (addMarker) {
                        mMap!!.addMarker(
                            MarkerOptions().position(currentLatLng!!)
                                .title("Current Location")
                        )
                    }
//                    if (isMove) {
//                        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, zoom))
//                    } else {
//                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, zoom))
//                    }
                }
            }
        }
    }

    override fun onStart() {
        binding.mapView.onStart()
        super.onStart()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        return true
    }


    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

}