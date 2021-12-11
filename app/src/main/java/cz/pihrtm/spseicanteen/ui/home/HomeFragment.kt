package cz.pihrtm.spseicanteen.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.pihrtm.spseicanteen.R





class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val b: Button = view.findViewById(R.id.button)
        b.setOnClickListener {
            Toast.makeText(context, "BOTTON", "1".toInt()).show()
        }
        return view
    }

}