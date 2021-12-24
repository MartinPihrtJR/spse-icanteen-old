package cz.pihrtm.spseicanteen.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cz.pihrtm.spseicanteen.FirstSetup
import cz.pihrtm.spseicanteen.R
import cz.pihrtm.spseicanteen.ui.user.UserFragment


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
        val firstopenbtn: Button = view.findViewById(R.id.openloginbtn)
        var jidla: String
        firstopenbtn.setOnClickListener {
            val intentus = Intent(context, FirstSetup::class.java)
            startActivity(intentus)
        }
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
                Log.i("FILES",files[0])
            }

            /*Log.i("BTN","BTN1 OK")
            context?.openFileInput("jidla.json")?.bufferedReader()?.useLines { lines ->
                jidla = lines.toString()
            }
            Log.i("JSON", jidla)*/

        }
        save.setOnClickListener {
            val filename = "jidla.json"
            val fileContents = "funguje"
            context?.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it?.write(fileContents?.toByteArray())
            }
        }

        laod.setOnClickListener {
            jidla = context?.openFileInput("jidla.json")?.bufferedReader()?.readLines().toString() //read
            jidla = jidla.subSequence(1 , jidla.length-1).toString() //convert output back to string, it returns [string]
            Log.i("JSON", jidla)
        }
//TODO pri zalozeni uzivatele se vytvori prazdny json a stahne se zradlo, pri smazani se smaze a vytvori znova prazdej


        return view
    }

}