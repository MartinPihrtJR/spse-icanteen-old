package cz.pihrtm.spseicanteen.ui.orderlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.pihrtm.spseicanteen.R
import cz.pihrtm.spseicanteen.adapter.ObedAdapter
import cz.pihrtm.spseicanteen.model.Obed
import org.json.JSONArray
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrderlistFragment : Fragment() {

    private lateinit var userViewModel: OrderlistViewModel
    private lateinit var recycler: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel = ViewModelProvider(this)[OrderlistViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_orderlist, container, false)
        recycler = view.findViewById<RecyclerView>(R.id.rvJidlo)
        val scroll = savedInstanceState?.getInt("scroll",0)?:0
        val nodata = view.findViewById<TextView>(R.id.noData)

        try {
            var json = context?.openFileInput("jidla.json")?.bufferedReader()?.readLines().toString() //read
            json = json.subSequence(1, json.length - 1).toString()
            val mainObject = JSONArray(json)
            val delkajson = mainObject.length() - 1
            var list: Array<Obed?> = arrayOfNulls<Obed>(delkajson+1)
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val current = LocalDate.now()
            var notNew = 0
            for (i in 0..delkajson){
                val obed = mainObject.getJSONObject(i)
                val datum: String = obed.getString("datum")
                val compdatum: LocalDate? = LocalDate.parse(datum, formatter)
                val jidlo: String = obed.getString("jidlo")

                if (compdatum != null) {
                    if (compdatum<current){
                        notNew += 1
                    }
                    else{
                        list[i-notNew] = Obed(jidlo, datum)

                    }
                }

            }

            list.reverse()
            list = list.drop(notNew).toTypedArray()
            if (list.isEmpty()) {
                nodata.visibility = View.VISIBLE
                recycler.visibility = View.GONE
            }
            val adapter = ObedAdapter(list)
            recycler.adapter = adapter
            recycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            recycler.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
            (recycler.layoutManager as LinearLayoutManager).scrollToPosition(scroll)
        } catch (e: Exception){

        }

        return view
    }


    override fun onSaveInstanceState(outState: Bundle) {

        outState.putInt("scroll",(recycler.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition())
        super.onSaveInstanceState(outState)
    }
}