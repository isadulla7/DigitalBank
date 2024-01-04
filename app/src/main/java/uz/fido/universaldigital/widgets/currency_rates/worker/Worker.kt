package uz.fido.universaldigital.widgets.currency_rates.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import uz.fido.universaldigital.widgets.currency_rates.retrofit.RatesRepository
import uz.fido.universaldigital.widgets.currency_rates.view.WidgetView
import uz.fido.universaldigital.widgets.currency_rates.widget.RatesWidgetProvider

class Worker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {
        return RatesWidgetProvider().doUpdate(context, RatesRepository(), WidgetView())
    }
}