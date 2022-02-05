package cz.pihrtm.spseicanteen


import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2


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
        val fileContents = "[{\"err\":\"not created\"}]"
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
        super.finish()

    }

    override fun onBackPressed() {
        Toast.makeText(this, R.string.onBack, Toast.LENGTH_LONG).show()
    }

}

