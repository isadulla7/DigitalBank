package uz.fido.universaldigital.ui.fragments.monitoring.local

import androidx.fragment.app.Fragment
import uz.fido.network.domain.model.monitoring.filter.FilterSaveVh
import uz.fido.universaldigital.R

fun Fragment.getOperationType(filterSaveVh: FilterSaveVh): Int {
    return when (filterSaveVh.operationType) {
        getString(R.string.enrollments) -> 0
        getString(R.string.write_offs) -> 1
        else -> 2
    }
}

fun FilterSaveVh.getSelectedCards(): ArrayList<Int> {
    val selectedObjectIds = arrayListOf<Int>()
    val selectedCards = cardList.filter { it.is_selected_monitoring }
    if (selectedCards.isNotEmpty()) {
        cardList.forEach { if (!it.is_selected_monitoring) selectedObjectIds.add(it.object_id) }
    } else {
        cardList.forEach { selectedObjectIds.add(it.object_id) }
    }
    return selectedObjectIds
}

fun FilterSaveVh.getServiceIdsAndPartnerObj(): Pair<ArrayList<Int>, ArrayList<String>> {
    val selectedServiceIds = arrayListOf<Int>()
    val selectedPartnerObj = arrayListOf<String>()
    val serviceIdCheck = serviceList.filter { it.service_current }
    if (serviceIdCheck.isEmpty()) {
        serviceList.forEach { selectedServiceIds.add(it.service_id ?: 0) }
    } else {
        serviceList.forEach { userPayedService ->
            if (userPayedService.service_current) {
                selectedServiceIds.add(userPayedService.service_id ?: 0)
                userPayedService.list.forEach {
                    selectedPartnerObj.add(it.partner_obj)
                }
            }
        }
    }
    return Pair(selectedServiceIds, selectedPartnerObj)
}

fun FilterSaveVh.getMaxAmount(): String? {
    return if (maxAmount.isNotEmpty()) "${
        maxAmount.replace(
            " ",
            ""
        )
    }00" else null
}

fun FilterSaveVh.getMinMinAmount(): String? {
    return if (minAmount.isNotEmpty()) "${
        minAmount.replace(
            " ",
            ""
        )
    }00" else null
}