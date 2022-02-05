package cz.pihrtm.spseicanteen.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import cz.pihrtm.spseicanteen.R
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val nextLayout: ConstraintLayout = view.findViewById(R.id.foodNext)
        val todayLayout: ConstraintLayout = view.findViewById(R.id.foodToday)
        val errorText: TextView = view.findViewById(R.id.foodError)
        val titleFoodToday: TextView = view.findViewById(R.id.titleFoodToday)
        val titleSoupToday: TextView = view.findViewById(R.id.titleSoupToday)
        val titleFoodTomorrow: TextView = view.findViewById(R.id.titleFoodTomorrow)
        val titleFood2Tomorrow: TextView = view.findViewById(R.id.titleFood2Tomorrow)
        val lastDate: TextView = view.findViewById(R.id.lastUpdated)


        if (context?.getSharedPreferences("first", Context.MODE_PRIVATE)?.getBoolean("isFirst",true)==true){
            //nic
        }
        else {
            var json = context?.openFileInput("jidla.json")?.bufferedReader()?.readLines().toString() //read
            json = json.subSequence(1, json.length - 1).toString() //convert output back to string, it returns [string]
            Log.d("JSON", json)
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

                }
                else {
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
            } catch (e: Exception){
                Log.d("ERROR",e.toString())
            }

            }



        return view
    }

}
