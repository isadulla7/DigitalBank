package uz.fido.universaldigital.ui.utils.extensions

import android.content.Context
import uz.fido.universaldigital.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Context.getFormattedDate(date: String): String {
    if (date.length == 19) {
        val formattedDate = date.dropLast(3)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return try {
            val parsedDate: Date = dateFormat.parse(formattedDate) as Date
            when {
                parsedDate >= today.time -> getString(R.string.today) + ", ${timeFormat.format(parsedDate)}"
                parsedDate >= yesterday.time && parsedDate < today.time -> getString(R.string.yesterday) + ", ${timeFormat.format(parsedDate)}"
                else -> formattedDate
            }
        } catch (e: Exception) {
            return formattedDate
        }
    } else {
        return date
    }
}