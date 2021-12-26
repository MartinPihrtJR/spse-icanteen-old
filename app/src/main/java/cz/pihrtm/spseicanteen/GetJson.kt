package cz.pihrtm.spseicanteen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.StrictMode
import android.util.Log
import android.view.View
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


/*
kdyz vrati none, spatny prihlasovaci udaje
 */
class GetJson : BroadcastReceiver() {

    private lateinit var json: String
    override fun onReceive(context: Context?, intent: Intent?) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        Log.d("JSON", "OK")

        val preferences = context?.getSharedPreferences("update", Context.MODE_PRIVATE)
        val addr = "https://pihrt.com/spse/jidlo/nacti_jidlo.php?jmeno="
        val name = context?.getSharedPreferences("creds", Context.MODE_PRIVATE)
            ?.getString("savedName", "missing")
        val pwd = context?.getSharedPreferences("creds", Context.MODE_PRIVATE)
            ?.getString("savedPwd", "missing")
        val objednej = context?.getSharedPreferences("settings", Context.MODE_PRIVATE)
            ?.getString("objednej", "null")
        val apikey = 1234
        var fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=$objednej"
        var output = getDataFromUrl(fulladdr).toString()
        Log.i("DATAint", output)
        val filename = "jidla.json"
        val fileContents = output
        context?.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it?.write(fileContents?.toByteArray())
        }
        json = output
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss")
        val lastUpdate = current.format(formatter)
        if (preferences != null) {
            with(preferences.edit()) {
                putString("lastDate", lastUpdate)
                apply()
            }
        }
        saveToPrefs(context)


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
        var mainObject = JSONArray(json)
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