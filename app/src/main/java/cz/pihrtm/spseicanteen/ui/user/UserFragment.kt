package cz.pihrtm.spseicanteen.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.pihrtm.spseicanteen.R

class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val view: View = inflater.inflate(R.layout.fragment_user, container, false)
        val lgnUser: TextView = view.findViewById(R.id.text_currentpost)
        val loggedUsr = "pihrtm"
        lgnUser.text = loggedUsr
        return view
    }
}