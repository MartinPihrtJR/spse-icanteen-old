package cz.pihrtm.spseicanteen.ui.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.pihrtm.spseicanteen.GetJson
import cz.pihrtm.spseicanteen.R
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService








class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val b: Button = view.findViewById(R.id.button)
        b.setOnClickListener {
            val repeatTime = 10 //Repeat alarm time in seconds

            val processTimer: AlarmManager? = context?.getSystemService(ALARM_SERVICE) as AlarmManager?
            val intent = Intent(context, GetJson::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            processTimer!!.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                (repeatTime * 1000).toLong(),
                pendingIntent
            )
            Log.i("BTN","BTN1 OK")

        }
        return view
    }

}