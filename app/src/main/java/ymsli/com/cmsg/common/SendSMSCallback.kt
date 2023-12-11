package ymsli.com.cmsg.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class SendSMSCallback(): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        if(intent.extras?.get("sms_update")!=null){
            val configReceived = intent.getLongExtra("sms_update",-1)
            val smsStatus = intent.getStringExtra("sms_status")
            if(configReceived>0) {
                intent.putExtra("sms_update", configReceived)
                intent.putExtra("sms_status", smsStatus)
                ObservableObject.getInstance().updateValue(intent)
            }
        }
    }
}