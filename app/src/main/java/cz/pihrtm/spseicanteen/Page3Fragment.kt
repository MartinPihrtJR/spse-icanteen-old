package cz.pihrtm.spseicanteen

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class Page3Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_page3, container, false)
        val lgnbtn: Button = view.findViewById(R.id.welcomeConfirm)
        val linkText: TextView = view.findViewById(R.id.textViewLink)
        lgnbtn.setOnClickListener {
            activity?.finish()
        }
        linkText.movementMethod = LinkMovementMethod.getInstance()
        return view
    }

}