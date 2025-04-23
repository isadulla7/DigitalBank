package uz.fido.universaldigital.ui.fragments.products.widgets.search

import android.app.Application
import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.fido.universaldigital.base.AbstractViewModel
import uz.fido.universaldigital.ui.fragments.payment.download_payment.database.DatabaseHelper
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_PAYMENT
import uz.fido.universaldigital.ui.fragments.products.widgets.search.SearchList.GROUP_NAME_PAYMENT_GROUP
import uz.fido.universaldigital.ui.fragments.products.widgets.search.model.SearchItem
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(application: Application) : AbstractViewModel(application) {

    private val _uiState = MutableStateFlow<ArrayList<SearchItem>>(arrayListOf())
    val uiState: StateFlow<ArrayList<SearchItem>> = _uiState

    fun setItemList(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            SearchList.searchList = arrayListOf()
            _uiState.emit(arrayListOf())
            val databaseHelper = DatabaseHelper(context)
            SearchList.fillSearchList(context)
            val searchList = SearchList.searchList
            databaseHelper.getGroupList().forEach { paymentGroup ->
                searchList.add(
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
                    searchList.add(
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
            _uiState.emit(searchList)
        }
    }
}