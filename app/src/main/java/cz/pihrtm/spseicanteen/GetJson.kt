package cz.pihrtm.spseicanteen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.StrictMode
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection


/*
kdyz vrati none, spatny prihlasovaci udaje
 */
class GetJson : BroadcastReceiver() {

    private lateinit var json: String
    private lateinit var fulladdr: String
    private lateinit var addr: String
    private lateinit var name:String
    private lateinit var pwd :String
    private var apikey =  1234
    private lateinit var preferences:SharedPreferences
    private lateinit var CHANNEL_ID: String

    override fun onReceive(context: Context?, intent: Intent?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        preferences = context?.getSharedPreferences("update", Context.MODE_PRIVATE)!!
        addr = "https://pihrt.com/spse/jidlo/nacti_jidlo.php?jmeno="
        name = context.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedName", "missing").toString()
        pwd = context.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedPwd", "missing").toString()
        val mode = context.getSharedPreferences("autoOrder", Context.MODE_PRIVATE)?.getInt("mode", 0)
        val repeat = context.getSharedPreferences("repeat",Context.MODE_PRIVATE).getInt("repeat",0)
        val notifPref = context.getSharedPreferences("notif",Context.MODE_PRIVATE)
        val foodPref = context.getSharedPreferences("savedFood",Context.MODE_PRIVATE)
        fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=null"
        createNotificationChannel(context) //create channel for notification
        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_jidla)
                .setContentTitle("My notification")
                .setContentText("Much longer text that cannot fit one line...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (repeat == 4){
            when{
                (mode==0)->{
                    getFood(context)
                }
                (mode==1)->{
                    orderFood(context,3)
                }
                (mode==2)->{
                    orderFood(context, 4)
                }
                (mode==3)->{
                    orderFood(context, 5)
                }
            }
            context.getSharedPreferences("repeat",Context.MODE_PRIVATE).edit().putInt("repeat",0).apply()
        }
        else {
            val replus = context.getSharedPreferences("repeat",Context.MODE_PRIVATE).getInt("repeat",0) + 1
            context.getSharedPreferences("repeat",Context.MODE_PRIVATE).edit().putInt("repeat",replus).apply()
        }
        if (notifPref.getBoolean("newEnabled",false)){
            val hrs = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh")).toString().toInt()
            when (notifPref.getInt("time",10)){
                10 -> {
                    if (hrs==10) {
                        val title = context.getString(R.string.ordered_today)+ foodPref.getString("TodayPopis",context.getString(R.string.noFoodData))
                        val description = foodPref.getString("TodayFood", context.getString(R.string.noFoodData))
                        builder = NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_jidla)
                                .setContentTitle(title)
                                .setContentText(description)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        val notificationId = 852
                        with(NotificationManagerCompat.from(context)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(notificationId, builder.build())
                        }
                    }
                }
                11 -> {
                    if (hrs==11) {
                        val title = context.getString(R.string.ordered_today)+ foodPref.getString("TodayPopis",context.getString(R.string.noFoodData))
                        val description = foodPref.getString("TodayFood", context.getString(R.string.noFoodData))
                        builder = NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_jidla)
                                .setContentTitle(title)
                                .setContentText(description)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        val notificationId = 852
                        with(NotificationManagerCompat.from(context)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(notificationId, builder.build())
                        }
                    }
                }
                12 -> {
                    if (hrs==12) {
                        val title = context.getString(R.string.ordered_today)+ foodPref.getString("TodayPopis",context.getString(R.string.noFoodData))
                        val description = foodPref.getString("TodayFood", context.getString(R.string.noFoodData))
                        builder = NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_jidla)
                                .setContentTitle(title)
                                .setContentText(description)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        val notificationId = 852
                        with(NotificationManagerCompat.from(context)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(notificationId, builder.build())
                        }
                    }
                }
                13 -> {
                    if (hrs==13) {
                        val title = context.getString(R.string.ordered_today)+ foodPref.getString("TodayPopis",context.getString(R.string.noFoodData))
                        val description = foodPref.getString("TodayFood", context.getString(R.string.noFoodData))
                        builder = NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_jidla)
                                .setContentTitle(title)
                                .setContentText(description)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        val notificationId = 852
                        with(NotificationManagerCompat.from(context)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(notificationId, builder.build())
                        }
                    }
                }
            }
        }





    }

    private fun createNotificationChannel(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context?.getString(R.string.channel_name)
            val descriptionText = context?.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            CHANNEL_ID = "SPSEiCanteen"
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun getFood(context: Context?){
        val output = getDataFromUrl(fulladdr).toString()
        val filename = "jidla.json"
        val fileContents = output
        context?.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it?.write(fileContents.toByteArray())
        }
        json = output
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss")
        val lastUpdate = current.format(formatter)
        with(preferences.edit()) {
            putString("lastDate", lastUpdate)
            apply()
        }
        saveToPrefs(context)
    }

    private fun orderFood(context: Context?,mode: Int){
        var output = getDataFromUrl(fulladdr).toString()
        val filename = "jidla.json"
        val fileContents = output
        context?.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it?.write(fileContents?.toByteArray())
        }
        val denvTydnu = LocalDate.now().plusDays(2).dayOfWeek
        val orderEnabled = when {
            (denvTydnu==DayOfWeek.SATURDAY)->{
                false
            }
            (denvTydnu==DayOfWeek.SUNDAY)->{
                false
            }
            else->{
                true
            }
        }
        Log.d("DOW",denvTydnu.toString())
        var datumobednavky:String
        var prikaz = "null"
        if (orderEnabled){
            val mainObject = JSONArray(output)

            if (!JSONObject(mainObject[0].toString()).has("err")){
                val current = LocalDateTime.now().plusDays(2)
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val datumobedu = current.format(formatter)
                val delkajson = mainObject.length() - 1
                for (i in 0..delkajson) {
                    val obed = mainObject.getJSONObject(i)
                    val datum: String = obed.getString("datum")
                    if (datumobedu!==datum){
                        val current = LocalDateTime.now().plusDays(2)
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        datumobednavky = current.format(formatter)
                        prikaz = "$mode,$datumobednavky,make"
                    }
                }
            }
        }
        fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=$prikaz"
        val notifPref = context?.getSharedPreferences("notif",Context.MODE_PRIVATE)
        if (notifPref!!.getBoolean("orderEnabled",false)){
            val title = context.getString(R.string.autoOrdered) + LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            val obed = when (mode) {
                3 -> {"oběd 1"}
                4 -> {"oběd 2"}
                5 -> {"oběd 3"}
                else -> {"oběd"}
            }
            val description = context.getString(R.string.autoOrderedDescr) + obed
            var builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_jidla)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notificationId = 852
            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                notify(notificationId, builder.build())
            }
        }
        output = getDataFromUrl(fulladdr).toString()
        val foodObject = JSONArray(output)
        if (JSONObject(foodObject[0].toString()).has("food")){
            fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=null"
            getFood(context)
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
                        input, "iso-8859-1"
                    ), 8
                )
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

    private fun saveToPrefs(context: Context?) {
        val mainObject = JSONArray(json)
        val foodPreferences = context?.getSharedPreferences("savedFood", Context.MODE_PRIVATE)
        val layoutPreferences = context?.getSharedPreferences("widgetLayout", Context.MODE_PRIVATE)

        if (JSONObject(mainObject[0].toString()).has("err")) {

            var errorType = mainObject[0].toString()
            errorType = JSONObject(errorType).getString("err")
            Log.i("JSONerr", errorType)
            layoutPreferences?.edit()?.putBoolean("widgetHide",true)?.apply()
        } else {
            layoutPreferences?.edit()?.putBoolean("widgetHide",false)?.apply()
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val formatedToday = current.format(formatter)
            val delkajson = mainObject.length() - 1
            for (i in 0..delkajson) {
                val obed = mainObject.getJSONObject(i)
                val datum: String = obed.getString("datum")
                val id: String = obed.getString("popis")
                val jidlo: String = obed.getString("jidlo")
                val polevka: String = obed.getString("polevka")
                if (datum == formatedToday) {
                    if (foodPreferences != null) {
                        with (foodPreferences.edit()) {
                            putString("TodayFood", jidlo)
                            putString("TodaySoup", polevka)
                            putString("TodayPopis", id)
                            apply()
                        }
                    }
                }


            }
        }
    }
}