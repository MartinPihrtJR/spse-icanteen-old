package cz.pihrtm.spseicanteen.ui.gallery

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.pihrtm.spseicanteen.R


class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_gallery, container, false)
        val swNew = view.findViewById<SwitchCompat>(R.id.switchNotifyNew)
        val swOrder = view.findViewById<SwitchCompat>(R.id.switchNotifyOrder)
        val buttonsView = view.findViewById<RadioGroup>(R.id.timeButtons)
        val but1: RadioButton = view.findViewById(R.id.notif1)
        val but2: RadioButton = view.findViewById(R.id.notif2)
        val but3: RadioButton = view.findViewById(R.id.notif3)
        val but4: RadioButton = view.findViewById(R.id.notif4)
        val preferences = context?.getSharedPreferences("notif", Context.MODE_PRIVATE)
        fun setNewLayout(){
            if (preferences?.getBoolean("newEnabled", false) == true){
                swNew.isChecked = true
                buttonsView.visibility = View.VISIBLE
                when (preferences.getInt("time",11)){
                    10 ->{
                        buttonsView.check(R.id.notif1)
                    }
                    11 ->{
                        buttonsView.check(R.id.notif2)
                    }
                    12 ->{
                        buttonsView.check(R.id.notif3)
                    }
                    13 ->{
                        buttonsView.check(R.id.notif4)
                    }
                }
            }
            else {
                swNew.isChecked = false
                buttonsView.visibility = View.GONE
                when (preferences?.getInt("time",11)){
                    10 ->{
                        buttonsView.check(R.id.notif1)
                    }
                    11 ->{
                        buttonsView.check(R.id.notif2)
                    }
                    12 ->{
                        buttonsView.check(R.id.notif3)
                    }
                    13 ->{
                        buttonsView.check(R.id.notif4)
                    }
                }
            }
            swOrder.isChecked = preferences?.getBoolean("orderEnabled", false) == true
        }
        setNewLayout()

        swNew.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                preferences?.edit()?.putBoolean("newEnabled", true)?.apply()
                Log.i("swOrder", preferences?.getBoolean("newEnabled", false).toString())
                setNewLayout()
            }
            else{
                preferences?.edit()?.putBoolean("newEnabled", false)?.apply()
                Log.i("swOrder", preferences?.getBoolean("newEnabled", false).toString())
                setNewLayout()
            }
        }

        swOrder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                preferences?.edit()?.putBoolean("orderEnabled", true)?.apply()
                Log.i("swOrder", preferences?.getBoolean("orderEnabled", false).toString())
                setNewLayout()
            }
            else{
                preferences?.edit()?.putBoolean("orderEnabled", false)?.apply()
                Log.i("swOrder", preferences?.getBoolean("orderEnabled", false).toString())
                setNewLayout()
            }
        }

        but1.setOnClickListener {
            preferences?.edit()?.putInt("time",10)?.apply()
        }
        but2.setOnClickListener {
            preferences?.edit()?.putInt("time",11)?.apply()
        }
        but3.setOnClickListener {
            preferences?.edit()?.putInt("time",12)?.apply()
        }
        but4.setOnClickListener {
            preferences?.edit()?.putInt("time",13)?.apply()
        }


        return view
    }
}