package cz.pihrtm.spseicanteen.ui.ordering

import android.app.AlertDialog
import android.content.DialogInterface
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
import cz.pihrtm.spseicanteen.model.Obed
import org.json.JSONArray
import java.io.StringReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrderingFragment : Fragment() {

    private lateinit var userViewModel: OrderingViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var list: Array<FoodList?>
    private lateinit var listOrder: Array<Obed?>

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


        //************************************************
        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.beta_notice)
            .setPositiveButton(R.string.beta_ok,
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

        // Create the AlertDialog object and return it




        //**********************************************

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
            list.reverse()

            try {
                var jsonOrdered = context?.openFileInput("orders.json")?.bufferedReader()?.readLines().toString() //read
                jsonOrdered = jsonOrdered.subSequence(1, jsonOrdered.length - 1).toString()
                val mainObject = JSONArray(jsonOrdered)
                val delkajson = mainObject.length() - 1
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val current = LocalDate.now()
                Log.d("current", current.toString())
                var notNew = 0
                Log.d("jsonOrdered", jsonOrdered)
                listOrder = arrayOfNulls<Obed>(delkajson+1)
                for (i in 0..delkajson){
                    val obedObj = mainObject.getJSONObject(i)
                    val datum: String = LocalDate.parse(obedObj.getString("datum"), formatter).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val obed: String = obedObj.getString("jidlo")
                    val compdatum: LocalDate? = LocalDate.parse(datum)
                    if (compdatum != null) {
                        if (compdatum<current){
                            notNew += 1
                        }
                        else{
                            listOrder[i-notNew] = Obed(obed, datum)

                        }
                    }
                }
                listOrder.reverse()
                listOrder = listOrder.drop(notNew).toTypedArray()

            } catch (e: Exception){
                Log.d("OrderingJsonParse", e.toString())
            }
            if (listOrder.isEmpty()) {
                nodata.visibility = View.VISIBLE
                recycler.visibility = View.GONE
            }
            Log.d("orderListSize", listOrder.size.toString())
            for (i in 0..listOrder.size-1){
                Log.d("listOrder", listOrder[i]!!.datum + ": " +  listOrder[i]!!.obed)
            }
            var orderedIndex = 0
            var previous = IntArray(50) { 0 }
            val adapter = OrderAdapter(list, listOrder, orderedIndex, previous)
            recycler.adapter = adapter
            recycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            recycler.addItemDecoration(DividerItemDecoration(context,LinearLayoutManager.VERTICAL))
            (recycler.layoutManager as LinearLayoutManager).scrollToPosition(scroll)
        } catch (e: Exception){
            Log.d("OrderingStart", e.toString())
        }


        builder.create().show() //dialog

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