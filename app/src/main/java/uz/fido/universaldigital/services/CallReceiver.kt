package uz.fido.universaldigital.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log


public class CallReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
       Log.d("TAG", "CallReceiver: ---------- ")
        if (intent!=null){
            if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)){
               Log.d("TAG", "CallReceiver:$-- ishladi")
                val  state = intent!!.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
                Log.d("TAG", "CallReceiver:$--  Qo'ng'iroq bo'layapti")
                   // onCLick(CallReceiverEnum.STATE_RINGING)
                }else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
                 //   onCLick(CallReceiverEnum.STATE_OFFHOOK)
                  Log.d("TAG", "CallReceiver:$-- Qo'ng'iroq jarayoni davom etmoqda")
                } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
                  //  onCLick(CallReceiverEnum.STATE_IDLE)
                  Log.d("TAG", "CallReceiver:$-- Qo'ng'iroq tugadi yoki rad etildi")

                }
            }
        }

    }
}