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


class OrderAdapter(val list: Array<FoodList?>) : RecyclerView.Adapter<OrderAdapter.ItemViewHolder>(){
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
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adp1

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, position: Int, id: Long) {
                // TODO Auto-generated method stub
                Toast.makeText(context, listObed[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "nic neobjednano", Toast.LENGTH_SHORT).show()
            }
        }




        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(view.context,R.color.light_blue_rv))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(view.context,R.color.transparent))
        }

    }

}