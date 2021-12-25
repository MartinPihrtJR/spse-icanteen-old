package cz.pihrtm.spseicanteen.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cz.pihrtm.spseicanteen.FirstSetup
import cz.pihrtm.spseicanteen.R
import cz.pihrtm.spseicanteen.ui.user.UserFragment
import org.json.JSONArray
import org.json.JSONObject


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        var json = context?.openFileInput("jidla.json")?.bufferedReader()?.readLines().toString() //read
        json = json.subSequence(1 , json.length-1).toString() //convert output back to string, it returns [string]
        Log.d("JSON", json)
        var mainObject = JSONArray(json)
        if (JSONObject(mainObject[0].toString()).has("err")){

            var errorType = mainObject[0].toString()
            errorType = JSONObject(errorType).getString("err")
            Log.i("JSONerr", errorType)
            //TODO skryjou se dolni zradla, vypisou se chyby
        }
        else{
            //TODO vyparsoujou se zradla ke 3 dnum
        }



        return view
    }

}
