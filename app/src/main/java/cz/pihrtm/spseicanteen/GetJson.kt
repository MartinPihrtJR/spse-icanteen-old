package cz.pihrtm.spseicanteen

import android.util.Log
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


class GetJson : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("JSON","OK")
        val addr = "https://pihrt.com/spse/jidlo/nacti_jidlo.php?jmeno="
        val name = context?.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedName","missing")
        val pwd = context?.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedPwd","missing")
        val apikey = 1234
        Log.i("CREDS","jméno: " + name)
        Log.i("CREDS","heslo " + pwd)
        Log.i("FULLURL", addr + name +"&heslo="+pwd+"&api="+apikey)
    }

}