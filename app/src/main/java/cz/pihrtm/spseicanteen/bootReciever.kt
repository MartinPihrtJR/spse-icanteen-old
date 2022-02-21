package cz.pihrtm.spseicanteen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity


class MyBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        scheduleAlarm(context)
    }

    private fun scheduleAlarm(ctxt: Context,) {
        if (!ctxt.getSharedPreferences("first", Context.MODE_PRIVATE).getBoolean("isFirst", true)) {
            if (ctxt.getSharedPreferences("creds", Context.MODE_PRIVATE).getString("savedName", null) != null){

                val repeatTime = 1200 //Repeat alarm time in seconds - 20 min

                val processTimer: AlarmManager? = ctxt.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager?
                val intent = Intent(ctxt, GetJson::class.java)
                val pendingIntent =
                    PendingIntent.getBroadcast(ctxt, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                processTimer!!.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(),
                    (repeatTime * 1000).toLong(),
                    pendingIntent
                )
            }

        }
    }

}