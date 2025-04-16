package uz.fido.universaldigital.ui.fragments.profile.about_bank.branches

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.maps.android.SphericalUtil
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.branches.Branches
import uz.fido.network.domain.model.branches.GetBranchListRequest
import uz.fido.universaldigital.R
import uz.fido.universaldigital.base.BaseFragment
import uz.fido.universaldigital.base.BaseInterface
import uz.fido.universaldigital.databinding.FragmentBranchesBinding
import uz.fido.universaldigital.ui.fragments.profile.MenuProfileViewModel
import uz.fido.universaldigital.ui.utils.extensions.bitmapDescriptorFromVector
import uz.fido.utils.app.PermissionInterface
import uz.fido.utils.utility.adapter.showSkeleton
import uz.fido.utils.utility.fragment.goto
import uz.fido.utils.utility.fragment.pop
import uz.fido.utils.utility.user.getClientToken
import kotlin.math.roundToInt

@AndroidEntryPoint
class MainBranchesFragment : BaseFragment<FragmentBranchesBinding, MenuProfileViewModel>(
    FragmentBranchesBinding::inflate, MenuProfileViewModel::class.java
), BaseInterface, OnMapReadyCallback, LocationListener,
    GoogleMap.OnMarkerClickListener, PermissionInterface {

    private lateinit var branchesAdapter: BranchesAdapter
    private lateinit var lastLocation: Location

    private var branches = ArrayList<Branches>()
    private var map: GoogleMap? = null

    companion object {
        var currentLatLng: LatLng? = null
        var allBranches = ArrayList<Branches>()
        const val FILIAL_TYPE_ATM = "B"
    }

    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        branchesAdapter = BranchesAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBranchesRv()
        getBranchesFromViewModel()
        initSetOnClickListeners()
        handleBackPressed()
        bottomSheetStateChangeListener()
    }

    private fun getBranchesFromViewModel() {
        viewModel.branches.observe(viewLifecycleOwner) { it ->
            (it as ArrayList<Branches>).sortByDescending { it.distance }
            buildListWithOperation(it)
        }
    }

    private fun initSetOnClickListeners() {
        binding.apply {
            appBar.setOnBackButtonClickListener { pop() }
            appBar.setOnAdditionalBtnClickListener {
                val behavior = BottomSheetBehavior.from(binding.bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            backFromDetails.setOnClickListener {
                val behavior = BottomSheetBehavior.from(binding.bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            additionalMap.setOnClickListener {
                val behavior = BottomSheetBehavior.from(binding.bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            binding.locateMe.setOnClickListener {
                currLocationMarker(
                    zoom = 14f,
                    isMove = true,
                    addMarker = false
                )
            }
        }
    }

    private fun buildListWithOperation(list: ArrayList<Branches>) {
        branches.clear()
        list.forEach {
            branches.add(it)
        }
        buildBranchList()
    }

    override fun openBranchDetails(branch: Branches, viewHolder: BranchesAdapter.ViewHolder) {
        super<BaseFragment>.openBranchDetails(branch, viewHolder)
        val bundle = Bundle()
        bundle.putSerializable("branch", branch)
        goto(R.id.fragmentBranchDetails, bundle)
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map?.let {
            it.uiSettings.isZoomControlsEnabled = false
            it.uiSettings.isCompassEnabled = false
            it.uiSettings.isMyLocationButtonEnabled = false
            branchesMarker()
            it.setOnMarkerClickListener(this)
        }
    }

    override fun onLocationChanged(p0: Location) {}

    override fun onMarkerClick(p0: Marker) = false

    private fun currLocationMarker(zoom: Float, isMove: Boolean, addMarker: Boolean) {
        if (checkForLocationPermissions(this)) {
            initBranches(addMarker, isMove, zoom)
        }
    }

    private fun initBranches(addMarker: Boolean, isMove: Boolean, zoom: Float) {
        map?.let {
            it.isMyLocationEnabled = true
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation.addOnSuccessListener(
                requireActivity()
            ) { location ->
                if (location != null) {
                    lastLocation = location
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    if (addMarker) {
                        it.addMarker(
                            MarkerOptions().position(currentLatLng!!).title("Current Location")
                        )
                    }
                    if (isMove) {
                        it.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng!!, zoom
                            )
                        )
                    } else {
                        it.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLatLng!!, zoom
                            )
                        )
                    }
                } else {
                    currentLatLng = LatLng(41.3775, 64.5853)
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 4f))
                }
                fetchBranches()
            }
        }
    }

    override fun locationPermissionGranted() {
        super.locationPermissionGranted()
        initBranches(addMarker = true, isMove = false, zoom = 13f)
    }

    private fun fetchBranches() {
        if (viewModel.branches.value.isNullOrEmpty()) {
            getBranches()
        }
    }

    private fun getBranches() {
        val skeletonScreen =
            showSkeleton(binding.list, branchesAdapter, R.layout.shimmer_item_branch, 4)
        viewModel.getBranches(getClientToken(), GetBranchListRequest("info", ""))
            .observe(viewLifecycleOwner) { resource ->
                resource?.let {
                    skeletonScreen.hide()
                    when (resource.status) {
                        Status.SUCCESS -> {
                            if (resource.data!!.filials.isNotEmpty()) {
                                allBranches.clear()
                                allBranches = resource.data!!.filials
                                allBranches.forEach { item ->
                                    run {
                                        if (item.x_coordinate.toString()
                                                .isNotEmpty() && item.y_coordinate.toString()
                                                .isNotEmpty()
                                        ) {
                                            if (!item.x_coordinate!!.contains(",") && !item.y_coordinate!!.contains(
                                                    ","
                                                )
                                            ) {
                                                val x =
                                                    java.lang.Double.parseDouble(item.x_coordinate.toString())
                                                val y =
                                                    java.lang.Double.parseDouble(item.y_coordinate.toString())
                                                val location = LatLng(x, y)
                                                val distance =
                                                    (SphericalUtil.computeDistanceBetween(
                                                        currentLatLng, location
                                                    ) / 1000)
                                                val formattedDistance =
                                                    (distance * 100).roundToInt() / 100.0
                                                item.distance = formattedDistance.toString()
                                            } else {
                                                val distance =
                                                    (SphericalUtil.computeDistanceBetween(
                                                        currentLatLng, LatLng(0.0, 0.0)
                                                    ) / 1000)
                                                val formattedDistance =
                                                    (distance * 100).roundToInt() / 100.0
                                                item.distance = formattedDistance.toString()
                                            }
                                        } else {
                                            val distance = (SphericalUtil.computeDistanceBetween(
                                                currentLatLng, LatLng(0.0, 0.0)
                                            ) / 1000)
                                            val formattedDistance =
                                                (distance * 100).roundToInt() / 100.0
                                            item.distance = formattedDistance.toString()
                                        }
                                    }
                                }
                                viewModel.updateBranches(allBranches)
                                buildListWithOperation(allBranches)
                            }
                        }

                        Status.ERROR -> {
                            pop()
                            showSnackbar(resource.message.toString())
                        }
                    }
                }
            }
    }

    private fun initBranchesRv() {
        binding.list.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = branchesAdapter
        }
    }

    private fun buildList(list: ArrayList<Branches>) {
        try {
            list.sortWith(compareBy { it.distance?.toDouble() ?: 0.0 })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        branchesAdapter.submitList(list)
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val behavior = BottomSheetBehavior.from(binding.bottomSheet)
                    if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                        pop()
                    }
                }
            })
    }

    private fun bottomSheetStateChangeListener() {
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.appBar.setAdditionalIcon(R.drawable.ic_map)
                    binding.backFromDetails.visibility = View.VISIBLE
                    binding.additionalMap.visibility = View.VISIBLE
                } else {
                    binding.appBar.setAdditionalIcon(R.drawable.ic_map_list)
                    binding.backFromDetails.visibility = View.GONE
                    binding.additionalMap.visibility = View.GONE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
    }

    private fun buildBranchList() {
        buildList(branches)
        branchesMarker()
    }

    private fun branchesMarker() {
        map?.let {
            it.clear()
            currLocationMarker(zoom = 13f, isMove = false, addMarker = true)
            val selectedFilialBranches = branches
            selectedFilialBranches.forEach { selectedBranch ->
                if (isBranchLocationCorrect(selectedBranch)) {
                    addMarkerToSelectedBranch(it, selectedBranch)
                }
            }
        }
    }

    private fun isBranchLocationCorrect(selectedBranch: Branches): Boolean {
        return selectedBranch.x_coordinate != "" && selectedBranch.y_coordinate != "" && !selectedBranch.x_coordinate!!.contains(
            ","
        ) && !selectedBranch.y_coordinate!!.contains(
            ","
        )
    }

    private fun addMarkerToSelectedBranch(map: GoogleMap, selectedBranch: Branches) {
        val xCoordinate = (selectedBranch.x_coordinate!!.toDouble())
        val yCoordinate = (selectedBranch.y_coordinate!!.toDouble())
        val location = LatLng(xCoordinate, yCoordinate)
        map.addMarker(
            MarkerOptions().position(location).title(selectedBranch.name).icon(
                bitmapDescriptorFromVector(
                    requireContext(),
                    if (selectedBranch.filial_type == FILIAL_TYPE_ATM) R.drawable.ic_map_pin_atm else R.drawable.ic_map_pin_branch
                )
            )
        )
    }

    override fun onStart() {
        binding.mapView.onStart()
        super.onStart()
    }

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

}
