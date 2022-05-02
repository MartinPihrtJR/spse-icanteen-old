package cz.pihrtm.spseicanteen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.StrictMode
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
    private var apikey =  "uIS0TDs8FumqtMWGG1wp"
    private lateinit var preferences:SharedPreferences
    private lateinit var channelId: String

    override fun onReceive(context: Context?, intent: Intent?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        preferences = context?.getSharedPreferences("update", Context.MODE_PRIVATE)!!
        addr = "https://jidlo.pihrt.com/nacti_jidlo.php?jmeno="
        name = context.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedName", "missing").toString()
        pwd = context.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedPwd", "missing").toString()
        val mode = context.getSharedPreferences("autoOrder", Context.MODE_PRIVATE)?.getInt("mode", 0)
        val repeat = context.getSharedPreferences("repeat",Context.MODE_PRIVATE).getInt("repeat",0)
        val notifPref = context.getSharedPreferences("notif",Context.MODE_PRIVATE)
        val foodPref = context.getSharedPreferences("savedFood",Context.MODE_PRIVATE)
        val internetPreferences = context.getSharedPreferences("internet",Context.MODE_PRIVATE)
        val builder: NotificationCompat.Builder
        val verze = BuildConfig.VERSION_NAME
        fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=null&ver=$verze"
        createNotificationChannel(context) //create channel for notification
        fun isOnline(): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

        internetPreferences.edit().putBoolean("net",isOnline()).apply()

        if (repeat == 4){
            when{
                (mode==0)->{
                    if (context.getSharedPreferences("creds", Context.MODE_PRIVATE).getString("savedName","missing") != "missing"){
                        if (internetPreferences.getBoolean("net",false)){
                            getFood(context)
                            getFoodList(context)
                        }
                    }

                }
                (mode==1)->{
                    if (context.getSharedPreferences("creds", Context.MODE_PRIVATE).getString("savedName","missing") != "missing"){
                        if (internetPreferences.getBoolean("net",false)) {
                            orderFood(context, 3)
                            getFoodList(context)
                        }
                    }

                }
                (mode==2)->{
                    if (context.getSharedPreferences("creds", Context.MODE_PRIVATE).getString("savedName","missing") != "missing"){
                        if (internetPreferences.getBoolean("net",false)){
                            orderFood(context, 4)
                            getFoodList(context)
                        }
                    }

                }
                (mode==3)->{
                    if (context.getSharedPreferences("creds", Context.MODE_PRIVATE).getString("savedName","missing") != "missing"){
                        if (internetPreferences.getBoolean("net",false)) {
                            orderFood(context, 5)
                            getFoodList(context)
                        }
                    }

                }
            }
            if (internetPreferences.getBoolean("net",false)){
                getFoodList(context)
            }
            context.getSharedPreferences("repeat",Context.MODE_PRIVATE).edit().putInt("repeat",0).apply()
        }
        else {
            val replus = context.getSharedPreferences("repeat",Context.MODE_PRIVATE).getInt("repeat",0) + 1
            context.getSharedPreferences("repeat",Context.MODE_PRIVATE).edit().putInt("repeat",replus).apply()
        }


        if (foodPref.getBoolean("TodayNotif", false)) {
            if (notifPref.getBoolean("newEnabled",false)){
                val hrs = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH")).toInt()
                val denvTydnu = LocalDate.now().dayOfWeek
                Log.d("DoW", denvTydnu.toString())
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
                if (orderEnabled) {
                    when (notifPref.getInt("time", 10)) {
                        10 -> {
                            if (hrs == 10) {
                                val title =
                                    context.getString(R.string.ordered_today) + " " + foodPref.getString(
                                        "TodayPopis",
                                        context.getString(R.string.noPopis)
                                    )
                                val description = foodPref.getString(
                                    "TodayFood",
                                    context.getString(R.string.noFoodData)
                                )
                                val notificationIntent = Intent(context, MainActivity::class.java)
                                val intentNotif = PendingIntent.getActivity(
                                    context, 0,
                                    notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                                builder = NotificationCompat.Builder(context, channelId)
                                    .setSmallIcon(R.drawable.ic_jidla)
                                    .setContentTitle(title)
                                    .setContentText(description)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(intentNotif)
                                val notificationId = 852
                                with(NotificationManagerCompat.from(context)) {
                                    // notificationId is a unique int for each notification that you must define
                                    notify(notificationId, builder.build())
                                }
                            }
                        }
                        11 -> {
                            if (hrs == 11) {
                                val title =
                                    context.getString(R.string.ordered_today) + " " + foodPref.getString(
                                        "TodayPopis",
                                        context.getString(R.string.noPopis)
                                    )
                                val description = foodPref.getString(
                                    "TodayFood",
                                    context.getString(R.string.noFoodData)
                                )
                                val notificationIntent = Intent(context, MainActivity::class.java)
                                val intentNotif = PendingIntent.getActivity(
                                    context, 0,
                                    notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                                builder = NotificationCompat.Builder(context, channelId)
                                    .setSmallIcon(R.drawable.ic_jidla)
                                    .setContentTitle(title)
                                    .setContentText(description)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(intentNotif)
                                val notificationId = 852
                                with(NotificationManagerCompat.from(context)) {
                                    // notificationId is a unique int for each notification that you must define
                                    notify(notificationId, builder.build())
                                }
                            }
                        }
                        12 -> {
                            if (hrs == 12) {
                                val title =
                                    context.getString(R.string.ordered_today) + " " + foodPref.getString(
                                        "TodayPopis",
                                        context.getString(R.string.noPopis)
                                    )
                                val description = foodPref.getString(
                                    "TodayFood",
                                    context.getString(R.string.noFoodData)
                                )
                                val notificationIntent = Intent(context, MainActivity::class.java)
                                val intentNotif = PendingIntent.getActivity(
                                    context, 0,
                                    notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                                builder = NotificationCompat.Builder(context, channelId)
                                    .setSmallIcon(R.drawable.ic_jidla)
                                    .setContentTitle(title)
                                    .setContentText(description)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(intentNotif)
                                val notificationId = 852
                                with(NotificationManagerCompat.from(context)) {
                                    // notificationId is a unique int for each notification that you must define
                                    notify(notificationId, builder.build())
                                }
                            }
                        }
                        13 -> {
                            if (hrs == 13) {
                                val title =
                                    context.getString(R.string.ordered_today) + " " + foodPref.getString(
                                        "TodayPopis",
                                        context.getString(R.string.noPopis)
                                    )
                                val description = foodPref.getString(
                                    "TodayFood",
                                    context.getString(R.string.noFoodData)
                                )
                                val notificationIntent = Intent(context, MainActivity::class.java)
                                val intentNotif = PendingIntent.getActivity(
                                    context, 0,
                                    notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                                builder = NotificationCompat.Builder(context, channelId)
                                    .setSmallIcon(R.drawable.ic_jidla)
                                    .setContentTitle(title)
                                    .setContentText(description)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                    .setContentIntent(intentNotif)
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

        }
        updateWidget(context)







    }

    private fun updateWidget(context: Context){
        try {
            val intentWidget = Intent(context, AppWidget::class.java)
            intentWidget.action = "android.appwidget.action.APPWIDGET_UPDATE"
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(
                    context,
                    AppWidget::class.java
                )
            )
            intentWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intentWidget)
        } catch (e: Exception){
            Log.d("WidgetUpdateERR", e.toString())
        }
    }

    private fun createNotificationChannel(context: Context?) {
        val name = context?.getString(R.string.channel_name)
        val descriptionText = context?.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        channelId = "SPSEiCanteen"
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }



    fun getFood(context: Context?){
        preferences = context?.getSharedPreferences("update", Context.MODE_PRIVATE)!!
        addr = "https://jidlo.pihrt.com/nacti_jidlo.php?jmeno="
        name = context.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedName", "missing").toString()
        pwd = context.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedPwd", "missing").toString()
        val verze = BuildConfig.VERSION_NAME
        fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=null&ver=$verze"

        //XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
        Log.d("getFood", "Start")
        val output = getDataFromUrl(fulladdr, context).toString()
        val filename = "orders.json"
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it?.write(output.toByteArray())
        }
        json = output
        Log.d("JSONOrders", json)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
        val lastUpdate = current.format(formatter)
        with(preferences.edit()) {
            putString("lastDate", lastUpdate)
            apply()
        }
        saveToPrefs(context)
    }

    private fun orderFood(context: Context?, mode: Int){
        var output = getDataFromUrl(fulladdr, context).toString()
        val filename = "orders.json"
        val fileContents = output
        context?.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it?.write(fileContents.toByteArray())
        }
        val denvTydnu = LocalDate.now().plusDays(4).dayOfWeek
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
                val current = LocalDateTime.now().plusDays(4)
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val datumobedu = current.format(formatter)
                val delkajson = mainObject.length() - 1
                for (i in 0..delkajson) {
                    val obed = mainObject.getJSONObject(i)
                    val datum: String = obed.getString("datum")
                    if (datumobedu!==datum){
                        val currentOrder = LocalDateTime.now().plusDays(4)
                        val formatterOrder = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        datumobednavky = currentOrder.format(formatterOrder)
                        prikaz = "$mode,$datumobednavky,make"
                    }
                }
            }
        }
        val verze = BuildConfig.VERSION_NAME
        fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=$prikaz&ver=$verze"
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
            val builder = NotificationCompat.Builder(context, channelId)
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
        output = getDataFromUrl(fulladdr, context).toString()
        val foodObject = JSONArray(output)
        if (JSONObject(foodObject[0].toString()).has("food")){
            fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=null"
            getFood(context)
        }
    }

    fun getFoodList(context: Context){
        addr = "https://jidlo.pihrt.com/nacti_list.php"
        val output = getDataFromUrl(addr, context).toString()
        val filename = "foodlist.json"
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it?.write(output.toByteArray())
        }
        Log.d("JSONAll", output.toString())
        Log.d("getFoodList", "DONE")
    }

    fun orderCustom(context: Context, date: String, id: Int, mode: String){
        name = context.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedName", "missing").toString()
        pwd = context.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedPwd", "missing").toString()
        val verze = BuildConfig.VERSION_NAME
        //fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=null&ver=$verze"

        val address = "https://jidlo.pihrt.com/nacti_jidlo.php?jmeno=$name&heslo=$pwd&api=$apikey&prikaz=$id,$date,$mode&ver=$verze"
        getDataFromUrl(address, context).toString()
    }


    private fun getDataFromUrl(demoIdUrl: String, context: Context?): String? {
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
            } else{
                val output = "[{\"err\":\"strava empty\"}]"
                var filename = "orders.json"
                context?.openFileOutput(filename, Context.MODE_PRIVATE).use {
                    it?.write(output.toByteArray())
                }
                filename = "foodlist.json"
                context?.openFileOutput(filename, Context.MODE_PRIVATE).use {
                    it?.write(output.toByteArray())
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun saveToPrefs(context: Context?) {
        try{
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
                            putBoolean("TodayNotif", true)
                            apply()
                        }
                    }
                }


            }
            layoutPreferences?.edit()?.putString("lastUpdate", context.getSharedPreferences("update", Context.MODE_PRIVATE)
                ?.getString("lastDate", context.getString(R.string.notYetUpdated)))?.apply()
        }
    } catch (e: Exception){
        Log.i("ParseError", e.toString())
    }
    }
}