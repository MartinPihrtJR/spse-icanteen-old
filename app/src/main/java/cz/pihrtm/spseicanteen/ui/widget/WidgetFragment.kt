package cz.pihrtm.spseicanteen.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import cz.pihrtm.spseicanteen.R

class WidgetFragment : Fragment() {

    private lateinit var userViewModel: WidgetViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel = ViewModelProvider(this).get(WidgetViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_widget, container, false)
        return view
    }
}