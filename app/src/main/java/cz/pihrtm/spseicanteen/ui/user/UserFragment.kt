package cz.pihrtm.spseicanteen.ui.user

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import cz.pihrtm.spseicanteen.GetJson
import cz.pihrtm.spseicanteen.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class UserFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val view: View = inflater.inflate(R.layout.fragment_user, container, false)
        val lgnUser: TextView = view.findViewById(R.id.text_currentpost)
        val fieldUser: EditText = view.findViewById(R.id.editTextName)
        val fieldPwd: EditText = view.findViewById(R.id.editTextPwd)
        val saveBtn: Button = view.findViewById(R.id.buttonLgnSave)
        val clearBtn: Button = view.findViewById(R.id.buttonLgnClear)
        val viewPwdBtn: Button = view.findViewById(R.id.btnViewPwd)
        val vitek: ImageView = view.findViewById(R.id.vitekSecret)
        val sharedPref = activity?.getSharedPreferences("creds", Context.MODE_PRIVATE)
        var name: String
        var pwd: String

        //********************************
        val loggedUsr = sharedPref?.getString("savedName", "-")
        lgnUser.text = loggedUsr

        if (context?.getSharedPreferences("secret", Context.MODE_PRIVATE)!!.getBoolean("enabled", true)){
        when (loggedUsr) {
            "fikrlev" -> {
                vitek.setImageResource(R.drawable.vita)
                vitek.visibility = View.VISIBLE
            }
            "kocherm" -> {
                vitek.setImageResource(R.drawable.marek)
                vitek.visibility = View.VISIBLE
            }
            "hosekm" -> {
                vitek.setImageResource(R.drawable.hosek)
                vitek.visibility = View.VISIBLE
            }
            else -> {
                vitek.visibility = View.GONE
            }
        }}
        else{
            vitek.visibility = View.GONE
        }

        viewPwdBtn.setOnTouchListener(
                OnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            fieldPwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                            fieldPwd.setSelection(fieldPwd.text.length)
                            return@OnTouchListener true
                        }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            fieldPwd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            fieldPwd.typeface = fieldUser.typeface
                            fieldPwd.setSelection(fieldPwd.text.length)
                            return@OnTouchListener true
                        }
                    }
                    false
                }
        )


        saveBtn.setOnClickListener {
            name = fieldUser.text.toString()
            pwd = fieldPwd.text.toString()
            if (name == "" || pwd == ""){
                Toast.makeText(context, getString(R.string.field_blank), Toast.LENGTH_LONG).show()
            }
            else {
                if (context?.getSharedPreferences("secret", Context.MODE_PRIVATE)!!.getBoolean("enabled", true)){
                    when (name) {
                        "fikrlev" -> {
                            vitek.setImageResource(R.drawable.vita)
                            vitek.visibility = View.VISIBLE
                        }
                        "kocherm" -> {
                            vitek.setImageResource(R.drawable.marek)
                            vitek.visibility = View.VISIBLE
                        }
                        "hosekm" -> {
                            vitek.setImageResource(R.drawable.hosek)
                            vitek.visibility = View.VISIBLE
                        }
                        else -> {
                            vitek.visibility = View.GONE
                        }
                    }}
                else{
                    vitek.visibility = View.GONE
                }
                if (sharedPref != null) {
                    with(sharedPref.edit()) {
                        putString("savedName", name)
                        putString("savedPwd", pwd)
                        apply()
                    }
                }
                val loggedInUsr = sharedPref?.getString("savedName", name)
                lgnUser.text = loggedInUsr
                fieldUser.text = null
                fieldPwd.text = null
                fieldPwd.clearFocus()
                fieldUser.clearFocus()
                hideKeyboard()
                Toast.makeText(context, getString(R.string.field_saved), Toast.LENGTH_SHORT).show()
                if (context?.getSharedPreferences("internet", Context.MODE_PRIVATE)?.getBoolean("net",false)!!){
                    uiScope.launch(Dispatchers.IO) {
                        GetJson().getFood(context)

                    }
                }
            }

        }
        clearBtn.setOnClickListener {
            if (sharedPref != null) {
                with(sharedPref.edit()) {
                    remove("savedName")
                    remove("savedPwd")
                    apply()
                }
            }
            lgnUser.text = sharedPref?.getString("savedName", "-")
            fieldUser.text = null
            fieldPwd.text = null
            fieldPwd.clearFocus()
            fieldUser.clearFocus()
            hideKeyboard()
            Toast.makeText(context, getString(R.string.field_clear), Toast.LENGTH_SHORT).show()
        }
        //editor.remove("key")
        return view
    //TODO po prvním přihlášení se musí počkat aby se aplikace nezasekla ve smyčce kvůli chybějícím obědnávkám nebo opravit tlačítko na první spuštění - asi vyrábí špatnej soubor
    }
    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy(){
        job.cancel()
        super.onDestroy()
    }
}