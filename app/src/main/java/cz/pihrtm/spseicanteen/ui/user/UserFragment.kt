package cz.pihrtm.spseicanteen.ui.user

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import cz.pihrtm.spseicanteen.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONArray
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

class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var navButton:Button
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val view: View = inflater.inflate(R.layout.fragment_user, container, false)
        val lgnUser: TextView = view.findViewById(R.id.text_currentpost)
        val fieldUser: EditText = view.findViewById(R.id.editTextName)
        val fieldPwd: EditText = view.findViewById(R.id.editTextPwd)
        val saveBtn: Button = view.findViewById(R.id.buttonLgnSave)
        val clearBtn: Button = view.findViewById(R.id.buttonLgnClear)
        val sharedPref = activity?.getSharedPreferences("creds", Context.MODE_PRIVATE)
        var name = "null"
        var pwd = "null"

        //********************************
        val loggedUsr = sharedPref?.getString("savedName","-")
        lgnUser.text = loggedUsr


        saveBtn.setOnClickListener {
            name = fieldUser.text.toString()
            pwd = fieldPwd.text.toString()
            if (name == "" || pwd == ""){
                Toast.makeText(context,getString(R.string.field_blank),Toast.LENGTH_LONG).show()
            }
            else {
                if (sharedPref != null) {
                    with (sharedPref.edit()) {
                        putString("savedName", name)
                        putString("savedPwd", pwd)
                        apply()
                    }
                }
                val loggedUsr = sharedPref?.getString("savedName",name)
                lgnUser.text = loggedUsr
                fieldUser.text = null
                fieldPwd.text = null
                fieldPwd.clearFocus()
                fieldUser.clearFocus()
                hideKeyboard()
                Toast.makeText(context,getString(R.string.field_saved),Toast.LENGTH_SHORT).show()
                uiScope.launch(Dispatchers.IO) {
                    getJsonOnetime(context)
                }
            }

        }
        clearBtn.setOnClickListener {
            if (sharedPref != null) {
                with (sharedPref.edit()) {
                    remove("savedName")
                    remove("savedPwd")
                    apply()
                }
            }
            lgnUser.text = sharedPref?.getString("savedName","-")
            fieldUser.text = null
            fieldPwd.text = null
            fieldPwd.clearFocus()
            fieldUser.clearFocus()
            hideKeyboard()
            Toast.makeText(context,getString(R.string.field_clear),Toast.LENGTH_SHORT).show()
        }
        //editor.remove("key")
        return view

    }
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    //*********************************************************
    //stahnuti jsonu
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
        val fulladdr = "$addr$name&heslo=$pwd&api=$apikey&prikaz=$objednej"
        val output: String = getDataFromUrl(fulladdr).toString()
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
                val reader = BufferedReader(InputStreamReader(
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

    override fun onDestroy(){
        job.cancel()
        super.onDestroy()
    }
}