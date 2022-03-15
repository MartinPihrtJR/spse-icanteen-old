package cz.pihrtm.spseicanteen.ui.slideshow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        userViewModel = ViewModelProvider(this)[SlideshowViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_slideshow, container, false)
        val sharedPref = context?.getSharedPreferences("autoOrder", Context.MODE_PRIVATE)
        val spinnerMo: Spinner = view.findViewById(R.id.spinnerMo)
        val orderDisabled: RadioButton = view.findViewById(R.id.autoOrder0)
        val order1: RadioButton = view.findViewById(R.id.autoOrder1)
        val order2: RadioButton = view.findViewById(R.id.autoOrder2) //TODO smazat
        val order3: RadioButton = view.findViewById(R.id.autoOrder3)
        //**************************************************************
        // Create an ArrayAdapter using the string array and a default spinner layout
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinnerItems, android.R.layout.simple_spinner_item
        )
        // Specify the layout to use when the list of choices appears
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        // Apply the adapter to the spinner
        spinnerMo.adapter = adapter
        //**********************************

        orderDisabled.setOnClickListener {

        }
        order1.setOnClickListener {
            sharedPref?.edit()?.putInt("mode",1)?.apply()
            Toast.makeText(context,getString(R.string.autoOrderToast1),Toast.LENGTH_SHORT).show()
        }
        order2.setOnClickListener { //TODO smazat
            sharedPref?.edit()?.putInt("mode",2)?.apply()
            Toast.makeText(context,getString(R.string.autoOrderToast2),Toast.LENGTH_SHORT).show()
        }
        order3.setOnClickListener {
            sharedPref?.edit()?.putInt("mode",3)?.apply()
            Toast.makeText(context,getString(R.string.autoOrderToast3),Toast.LENGTH_SHORT).show()
        }

        spinnerMo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener { //TODO copy na zbyly dny
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        sharedPref?.edit()?.putInt("MO", 0)?.apply()
                        Toast.makeText(
                            context,
                            getString(R.string.autoOrderToast0),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    1 -> {
                        sharedPref?.edit()?.putInt("MO", 1)?.apply()
                        Toast.makeText(
                            context,
                            getString(R.string.autoOrderToast0),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    2 -> {
                        sharedPref?.edit()?.putInt("MO", 2)?.apply()
                        Toast.makeText(
                            context,
                            getString(R.string.autoOrderToast0),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    3 -> {
                        sharedPref?.edit()?.putInt("MO", 3)?.apply()
                        Toast.makeText(
                            context,
                            getString(R.string.autoOrderToast0),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

                // sometimes you need nothing here
            }
        }
        //TODO pro kazdej den se ulozi do promenne MO - FR a v orderFood se pak nacte pro kazdej den



        return view
    }

}