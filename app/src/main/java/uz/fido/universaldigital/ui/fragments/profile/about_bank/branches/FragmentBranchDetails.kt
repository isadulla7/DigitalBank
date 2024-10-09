package uz.fido.universaldigital.ui.fragments.profile.about_bank.branches

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import uz.fido.network.domain.model.branches.Branches
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentBranchDetailsBinding
import uz.fido.universaldigital.ui.utils.extensions.bitmapDescriptorFromVector
import uz.fido.universaldigital.ui.utils.extensions.serializable
import uz.fido.utils.utility.fragment.pop

class FragmentBranchDetails :
    BaseSimpleFragment<FragmentBranchDetailsBinding>(FragmentBranchDetailsBinding::inflate),
    BaseInterface, OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var branch: Branches

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBranchDetails()
        initSetOnClickListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun initBranchDetails() {
        branch = this.requireArguments().serializable<Branches>("branch") as Branches
        binding.name.text = branch.name
        if (!branch.address.isNullOrBlank()) {
            binding.address.text = branch.address
        }
        if (branch.admission_days!!.isNotEmpty() || branch.working_time!!.isNotEmpty()) {
            binding.workingTime.text = branch.admission_days + "\n" + branch.working_time
        }
    }

    private fun initSetOnClickListeners() {
        binding.locate.setOnClickListener { drawRouteBetweenTwoLocations() }
        binding.call.setOnClickListener { callToBranch() }
        binding.appBar.setOnBackButtonClickListener { pop() }
    }

    override fun onStart() {
        binding.mapView.onStart()
        super.onStart()
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = false
        map.uiSettings.isMyLocationButtonEnabled = false
        setMapStyle()
        addBottomPaddingToMap()
        branchesMarker()
        map.setOnMarkerClickListener(this)
    }

    private fun addBottomPaddingToMap() {
        var actionBarHeight = 0
        if (requireContext().theme.resolveAttribute(
                android.R.attr.actionBarSize,
                TypedValue(),
                true
            )
        ) {
            actionBarHeight =
                TypedValue.complexToDimensionPixelSize(TypedValue().data, resources.displayMetrics)
        }
        map.setPadding(0, 0, 0, actionBarHeight)
    }

    private fun setMapStyle() {
        if (isCurrentThemeDark()) {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireActivity(),
                    R.raw.map_style
                )
            )
        } else {
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireActivity(),
                    R.raw.map_style_retro
                )
            )
        }
    }

    override fun onLocationChanged(p0: Location) {}

    override fun onMarkerClick(p0: Marker) = false

    override fun onResume() {
        binding.mapView.onResume()
        super.onResume()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    companion object {
        lateinit var map: GoogleMap

        fun newInstance(
            name: String
        ): FragmentBranchDetails {
            val infoPage = FragmentBranchDetails()
            val bundle = Bundle()
            bundle.putString("name", name)
            infoPage.arguments = bundle
            return infoPage
        }

    }

    private fun drawRouteBetweenTwoLocations() {
        val branchLocationX = branch.x_coordinate
        val branchLocationY = branch.y_coordinate
        val currentLocationX = MainBranchesFragment.currentLatLng?.latitude
        val currentLocationY = MainBranchesFragment.currentLatLng?.longitude
        val uri = Uri.parse(
            "geo:" + branchLocationX.toString() + "," + branchLocationY.toString() + "?q=" + Uri.encode(
                currentLocationX.toString() + "," + currentLocationY.toString()
            ) + "(" + branch.name + ")"
        )
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun callToBranch() {
        if (!branch.phone.isNullOrBlank()) {
            val phone = "tel: ${branch.phone}"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(phone)
            startActivity(intent)
        }
    }

    private fun branchesMarker() {
        map.clear()
        val xCoordinate = (branch.x_coordinate!!.toDouble())
        val yCoordinate = (branch.y_coordinate!!.toDouble())
        val location = LatLng(xCoordinate, yCoordinate)
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {
            map.isMyLocationEnabled = true
            map.addMarker(
                MarkerOptions().position(location).title(branch.name).icon(
                    bitmapDescriptorFromVector(
                        requireContext(),
                        if (branch.filial_type == "B") R.drawable.ic_map_pin_atm else R.drawable.ic_map_pin_branch
                    )
                )
            )
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
        }
    }

}