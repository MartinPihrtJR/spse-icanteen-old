package cz.pihrtm.spseicanteen


import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import cz.pihrtm.spseicanteen.ui.home.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var preferences: SharedPreferences? = null
    private var view = this
    private lateinit var nextLayout: ConstraintLayout
    private lateinit var todayLayout: ConstraintLayout
    private lateinit var errorText: TextView
    private lateinit var titleFoodToday: TextView
    private lateinit var titleSoupToday: TextView
    private lateinit var titleFoodTomorrow: TextView
    private lateinit var titleFood2Tomorrow: TextView
    private lateinit var lastDate: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val refreshButton: Button = findViewById(R.id.nav_refresh)
        val updatejob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + updatejob)
        view = this
        nextLayout = view.findViewById(R.id.foodNext)
        todayLayout = view.findViewById(R.id.foodToday)
        errorText= view.findViewById(R.id.foodError)
        titleFoodToday = view.findViewById(R.id.titleFoodToday)
        titleSoupToday = view.findViewById(R.id.titleSoupToday)
        titleFoodTomorrow= view.findViewById(R.id.titleFoodTomorrow)
        titleFood2Tomorrow = view.findViewById(R.id.titleFood2Tomorrow)
        lastDate = view.findViewById(R.id.lastUpdated)

        preferences = getPreferences(Context.MODE_PRIVATE)
        appBarConfiguration = AppBarConfiguration(
                setOf(
                        R.id.nav_home,
                        R.id.nav_gallery,
                        R.id.nav_slideshow,
                        R.id.nav_user,
                ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        //**********************************************************************************
        //dalsi kod tady

        if (this.getSharedPreferences("first", Context.MODE_PRIVATE).getBoolean("isFirst", true)){
            val intentFirst = Intent(this, FirstSetup::class.java)
            startActivity(intentFirst)
            Handler().postDelayed({
                replaceUserFragment()
            }, 500)
        }
        else{
            if (this.getSharedPreferences("creds", Context.MODE_PRIVATE).getString("savedName", null) != null){

                val repeatTime = 3600 //Repeat alarm time in seconds

                val processTimer: AlarmManager? = this.getSystemService(ALARM_SERVICE) as AlarmManager?
                val intent = Intent(this, GetJson::class.java)
                val pendingIntent =
                        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                processTimer!!.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(),
                        (repeatTime * 1000).toLong(),
                        pendingIntent
                )
            }
        }

        refreshButton.setOnClickListener {
            uiScope.launch(Dispatchers.IO) {
                getJsonOnetime(this@MainActivity)
            }
            refreshButton.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.rotate360x2) );
            Toast.makeText(this,getString(R.string.action_updating),Toast.LENGTH_LONG).show()
            //aktualizace UI
            Handler().postDelayed({
                updateUI(this)
            }, 10000)
        }



        //TODO kdyz neni internet, vypiseme chybu (Toast)



    }
    private fun replaceUserFragment() {
        Log.i("replaceFragment", "OK")
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.menu.getItem(0).isChecked = false;
        navView.menu.getItem(3).isChecked = true;
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.nav_user)
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val userTextView: TextView = findViewById(R.id.userTextView)
        if (navView.menu.getItem(3).isChecked){
            hideKeyboard()}
        userTextView.text = getSharedPreferences("creds", Context.MODE_PRIVATE).getString("savedName",getString(R.string.notLoggedIn))
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun getJsonOnetime(context: Context?){
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        val preferences = context?.getSharedPreferences("update",Context.MODE_PRIVATE)
        StrictMode.setThreadPolicy(policy)
        Log.d("JSON", "OK")
        val addr = "https://pihrt.com/spse/jidlo/nacti_jidlo.php?jmeno="
        val name = context?.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedName", "missing")
        val pwd = context?.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedPwd", "missing")
        val objednej = context?.getSharedPreferences("objednavkySettings", Context.MODE_PRIVATE)?.getString("objednej", "null")
        val apikey = 1234
        var fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=$objednej"
        var output: String = getDataFromUrl(fulladdr).toString()
        Log.i("DATAint", output)
        val mainObject = JSONArray(output)
        val delkajson = mainObject.length()-1
        Log.i("JSONcontent",output)
        val filename = "jidla.json"
        val fileContents = output
        context?.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it?.write(fileContents?.toByteArray())
        }
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss")
        val lastUpdate = current.format(formatter)
        if (preferences != null) {
            with (preferences.edit()) {
                putString("lastDate", lastUpdate)
                apply()
            }
        }


    }

    var error = "" // string field

    private fun getDataFromUrl(demoIdUrl: String): String? {
        var result: String? = null
        val resCode: Int
        val input: InputStream
        try {
            val url = URL(demoIdUrl)
            val urlConn: URLConnection = url.openConnection()
            val httpsConn: HttpsURLConnection = urlConn as HttpsURLConnection
            httpsConn.allowUserInteraction = false
            httpsConn.instanceFollowRedirects = true
            httpsConn.requestMethod = "GET"
            httpsConn.connect()
            resCode = httpsConn.responseCode
            if (resCode == HttpURLConnection.HTTP_OK) {
                input = httpsConn.inputStream
                val reader = BufferedReader(
                    InputStreamReader(
                    input, "iso-8859-1"), 8)
                val sb = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line).append("\n")
                }
                input.close()
                result = sb.toString()
            } else {
                error += resCode
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun updateUI(context: Context?){
        if (context?.getSharedPreferences("first", Context.MODE_PRIVATE)?.getBoolean("isFirst",true)==true){
            //nic
        }
        else {
            var json = context?.openFileInput("jidla.json")?.bufferedReader()?.readLines().toString() //read
            json = json.subSequence(1, json.length - 1).toString() //convert output back to string, it returns [string]
            Log.d("JSON", json)
            var mainObject = JSONArray(json)
            val foodPreferences = context?.getSharedPreferences("savedFood", Context.MODE_PRIVATE)
            val layoutPreferences = context?.getSharedPreferences("widgetLayout", Context.MODE_PRIVATE)
            if (JSONObject(mainObject[0].toString()).has("err")) {
                layoutPreferences?.edit()?.putBoolean("widgetHide",true)?.apply()
                var errorType = mainObject[0].toString()
                errorType = JSONObject(errorType).getString("err")
                Log.i("JSONerr", errorType)
                nextLayout.visibility = View.GONE
                todayLayout.visibility = View.GONE
                if (errorType == "strava empty") {
                    errorText.visibility = View.VISIBLE
                    errorText.text = getString(R.string.stravaEmpty)
                } else if (errorType == "loading food in json error") {
                    errorText.visibility = View.VISIBLE
                    errorText.text = getString(R.string.loadingFoodInJsonError)
                }
                else if (errorType == "not created") {
                    errorText.visibility = View.VISIBLE
                    errorText.text = getString(R.string.notLoggedIn)
                }
                else {
                    errorText.visibility = View.VISIBLE
                    errorText.text = getString(R.string.unknownError)
                }

            } else {
                layoutPreferences?.edit()?.putBoolean("widgetHide",false)?.apply()
                nextLayout.visibility = View.VISIBLE
                todayLayout.visibility = View.VISIBLE
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val formatedToday = current.format(formatter)
                val tomorrow = LocalDateTime.now().plusDays(1)
                val formatedTomorrow = tomorrow.format(formatter)
                val totomorrow = LocalDateTime.now().plusDays(2)
                val formatedToTomorrow = totomorrow.format(formatter)
                val delkajson = mainObject.length() - 1
                for (i in 0..delkajson) {
                    val obed = mainObject.getJSONObject(i)
                    val datum: String = obed.getString("datum")
                    val jidlo: String = obed.getString("jidlo")
                    val id: String = obed.getString("popis")
                    val polevka: String = obed.getString("polevka")
                    if (datum == formatedToday) {
                        titleFoodToday.text = jidlo
                        titleSoupToday.text = polevka
                        if (foodPreferences != null) {
                            with (foodPreferences.edit()) {
                                putString("TodayFood", jidlo)
                                putString("TodaySoup", polevka)
                                putString("TodayPopis", id)
                                apply()
                            }
                        }
                    }
                    if (datum == formatedTomorrow) {
                        titleFoodTomorrow.text = jidlo
                    }
                    if (datum == formatedToTomorrow) {
                        titleFood2Tomorrow.text = jidlo
                    }


                }
                lastDate.text = context?.getSharedPreferences("update", Context.MODE_PRIVATE)
                    ?.getString("lastDate", getString(R.string.notYetUpdated))

            }
        }
    }
}
