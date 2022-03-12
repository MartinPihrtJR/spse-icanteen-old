package cz.pihrtm.spseicanteen.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cz.pihrtm.spseicanteen.R
import cz.pihrtm.spseicanteen.model.Obed

class ObedAdapter(val list: Array<Obed?>) : RecyclerView.Adapter<ObedAdapter.ItemViewHolder>(){
    class ItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.list_obed,parent,false)
    return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val view = holder.itemView
        val datum = view.findViewById<TextView>(R.id.rv_Datum)
        val obed = view.findViewById<TextView>(R.id.rv_Obed)
        datum.text = list[position]?.datum
        obed.text = list[position]?.obed




        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(view.context,R.color.light_blue_rv))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(view.context,R.color.transparent))
        }

    }

}