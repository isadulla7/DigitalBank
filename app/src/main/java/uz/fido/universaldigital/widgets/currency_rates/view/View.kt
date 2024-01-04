package uz.fido.universaldigital.widgets.currency_rates.view

import android.content.Context
import uz.fido.network.domain.model.rates.CourseItem

interface View {
    fun update(context: Context, data: ArrayList<CourseItem>)
}