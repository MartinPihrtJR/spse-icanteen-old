package cz.pihrtm.spseicanteen.ui.ordering

import android.os.Bundle
import android.util.Log
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
import cz.pihrtm.spseicanteen.adapter.OrderAdapter
import cz.pihrtm.spseicanteen.model.FoodList
import org.json.JSONArray
import java.io.StringReader

class OrderingFragment : Fragment() {

    private lateinit var userViewModel: OrderingViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var list: Array<FoodList?>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        userViewModel = ViewModelProvider(this)[OrderingViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_ordering, container, false)
        recycler = view.findViewById<RecyclerView>(R.id.rvOrdering)
        val scroll = savedInstanceState?.getInt("scrollOrder",0)?:0
        val nodata = view.findViewById<TextView>(R.id.noData)


        try {
            try {
                var json = context?.openFileInput("foodlist.json")?.bufferedReader()?.readLines().toString() //read
                json = json.subSequence(1, json.length - 1).toString()
                val mainObject = JSONArray(json)
                val delkajson = mainObject.length() - 1
                list = arrayOfNulls<FoodList>(delkajson+1)
                for (i in 0..delkajson){
                    val obed = mainObject.getJSONObject(i)
                    val datum: String = obed.getString("datum")
                    val obed1: String = try {
                        obed.getString("obed1")
                    } catch (e: Exception){
                        context?.getString(R.string.ordering_noOrdered).toString()
                    }
                    val obed2: String = try {
                        obed.getString("obed2")
                    } catch (e: Exception){
                        context?.getString(R.string.ordering_noOrdered).toString()
                    }
                    val obed3: String = try {
                        obed.getString("obed3")
                    } catch (e: Exception){
                        context?.getString(R.string.ordering_noOrdered).toString()
                    }
                    val polevka: String = try {
                        obed.getString("polevka2")
                    } catch (e: Exception){
                        context?.getString(R.string.ordering_noOrdered).toString()
                    }

                    list[i] = FoodList(datum, obed1, obed2, obed3, polevka)


                }

                list.reverse()
                if (list.isEmpty()) {
                    nodata.visibility = View.VISIBLE
                    recycler.visibility = View.GONE
                }
            } catch (e: Exception){
                Log.d("OrderingJsonParse", e.toString())
            }
            if (list.isEmpty()) {
                nodata.visibility = View.VISIBLE
                recycler.visibility = View.GONE
            }
            val adapter = OrderAdapter(list)
            recycler.adapter = adapter
            recycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            recycler.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
            (recycler.layoutManager as LinearLayoutManager).scrollToPosition(scroll)
        } catch (e: Exception){
            Log.d("OrderingStart", e.toString())
        }

        return view
    }


    override fun onSaveInstanceState(outState: Bundle) {
        try{
        outState.putInt("scrollOrder",(recycler.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition())
        super.onSaveInstanceState(outState)}
        catch (e: Exception){
            Log.d("saveInstanceError", e.toString())
        }
    }
}