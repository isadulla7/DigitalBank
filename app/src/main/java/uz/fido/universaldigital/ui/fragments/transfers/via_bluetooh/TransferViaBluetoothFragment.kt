package uz.fido.universaldigital.ui.fragments.transfers.via_bluetooh

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import dagger.hilt.android.AndroidEntryPoint
import uz.fido.network.domain.model.cards.CardResponse
import uz.fido.universaldigital.base.BaseSimpleFragment
import uz.fido.universaldigital.databinding.FragmentTransferViaBluetoothBinding
import uz.fido.universaldigital.ui.fragments.products.MenuProductsViewModel
import uz.fido.universaldigital.ui.fragments.transfers.over_my_cards.OverMyCardsAdapter
import uz.fido.universaldigital.ui.utils.file.FileUtils.TAG
import uz.fido.utils.const.CurrencyConst
import uz.fido.utils.device.vibrateTick
import uz.fido.utils.log.Log


@AndroidEntryPoint
class TransferViaBluetoothFragment :
    BaseSimpleFragment<FragmentTransferViaBluetoothBinding>(FragmentTransferViaBluetoothBinding::inflate) {

    private val menuProductsViewModel: MenuProductsViewModel by activityViewModels()
    private var userSumCards = ArrayList<CardResponse>()
    private var senderCard: CardResponse? = null
    private var scanner: BluetoothLeScanner? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    private val bluetoothPermissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )
    } else {
        arrayOf()
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var isAllPermissionGranted = true
        permissions.entries.forEach {
            isAllPermissionGranted = isAllPermissionGranted && it.value
        }
        if (isAllPermissionGranted) {
            startAdvertising()
            startScanner()
        }
    }

    private fun checkPermissions() {
        if (bluetoothPermissionList.isNotEmpty())
            requestMultiplePermissions.launch(bluetoothPermissionList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    @SuppressLint("MissingPermission")
    override fun onInit(savedInstanceState: Bundle?) {
        super.onInit(savedInstanceState)
        binding.radarView.startRippleAnimation()
        initCardList { initSenderCards() }
        checkPermissions()
    }

    @SuppressLint("MissingPermission")
    private fun startAdvertising() {
        val bluetoothManager =
            requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothAdapter?.startDiscovery()

        val bluetoothLeAdvertiser = bluetoothAdapter?.bluetoothLeAdvertiser

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setConnectable(true)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
            .build()

        val service_UUID = ParcelUuid.fromString("0000b81d-0000-1000-8000-00805f9b34fb")

        val data = AdvertiseData.Builder()
            .addServiceUuid(service_UUID)
            .addServiceData(
                service_UUID,
                (senderCard?.object_value)?.toByteArray(Charsets.UTF_8)
            )
            .build()

        bluetoothLeAdvertiser?.startAdvertising(settings, data, object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                super.onStartSuccess(settingsInEffect)
                Log.d(TAG, "Advertising started successfully")
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                Log.e(TAG, "Advertising failed with error code $errorCode")
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun startScanner() {
        scanner = bluetoothAdapter?.bluetoothLeScanner
        val scanSettings =
            ScanSettings.Builder().setCallbackType(ScanSettings.CALLBACK_TYPE_MATCH_LOST).build()
        val scanFilters: ArrayList<ScanFilter> = ArrayList()
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid.fromString("0000b81d-0000-1000-8000-00805f9b34fb"))
            .build()
        scanFilters.add(filter)
        scanner?.startScan(scanFilters, scanSettings, scanCallback)
    }

    private fun initCardList(listener: () -> Unit) {
        menuProductsViewModel.cards.observe(viewLifecycleOwner) { cardList ->
            cardList.forEach {
                if (it.currency_code == CurrencyConst.CURRENCY_CODE_UZS) {
                    userSumCards.add(it)
                }
            }
            if (cardList.isNotEmpty()) senderCard = cardList[0]
            listener.invoke()
        }
    }

    private fun initSenderCards() {
        if (userSumCards.isEmpty()) {
            binding.sendersEmpty.visibility = View.VISIBLE
        } else {
            val senderCardsAdapter = OverMyCardsAdapter(requireContext(), userSumCards)
            binding.senderCards.adapter = senderCardsAdapter
            binding.senderIndicator.setViewPager(binding.senderCards)
            binding.senderCards.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(
                    position: Int, positionOffset: Float, positionOffsetPixels: Int
                ) {
                }

                @SuppressLint("MissingPermission")
                override fun onPageSelected(position: Int) {
                    vibrateTick(requireContext())
                    senderCard = userSumCards[position]
                    scanner?.stopScan(scanCallback)
                    startAdvertising()
                    startScanner()
//                    foundDevice()
                }
            })
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.scanRecord?.bytes?.let {
                val dataString = it.toString(Charsets.UTF_8)
                Log.d("AdvertiseData", "Byte: $it")
                Log.d("AdvertiseData", "Received data: $dataString")
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroyView() {
        super.onDestroyView()
        scanner?.stopScan(scanCallback)
    }


}