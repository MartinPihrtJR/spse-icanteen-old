package cz.pihrtm.spseicanteen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}