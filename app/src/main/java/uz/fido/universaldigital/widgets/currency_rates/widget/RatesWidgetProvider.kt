package uz.fido.universaldigital.widgets.currency_rates.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.work.ListenableWorker
import uz.fido.nfccardreaderlib.Logger
import uz.fido.universaldigital.widgets.currency_rates.retrofit.Failure
import uz.fido.universaldigital.widgets.currency_rates.retrofit.RatesRepository
import uz.fido.universaldigital.widgets.currency_rates.retrofit.Repository
import uz.fido.universaldigital.widgets.currency_rates.retrofit.Success
import uz.fido.universaldigital.widgets.currency_rates.view.View
import uz.fido.universaldigital.widgets.currency_rates.view.WidgetView


class RatesWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        doUpdate(context, RatesRepository(), WidgetView())
    }

    fun doUpdate(context: Context?, repository: Repository, view: View): ListenableWorker.Result {
        context ?: return ListenableWorker.Result.failure()
        var hasFailed = true
        repository.getData {
            hasFailed = when (it) {
                is Success -> {
                    view.update(context, it.data)
                    false
                }
                is Failure -> {
                    Logger.writeLog("fail "+it.error)
                    true
                }
            }
        }

        return if (hasFailed) ListenableWorker.Result.retry() else ListenableWorker.Result.success()
    }
}