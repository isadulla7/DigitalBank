package uz.fido.universaldigital.ui.fragments.payment.download_payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.paperdb.Book
import io.paperdb.Paper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.fido.network.data.utility.Status
import uz.fido.network.domain.model.payment.Payment
import uz.fido.network.domain.model.payment.PaymentGroup
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_PAYMENT
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_PAYMENT_GROUP
import uz.fido.universaldigital.ui.fragments.products.widgets.search.model.SearchItem
import uz.fido.utils.const.Const

@AndroidEntryPoint
abstract class DownloadPayment : Fragment() {

    lateinit var downloadPaymentViewModel: DownloadPaymentViewModel
    private lateinit var downloadPaymentInterface: DownloadPaymentInterface

    var databaseHelper: DatabaseHelper? = null
    var paymentGroupsList: ArrayList<PaymentGroup> = ArrayList()
     var updateLang:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        downloadPaymentViewModel =
            ViewModelProvider(requireActivity())[DownloadPaymentViewModel::class.java]
        databaseHelper = DatabaseHelper(requireContext())
    }



  fun checkLang() {
        try {
            updateLang= Paper.book().read(Const.UPDATE_LANG)
        }catch (e:Exception){
            updateLang=false
        }
    }


    fun setPaymentStatusListener(listener: DownloadPaymentInterface) {
        this.downloadPaymentInterface = listener
    }

    fun checkForPaymentDownload() {

        val currentDatabaseVersion = Paper.book().read(Const.PAPER_PAYMENT_VERSION, "0")
        val savedDatabaseVersion = Paper.book().read(Const.PAPER_PAYMENT_VERSION_DB, "0")
        if (downloadPaymentViewModel.paymentGroupMutableList.value != null && downloadPaymentViewModel.paymentGroupMutableList.value!!.size != 0 && !updateLang
        ) {
            downloadPaymentInterface.getMutablePaymentList()
        } else {
            Paper.book().write(Const.UPDATE_LANG,false)
            if (currentDatabaseVersion == savedDatabaseVersion) {
                getPaymentsFromLocal()
            } else {
                downloadPayments()
            }
        }
    }

     private fun getPaymentsFromLocal() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            databaseHelper?.let { databaseHelper ->
                if (databaseHelper.getGroupList().size != 0) {
                    paymentGroupsList = databaseHelper.getGroupList()
                    paymentGroupsList.sortBy { it.order }
                    withContext(Dispatchers.Main) {
                        downloadPaymentInterface.fetchCompleteFromDB()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        downloadPayments()
                    }
                }
            }
        }
    }

    private fun downloadPayments() {
        downloadPaymentInterface.downloadPaymentStart()
        downloadPaymentViewModel.downloadPayment().observe(viewLifecycleOwner) {
            if (it.status == Status.SUCCESS) {
                downloadPaymentInterface.downloadPaymentPreparing()
                try {
                    databaseHelper?.clearAll()
                    setToStorage(it.data as Payment)
                } catch (e: Exception) {
                    downloadPaymentInterface.downloadPaymentFailure()
                }
            } else {
                downloadPaymentInterface.downloadPaymentFailure()
            }
        }
    }

    private fun setToStorage(payment: Payment) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            databaseHelper?.let { databaseHelper ->
                databaseHelper.insertServiceGroups(payment.service_groups ?: ArrayList())
                databaseHelper.insertServiceList(payment.service_list ?: ArrayList())
                databaseHelper.insertPaymentParams(payment.payment_details ?: ArrayList())
                databaseHelper.insertCashbackList(payment.cashback_list ?: ArrayList())
                databaseHelper.insertReferenceList(payment.references_list ?: ArrayList())
                paymentGroupsList = databaseHelper.getGroupList()
                withContext(Dispatchers.Main){
                downloadPaymentInterface.downloadPaymentSuccess()
                }
                Paper.book().write(Const.PAPER_PAYMENT_VERSION_DB, payment.curr_version ?: "0")
            }
        }
    }

  /*  fun fillSearchList() {
        val paymentService = ArrayList<SearchItem>()
        val paymentGroups = ArrayList<SearchItem>()
        paymentGroupsList.forEach { paymentGroup ->
            paymentGroups.add(
                SearchItem(
                    null,
                    paymentGroup,
                    paymentGroup.name,
                    paymentGroup.group_code,
                    GROUP_NAME_PAYMENT_GROUP,
                    paymentGroup.service_group_code
                )
            )
            paymentGroup.service_list!!.forEach {
                paymentService.add(
                    SearchItem(
                        it,
                        paymentGroup,
                        it.nameIndex,
                        it.icon_name,
                        GROUP_NAME_PAYMENT,
                        it.service_id.toString()
                    )
                )
            }
        }
        SearchList.addList(paymentService)
        SearchList.addList(paymentGroups)
        SearchList.saveSearchList(requireActivity())
    }*/

}