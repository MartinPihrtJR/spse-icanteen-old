package cz.pihrtm.spseicanteen


import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView



class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    var preferences: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
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









    val intent = Intent(this,GetJson::onReceive.javaClass)
        val alarmManager =
            this.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent =
            PendingIntent.getService(this, 1, intent,
                PendingIntent.FLAG_NO_CREATE)
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
        alarmManager?.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR,
            AlarmManager.INTERVAL_HOUR,
            pendingIntent
        )


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

}