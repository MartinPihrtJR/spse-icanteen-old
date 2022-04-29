package cz.pihrtm.spseicanteen.adapter


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cz.pihrtm.spseicanteen.GetJson
import cz.pihrtm.spseicanteen.R
import cz.pihrtm.spseicanteen.model.FoodList
import cz.pihrtm.spseicanteen.model.Obed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class OrderAdapter(val list: Array<FoodList?>, private val ordered: Array<Obed?>, var orderedIndex: Int, var previous: IntArray) : RecyclerView.Adapter<OrderAdapter.ItemViewHolder>(){
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
        /*Log.d("ordered", ordered[position]?.obed?.trim().toString())
        Log.d("list1", listObed[1].trim().dropLastWhile {  !it.isLetter() })
        Log.d("list2", listObed[2].trim().dropLastWhile {  !it.isLetter() })
        Log.d("list3", listObed[3].trim().dropLastWhile {  !it.isLetter() })*/

        try {
            when (ordered[position]?.obed?.trim()) {
                listObed[1].trim().dropLastWhile { !it.isLetter() } -> {
                    Log.d("oreders", "ordered == list 1")
                    spinner.setSelection(1, false)
                    previous[position] = 1
                }
                listObed[2].trim().dropLastWhile { !it.isLetter() } -> {
                    Log.d("oreders", "ordered == list 2")
                    spinner.setSelection(2, false)
                    previous[position] = 2
                }
                listObed[3].trim().dropLastWhile { !it.isLetter() } -> {
                    Log.d("oreders", "ordered == list 3")
                    spinner.setSelection(3, false)
                    previous[position] = 3
                }
                else -> {
                    Log.d("oreders", "neni")
                    spinner.setSelection(0, false)
                    previous[position] = 0
                }
            }
        } catch (e: Exception){
            Log.d("oreders", "ERROR: $e")
            spinner.setSelection(0, false)
            previous[position] = 0
        }


        try {
            Log.d("datumList", list[position]?.datum?.trim().toString())
            Log.d("datumOrdered", ordered[position]?.datum?.trim().toString())
            Log.d("ordered", ordered[position]?.obed?.trim().toString())
            Log.d("list1", listObed[1].trim().dropLastWhile { !it.isLetter() })
            Log.d("list2", listObed[2].trim().dropLastWhile { !it.isLetter() })
            Log.d("list3", listObed[3].trim().dropLastWhile { !it.isLetter() })
        } catch (e: Exception){
            Log.d("error", e.toString())
        }

        //TODO kdyz je datum stejne kontroluj jestli je obednano jinak ne, pridame do promenne na datumy mimo
        try {
            when (ordered[orderedIndex]?.obed?.trim().toString()) {
                listObed[1].trim().dropLastWhile { !it.isLetter() } -> {
                    Log.d("je_obednano", "ordered == list 1")
                    spinner.setSelection(1, false)
                    orderedIndex++
                }
                listObed[2].trim().dropLastWhile { !it.isLetter() } -> {
                    Log.d("je_obednano", "ordered == list 2")
                    spinner.setSelection(2, false)
                    orderedIndex++
                }
                listObed[3].trim().dropLastWhile { !it.isLetter() } -> {
                    Log.d("je_obednano", "ordered == list 3")
                    spinner.setSelection(3, false)
                    orderedIndex++
                }
                else -> {
                    Log.d("je_obednano", "neni")
                    spinner.setSelection(0, false)
                }
            }
        } catch (e: Exception){
            Log.d("oreders", "neni")
            spinner.setSelection(0, false)
        }



        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?,
                arg1: View?,
                position: Int,
                id: Long
            ) {
                val updatejob = Job()
                val uiScope = CoroutineScope(Dispatchers.Main + updatejob)

                when (position) {
                    0 -> {
                        uiScope.launch(Dispatchers.IO){
                            GetJson().orderCustom(context, list[holder.adapterPosition]?.datum.toString(), 4, "delete" )
                        }
                        Toast.makeText(context, context.getString(R.string.ordering_delete), Toast.LENGTH_SHORT).show()
                        previous[holder.adapterPosition] = 0
                    }
                    1 -> {
                        if (previous[holder.adapterPosition] == 0) {
                            Log.d(
                                "startedOrdering",
                                "started ordering " + list[holder.adapterPosition]?.datum.toString() + "at index " + holder.adapterPosition
                            )
                            uiScope.launch(Dispatchers.IO) {
                                GetJson().orderCustom(
                                    context,
                                    list[holder.adapterPosition]?.datum.toString(),
                                    3,
                                    "make"
                                )
                            }
                            Toast.makeText(
                                context,
                                listObed[position] + context.getString(R.string.ordering_make),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Log.d(
                                "startedOrdering",
                                "started reordering " + list[holder.adapterPosition]?.datum.toString() + "at index " + holder.adapterPosition
                            )
                            uiScope.launch(Dispatchers.IO) {
                                GetJson().orderCustom(
                                    context,
                                    list[holder.adapterPosition]?.datum.toString(),
                                    3,
                                    "reorder"
                                )
                            }
                            Toast.makeText(
                                context,
                                listObed[position] + context.getString(R.string.ordering_reorder),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        previous[holder.adapterPosition] = 1
                    }
                    2 -> {
                        if (previous[holder.adapterPosition] == 0){
                            Log.d("startedOrdering", "started ordering " + list[holder.adapterPosition]?.datum.toString() + "at index " + holder.adapterPosition)
                            uiScope.launch(Dispatchers.IO) {
                                GetJson().orderCustom(
                                    context,
                                    list[holder.adapterPosition]?.datum.toString(),
                                    4,
                                    "make"
                                )
                            }
                            Toast.makeText(context, listObed[position] + context.getString(R.string.ordering_make), Toast.LENGTH_SHORT).show()
                        } else{
                            Log.d("startedOrdering", "started reordering " + list[holder.adapterPosition]?.datum.toString() + "at index " + holder.adapterPosition)
                            uiScope.launch(Dispatchers.IO) {
                                GetJson().orderCustom(
                                    context,
                                    list[holder.adapterPosition]?.datum.toString(),
                                    4,
                                    "reorder"
                                )
                            }
                            Toast.makeText(context, listObed[position] + context.getString(R.string.ordering_reorder), Toast.LENGTH_SHORT).show()
                        }
                        previous[holder.adapterPosition] = 2

                    }
                    3 -> {
                        if (previous[holder.adapterPosition] == 0){
                            Log.d("startedOrdering", "started ordering " + list[holder.adapterPosition]?.datum.toString() + "at index " + holder.adapterPosition)
                            uiScope.launch(Dispatchers.IO) {
                                GetJson().orderCustom(
                                    context,
                                    list[holder.adapterPosition]?.datum.toString(),
                                    5,
                                    "make"
                                )
                            }
                            Toast.makeText(context, listObed[position] + context.getString(R.string.ordering_make), Toast.LENGTH_SHORT).show()
                        } else{
                            Log.d("startedOrdering", "started reordering " + list[holder.adapterPosition]?.datum.toString() + "at index " + holder.adapterPosition)
                            uiScope.launch(Dispatchers.IO) {
                                GetJson().orderCustom(
                                    context,
                                    list[holder.adapterPosition]?.datum.toString(),
                                    5,
                                    "reorder"
                                )
                            }
                            Toast.makeText(context, listObed[position] + context.getString(R.string.ordering_reorder), Toast.LENGTH_SHORT).show()
                        }
                        previous[holder.adapterPosition] = 3
                        //TODO padá - opravit
                    }
                }

                previous[holder.adapterPosition] = position
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
                Log.d("info", "nothing selected")
            }
        }





        if (position % 2 == 0) {
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.light_blue_rv))
        } else {
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.transparent))
        }

    }
}