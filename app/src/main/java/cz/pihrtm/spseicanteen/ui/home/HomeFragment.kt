package cz.pihrtm.spseicanteen.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.pihrtm.spseicanteen.R


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val button: Button = view.findViewById(R.id.button)
        val save: Button = view.findViewById(R.id.buttonsave)
        val laod: Button = view.findViewById(R.id.buttonload)
        var jidla = ""
        button.setOnClickListener {
            /*val repeatTime = 10 //Repeat alarm time in seconds

            val processTimer: AlarmManager? = context?.getSystemService(ALARM_SERVICE) as AlarmManager?
            val intent = Intent(context, GetJson::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            processTimer!!.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                (repeatTime * 1000).toLong(),
                pendingIntent
            )*/
            var files: Array<String> = requireContext().fileList()
            if (files.isEmpty()){
                Log.i("FILES","ERR")
            }
            else{
                Log.i("FILES",files[0].toString())
            }

            /*Log.i("BTN","BTN1 OK")
            context?.openFileInput("jidla.json")?.bufferedReader()?.useLines { lines ->
                jidla = lines.toString()
            }
            Log.i("JSON", jidla)*/

        }
        save.setOnClickListener {
            val filename = "jidla.json"
            val fileContents = "test"
            context?.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it?.write(fileContents?.toByteArray())
            }
        }

        laod.setOnClickListener {
            context?.openFileInput("jidla.json")?.bufferedReader()?.readLines()
            Log.i("JSON", jidla)
        }


        return view
    }

}