package cz.pihrtm.spseicanteen.ui.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
        val fieldUser: EditText = view.findViewById(R.id.editTextName)
        val fieldPwd: EditText = view.findViewById(R.id.editTextPwd)
        val saveBtn: Button = view.findViewById(R.id.buttonLgnSave)
        val clearBtn: Button = view.findViewById(R.id.buttonLgnClear)
        val sharedPref = activity?.getSharedPreferences("creds", Context.MODE_PRIVATE)
        var name = "null"
        var pwd = "null"

        val loggedUsr = sharedPref?.getString("savedName","-")
        lgnUser.text = loggedUsr
        //********************************
        saveBtn.setOnClickListener {
            name = fieldUser.text.toString()
            pwd = fieldPwd.text.toString()
            if (name == "" || pwd == ""){
                Toast.makeText(context,getString(R.string.field_blank),4.toInt()).show()
            }
            else {
                if (sharedPref != null) {
                    with (sharedPref.edit()) {
                        putString("savedName", name)
                        putString("savedPwd", pwd)
                        apply()
                    }
                }
                lgnUser.text = sharedPref?.getString("savedName","-")
                fieldUser.text = null
                fieldPwd.text = null
                Toast.makeText(context,getString(R.string.field_saved),2.toInt()).show()
            }

        }
        clearBtn.setOnClickListener {
            if (sharedPref != null) {
                with (sharedPref.edit()) {
                    remove("savedName")
                    remove("savedPwd")
                    apply()
                }
            }
            lgnUser.text = sharedPref?.getString("savedName","-")
            fieldUser.text = null
            fieldPwd.text = null
            Toast.makeText(context,getString(R.string.field_clear),2.toInt()).show()
        }
        //editor.remove("key")
        return view
    }
}