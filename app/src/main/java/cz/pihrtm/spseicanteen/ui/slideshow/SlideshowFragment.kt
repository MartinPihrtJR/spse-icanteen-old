package cz.pihrtm.spseicanteen.ui.slideshow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.pihrtm.spseicanteen.R

class SlideshowFragment : Fragment() {

    private lateinit var userViewModel: SlideshowViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel = ViewModelProvider(this).get(SlideshowViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_slideshow, container, false)
        val sharedPref = context?.getSharedPreferences("autoOrder", Context.MODE_PRIVATE)
        val orderGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        val orderDisabled: RadioButton = view.findViewById(R.id.autoOrder0)
        val order1: RadioButton = view.findViewById(R.id.autoOrder1)
        val order2: RadioButton = view.findViewById(R.id.autoOrder2)
        val order3: RadioButton = view.findViewById(R.id.autoOrder3)
        //**************************************************************
        //**********************************
        when {
            sharedPref?.getInt("mode",0)==0 -> {
                orderGroup.check(R.id.autoOrder0)
            }
            sharedPref?.getInt("mode",0)==1 -> {
                orderGroup.check(R.id.autoOrder1)
            }
            sharedPref?.getInt("mode",0)==2 -> {
                orderGroup.check(R.id.autoOrder2)
            }
            sharedPref?.getInt("mode",0)==3 -> {
                orderGroup.check(R.id.autoOrder3)
            }
        }
        orderDisabled.setOnClickListener {
            sharedPref?.edit()?.putInt("mode",0)?.apply()
            Toast.makeText(context,getString(R.string.autoOrderToast0),Toast.LENGTH_SHORT).show()
        }
        order1.setOnClickListener {
            sharedPref?.edit()?.putInt("mode",1)?.apply()
            Toast.makeText(context,getString(R.string.autoOrderToast1),Toast.LENGTH_SHORT).show()
        }
        order2.setOnClickListener {
            sharedPref?.edit()?.putInt("mode",2)?.apply()
            Toast.makeText(context,getString(R.string.autoOrderToast2),Toast.LENGTH_SHORT).show()
        }
        order3.setOnClickListener {
            sharedPref?.edit()?.putInt("mode",3)?.apply()
            Toast.makeText(context,getString(R.string.autoOrderToast3),Toast.LENGTH_SHORT).show()
        }


        return view
    }
}