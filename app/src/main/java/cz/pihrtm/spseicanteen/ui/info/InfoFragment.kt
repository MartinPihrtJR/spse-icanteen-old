package cz.pihrtm.spseicanteen.ui.info

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.pihrtm.spseicanteen.R

class InfoFragment : Fragment() {

    private lateinit var userViewModel: InfoViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel = ViewModelProvider(this)[InfoViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        val button: Button = view.findViewById(R.id.buttonStore)
        val secret: TextView = view.findViewById(R.id.secretText)
        val text:TextView = view.findViewById(R.id.tosText)
        button.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context?.packageName)))
            } catch (e: Exception) {
                Toast.makeText(context, context?.getString(R.string.error),Toast.LENGTH_LONG).show()
            }

        }
        secret.setOnClickListener {
            if (context?.getSharedPreferences("secret", Context.MODE_PRIVATE)?.getBoolean("enabled", true) == true){
                context?.getSharedPreferences("secret", Context.MODE_PRIVATE)!!
                    .edit().putBoolean("enabled", false).apply()
                Toast.makeText(context, getString(R.string.secretDis), Toast.LENGTH_SHORT).show()
            }
            else{
                context?.getSharedPreferences("secret", Context.MODE_PRIVATE)!!
                    .edit().putBoolean("enabled", true).apply()
                Toast.makeText(context, getString(R.string.secretEn), Toast.LENGTH_SHORT).show()
            }

        }

        text.movementMethod = LinkMovementMethod.getInstance()
        return view
    }
}