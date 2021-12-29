package cz.pihrtm.spseicanteen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.StrictMode
import android.util.Log
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

    override fun onReceive(context: Context?, intent: Intent?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        preferences = context?.getSharedPreferences("update", Context.MODE_PRIVATE)!!
        addr = "https://pihrt.com/spse/jidlo/nacti_jidlo.php?jmeno="
        name = context.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedName", "missing").toString()
        pwd = context.getSharedPreferences("creds", Context.MODE_PRIVATE)?.getString("savedPwd", "missing").toString()
        val mode = context.getSharedPreferences("autoOrder", Context.MODE_PRIVATE)?.getInt("mode", 0)
        val prikaz: String
        fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=null"
        when{
            (mode==0)->{
                getFood(context)
            }
            (mode==1)->{
                orderFood(context,3)
                Log.i("Ordermode","3")
            }
            (mode==2)->{
                orderFood(context, 4)
                Log.i("Ordermode","4")
            }
            (mode==3)->{
                orderFood(context, 5)
                Log.i("Ordermode","5")
            }
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