package uz.fido.universaldigital.widgets.currency_rates.view

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import uz.fido.network.domain.model.rates.CourseItem
import uz.fido.universaldigital.R
import uz.fido.universaldigital.ui.activities.LoginActivity
import uz.fido.universaldigital.ui.fragments.products.MenuHomeFragment
import uz.fido.universaldigital.widgets.currency_rates.widget.RatesWidgetProvider
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_EUR
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_RUB
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_USD
import uz.fido.utils.const.CurrencyConst.CURRENCY_CODE_UZS
import uz.fido.utils.format.Format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WidgetView : View {

    override fun update(context: Context, data: ArrayList<CourseItem>) {
        val views = RemoteViews(context.packageName, R.layout.currency_rates_widget)

        // Set updated date
//        val dateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
//        val updatedDate = context.getString(R.string.updated_at) + " " + dateFormat.format(Date())
//        views.setTextViewText(R.id.tv_updated_date, updatedDate)

        // Set Currency Rates
        val rates = currencyRatesFilter(data)
        if (rates.size == 3) {
            val usdRate = rates[0]
            val eurRate = rates[1]
            val rubRate = rates[2]
            //set USD
            views.setTextViewText(R.id.buying_rate1, formattedRate(usdRate.buyingRate))
            views.setTextViewText(R.id.selling_rate1, formattedRate(usdRate.sellingRate))
            //set EUR
            views.setTextViewText(R.id.buying_rate2, formattedRate(eurRate.buyingRate))
            views.setTextViewText(R.id.selling_rate2, formattedRate(eurRate.sellingRate))
            //set RUB
            views.setTextViewText(R.id.buying_rate, formattedRate(rubRate.buyingRate))
            views.setTextViewText(R.id.selling_rate, formattedRate(rubRate.sellingRate))
        }

        // Handle click widget event
        val intent = Intent(context, LoginActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        views.setOnClickPendingIntent(R.id.currency_rate_father, pendingIntent)

        val thisWidget = ComponentName(context, RatesWidgetProvider::class.java)
        AppWidgetManager.getInstance(context).apply {
            updateAppWidget(thisWidget, views)
        }
    }

    private fun formattedRate(rate: Double) =
        Format.formatAmount(Format.formatAmountToTiyn((rate / 100).toString())) + " UZS"

    private fun currencyRatesFilter(courseList: ArrayList<CourseItem>): ArrayList<CourseItem> {
        val filteredList = ArrayList<CourseItem>()
        courseList.forEach { courseItem ->
            if (courseItem.quote_currency == CURRENCY_CODE_UZS) {
                when (courseItem.currencyCode) {
                    CURRENCY_CODE_USD -> {
                        MenuHomeFragment.sellingRate = courseItem.sellingRate
                        MenuHomeFragment.buyingRateDollar = courseItem.buyingRate
                        courseItem.order = 1
                        filteredList.add(courseItem)
                    }

                    CURRENCY_CODE_EUR -> {
                        MenuHomeFragment.sellingRateEur = courseItem.buyingRate
                        courseItem.order = 2
                        filteredList.add(courseItem)
                    }

                    CURRENCY_CODE_RUB -> {
                        MenuHomeFragment.sellingRateRub = courseItem.buyingRate
                        courseItem.order = 3
                        filteredList.add(courseItem)
                    }
                }
            }
        }
        filteredList.sortWith { o1, o2 ->
            val or1: Int = o1.order
            val or2: Int = o2.order
            or1.compareTo(or2)
        }
        return filteredList
    }

}