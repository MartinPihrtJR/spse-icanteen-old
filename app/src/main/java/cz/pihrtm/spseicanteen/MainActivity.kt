package cz.pihrtm.spseicanteen


import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var preferences: SharedPreferences? = null
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
        val internetPreferences = getSharedPreferences("internet", Context.MODE_PRIVATE)
        internetPreferences.edit().putBoolean("net",isOnline()).apply()
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
            Handler(Looper.getMainLooper()).postDelayed({
                replaceUserFragment()
            }, 500)
        }
        else{
            if (this.getSharedPreferences("creds", Context.MODE_PRIVATE).getString("savedName", null) != null){

                val repeatTime = 1200 //Repeat alarm time in seconds - 20 min

                val processTimer: AlarmManager? = this.getSystemService(ALARM_SERVICE) as AlarmManager?
                val intent = Intent(this, GetJson::class.java)
                val pendingIntent =
                        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                processTimer!!.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(),
                        (repeatTime * 1000).toLong(),
                        pendingIntent
                )
            }
            if (!internetPreferences.getBoolean("net",false)){
                Toast.makeText(this,getString(R.string.noInternet),Toast.LENGTH_LONG).show()
            }
        }


        refreshButton.setOnClickListener {
            internetPreferences.edit().putBoolean("net",isOnline()).apply()
            if (internetPreferences.getBoolean("net",false)){
                if(this.getSharedPreferences("creds", Context.MODE_PRIVATE).getString("savedName", "missing") != "missing"){
                    uiScope.launch(Dispatchers.IO) {
                        GetJson().getFood(this@MainActivity)

                    }
                }
            refreshButton.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.rotate360x2) )
            Toast.makeText(this,getString(R.string.action_updating),Toast.LENGTH_LONG).show()
            //aktualizace UI
            Handler(Looper.getMainLooper()).postDelayed({
                updateUI(this)
            }, 10000)
            }
            else {
                Toast.makeText(this,getString(R.string.noInternet),Toast.LENGTH_LONG).show()
            }
        }



    }

    private fun replaceUserFragment() {
        Log.d("replaceFragment", "OK")
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.menu.getItem(0).isChecked = false
        navView.menu.getItem(3).isChecked = true
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



    private fun updateUI(context: Context?){
        if (context?.getSharedPreferences("first", Context.MODE_PRIVATE)?.getBoolean("isFirst",true)==true){
            //nic
        }
        else {
            var json = context?.openFileInput("jidla.json")?.bufferedReader()?.readLines().toString() //read
            if (json !== "null") {
                json = json.subSequence(1, json.length - 1).toString() //convert output back to string, it returns [string]
                try {
                    val mainObject = JSONArray(json)
                    val foodPreferences = context?.getSharedPreferences("savedFood", Context.MODE_PRIVATE)
                    val layoutPreferences = context?.getSharedPreferences("widgetLayout", Context.MODE_PRIVATE)
                    if (JSONObject(mainObject[0].toString()).has("err")) {
                        layoutPreferences?.edit()?.putBoolean("widgetHide", true)?.apply()
                        var errorType = mainObject[0].toString()
                        errorType = JSONObject(errorType).getString("err")
                        Log.d("JSONerr", errorType)
                        nextLayout.visibility = View.GONE
                        todayLayout.visibility = View.GONE
                        when (errorType) {
                            "strava empty" -> {
                                errorText.visibility = View.VISIBLE
                                errorText.text = getString(R.string.stravaEmpty)
                            }
                            "loading food in json error" -> {
                                errorText.visibility = View.VISIBLE
                                errorText.text = getString(R.string.loadingFoodInJsonError)
                            }
                            "not created" -> {
                                errorText.visibility = View.VISIBLE
                                errorText.text = getString(R.string.notLoggedIn)
                            }
                            else -> {
                                errorText.visibility = View.VISIBLE
                                errorText.text = getString(R.string.unknownError)
                            }
                        }

                    } else {
                        layoutPreferences?.edit()?.putBoolean("widgetHide", false)?.apply()
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
                            var wasFound = false
                            val obed = mainObject.getJSONObject(i)
                            val datum: String = obed.getString("datum")
                            val jidlo: String = obed.getString("jidlo")
                            val id: String = obed.getString("popis")
                            val polevka: String = obed.getString("polevka")
                            if (datum == formatedToday) {
                                titleFoodToday.text = jidlo
                                titleSoupToday.text = polevka
                                if (foodPreferences != null) {
                                    with(foodPreferences.edit()) {
                                        putString("TodayFood", jidlo)
                                        putString("TodaySoup", polevka)
                                        putString("TodayPopis", id)
                                        putBoolean("TodayNotif", true)
                                        apply()
                                    }
                                }
                                wasFound = true
                            }
                            if (datum == formatedTomorrow) {
                                titleFoodTomorrow.text = jidlo
                            }
                            if (datum == formatedToTomorrow) {
                                titleFood2Tomorrow.text = jidlo
                            }
                            if (i == delkajson && !wasFound){
                                titleFoodToday.text = getString(R.string.noFoodData)
                                titleSoupToday.text = getString(R.string.noSoupData)
                                if (foodPreferences != null) {
                                    with(foodPreferences.edit()) {
                                        putString("TodayFood", getString(R.string.noFoodData))
                                        putString("TodaySoup", getString(R.string.noSoupData))
                                        putString("TodayPopis", getString(R.string.notif_noIdData))
                                        putBoolean("TodayNotif", false)
                                        apply()
                                    }
                                }
                                titleFoodTomorrow.text = getString(R.string.noFoodData)
                                titleFood2Tomorrow.text = getString(R.string.noFoodData)
                            }


                        }
                        lastDate.text = context?.getSharedPreferences("update", Context.MODE_PRIVATE)
                                ?.getString("lastDate", getString(R.string.notYetUpdated))
                        layoutPreferences?.edit()?.putString("lastUpdate", lastDate.text.toString())?.apply()

                        val navView: NavigationView = findViewById(R.id.nav_view)
                        navView.menu.getItem(0).isChecked = false
                        navView.menu.getItem(0).isChecked = true
                        val navController = findNavController(R.id.nav_host_fragment)
                        navController.navigate(R.id.nav_home)
                    }

                } catch (e: Exception){
                    Log.d("UPDATEUI", "Error: $e")
                }
                try {
                    val intent = Intent(this@MainActivity, AppWidget::class.java)
                    intent.action = "android.appwidget.action.APPWIDGET_UPDATE"
                    val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
                        ComponentName(
                            application,
                            AppWidget::class.java
                        )
                    )
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    sendBroadcast(intent)
                } catch (e: Exception){
                    Log.d("WidgetUpdateERR", e.toString())
                }
            }
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
        return false
    }
}
