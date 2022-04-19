package cz.pihrtm.spseicanteen.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cz.pihrtm.spseicanteen.R
import cz.pihrtm.spseicanteen.model.FoodList
import cz.pihrtm.spseicanteen.model.Obed


class OrderAdapter(val list: Array<FoodList?>, val ordered: Array<Obed?>) : RecyclerView.Adapter<OrderAdapter.ItemViewHolder>(){
    class ItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.list_ordering,parent,false)
    return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val view = holder.itemView
        val datum = view.findViewById<TextView>(R.id.ob_Datum)
        val polevka = view.findViewById<TextView>(R.id.ob_Polevka)
        val spinner = view.findViewById<Spinner>(R.id.ob_FoodSpinner)
        Log.d("pos", position.toString())
        datum.text = list[position]?.datum.toString()
        polevka.text = list[position]?.polevka.toString()
        val context = datum.context
        val listObed: MutableList<String> = ArrayList()
        listObed.add(context.getString(R.string.ordering_noOrdered))
        listObed.add(list[position]?.obed1.toString())
        listObed.add(list[position]?.obed2.toString())
        listObed.add(list[position]?.obed3.toString())

        val adp1: ArrayAdapter<String> = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_item, listObed
        )
        adp1.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1)
        spinner.adapter = adp1

        //nastaveni podle toho co mame objednano
        Log.d("ordered", ordered[position]?.obed?.trim().toString())
        Log.d("list1", listObed[1].trim().dropLastWhile {  !it.isLetter() })
        Log.d("list2", listObed[2].trim().dropLastWhile {  !it.isLetter() })
        Log.d("list3", listObed[3].trim().dropLastWhile {  !it.isLetter() })

        when (ordered[position]?.obed?.trim()) {
            listObed[1].trim().dropLastWhile {  !it.isLetter() } -> {
                Log.d("oreders","ordered == list 1")
                spinner.setSelection(1, false)
            }
            listObed[2].trim().dropLastWhile {  !it.isLetter() } -> {
                Log.d("oreders","ordered == list 2")
                spinner.setSelection(2, false)
            }
            listObed[3].trim().dropLastWhile {  !it.isLetter() } -> {
                Log.d("oreders","ordered == list 3")
                spinner.setSelection(3, false)
            }
            else -> {
                Log.d("oreders","neni")
                spinner.setSelection(0, false)
            }
        }



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, position: Int, id: Long) {

                if (position == 0){

                }

                Toast.makeText(context, listObed[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub

            }
        }




        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(view.context,R.color.light_blue_rv))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(view.context,R.color.transparent))
        }

    }

}