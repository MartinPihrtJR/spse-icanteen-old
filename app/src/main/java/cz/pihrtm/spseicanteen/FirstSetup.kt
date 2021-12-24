package cz.pihrtm.spseicanteen

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.viewpager2.widget.ViewPager2
import cz.pihrtm.spseicanteen.ui.home.HomeFragment
import cz.pihrtm.spseicanteen.ui.user.UserFragment as UserFragment


class FirstSetup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_setup)
        val viewpager: ViewPager2 = findViewById(R.id.view_pager)
        val fragments: ArrayList<Fragment> = arrayListOf(
                Page1Fragment(),
                Page2Fragment(),
                Page3Fragment()
        )
        val adapter = ViewPagerAdapter(fragments, this)
        viewpager.adapter = adapter



    }

    override fun finish() {

        val filename = "jidla.json"
        val fileContents = ""
        this.openFileOutput(filename, Context.MODE_PRIVATE).use {
             it?.write(fileContents.toByteArray())
        }
        val sharedPref = this.getSharedPreferences("first", Context.MODE_PRIVATE)
        if (sharedPref != null) {
            with(sharedPref.edit()) {
                putBoolean("isFirst", false)
                apply()
            }
        }
        //TODO zavolat funkci replaceUserFragment
        Handler().postDelayed({
            super.finish()
        }, 500)

    }

    override fun onBackPressed() {
        Toast.makeText(this, R.string.onBack, Toast.LENGTH_LONG).show()
    }

}

