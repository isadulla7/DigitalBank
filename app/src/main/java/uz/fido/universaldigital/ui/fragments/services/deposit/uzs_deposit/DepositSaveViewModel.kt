package uz.fido.universaldigital.ui.fragments.services.deposit.uzs_deposit

import android.app.Application
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.fido.network.domain.model.deposits.Deposit
import uz.fido.universaldigital.base.AbstractViewModel
import javax.inject.Inject

@HiltViewModel
class DepositSaveViewModel @Inject constructor(application: Application): AbstractViewModel(application) {

   val depositList=MutableLiveData<ArrayList<Deposit>>()
   var depositCurrent=false

   fun saveDepositList(list:ArrayList<Deposit>){
       depositList.value=list
   }
}