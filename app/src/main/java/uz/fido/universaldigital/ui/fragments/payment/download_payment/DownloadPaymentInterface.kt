package uz.fido.universaldigital.ui.fragments.payment.download_payment

interface DownloadPaymentInterface {
    fun fetchCompleteFromDB() {}
    fun downloadPaymentSuccess() {}
    fun downloadPaymentPreparing() {}
    fun downloadPaymentFailure() {}
    fun downloadPaymentStart() {}
    fun getMutablePaymentList() {}
}