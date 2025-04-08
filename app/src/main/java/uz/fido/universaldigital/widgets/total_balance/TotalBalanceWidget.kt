package uz.fido.universaldigital.widgets.total_balance

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.utils.const.Const
import uz.fido.utils.security.getFromSecureStore

class TotalBalanceWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.total_balance_widget_layout)
        setLastUpdatedDate(views)
        setClickEvents(context, views)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }


    private fun setLastUpdatedDate(views: RemoteViews) {
        val updatedDate = getFromSecureStore(Const.TOTAL_BALANCE_UPDATED_AT)
        views.setTextViewText(R.id.updated_on, updatedDate)
    }

    private fun setClickEvents(context: Context, views: RemoteViews) {
        val intent = Intent(context, LoginActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.total_balance_father, pendingIntent)
    }
}