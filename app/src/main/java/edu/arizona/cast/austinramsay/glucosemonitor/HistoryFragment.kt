package edu.arizona.cast.austinramsay.glucosemonitor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "edu.arizona.cast.austinramsay.glucosemonitor.HistoryFragment"

/*
 * This fragment will contain different elements of stored Glucose values that the user has
 * calculated previously. When a user clicks on an item in the view, the information about that
 * Glucose item is shown along with details of the calculation.
 */
class HistoryFragment : Fragment(R.layout.history_view) {

    private val model: SharedViewModel by activityViewModels()
    private var adapter: GlucoseRViewAdapter? = GlucoseRViewAdapter(emptyList())
    private lateinit var rView: RecyclerView
    private lateinit var rViewAdapter: RecyclerView.Adapter<*>
    private lateinit var rViewManager: RecyclerView.LayoutManager

    /*
     * RecyclerView Adapter and Holder classes
     * This is what defines how each element is viewed in the RecyclerView
     * We set each part of the view holder to its corresponding value from the Glucose object it is bound to
     */
    private inner class GlucoseHolder(view: View) : RecyclerView.ViewHolder(view) {

        // These are all individual parts of the view holder that we will populate with information from each individual glucose instance
        private lateinit var glucose: Glucose
        private val dateView: TextView = itemView.findViewById(R.id.glucose_date)
        private val avgView: TextView = itemView.findViewById(R.id.glucose_avg)
        private val statusCheck: CheckBox = itemView.findViewById(R.id.glucose_check)
        private val avgStatus: TextView = itemView.findViewById(R.id.glucose_status)

        // Bind the values of the glucose instance to the corresponding view holder places
        fun bind(glucose: Glucose) {
            this.glucose = glucose
            dateView.text = this.glucose.date.toString()
            // dateView.text = this.glucose.date.toLocalDate().toString()
            // avgView.text = this.glucose.average.toString()
            /*statusCheck.isChecked = if (this.glucose.fastingStatus != Glucose.STATUS_NORMAL) {
                true
            } else if (this.glucose.breakfastStatus != Glucose.STATUS_NORMAL) {
                true
            } else if (this.glucose.lunchStatus != Glucose.STATUS_NORMAL) {
                true
            } else if (this.glucose.dinnerStatus != Glucose.STATUS_NORMAL) {
                true
            } else {
                false
            }
            avgStatus.text = this.glucose.overallStatus
             */
            avgView.text = "testing"
            statusCheck.isChecked = true
            avgStatus.text = "testing"
        }
    }

    /*
     * The RecyclerView adapter
     * Binds a glucose object to a holder in the recycler view, and sets an event listener for when
     * the user clicks on a given history item.
     */
    private inner class GlucoseRViewAdapter(var glucoseList: List<Glucose>) : RecyclerView.Adapter<GlucoseHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlucoseHolder {
            val view = layoutInflater.inflate(R.layout.list_item_glucose, parent, false)

            return GlucoseHolder(view)
        }

        override fun onBindViewHolder(holder: GlucoseHolder, position: Int) {
            val glucose = glucoseList[position]
            holder.bind(glucose)

            // When an item is clicked on, display a Toast with the Glucose details
            holder.itemView.setOnClickListener {
                Toast.makeText(context, glucose.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount() = glucoseList.size
    }

    /*
     * Main History fragment onCreateView function
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Expand the history view layout into the fragment space
        val view = layoutInflater.inflate(R.layout.history_view, container, false)

        // Set the recycler view layout manager and adapter (with just an empty list for now until DB is read)
        rViewManager = LinearLayoutManager(context)
        rViewAdapter = GlucoseRViewAdapter(emptyList())

        // Apply the layout manager and adapter to the recycler view
        rView = view.findViewById<RecyclerView>(R.id.history_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = rViewManager
            adapter = rViewAdapter
        }

        // Add an observer to the glucose database live data so that when data changes the UI syncs to it
        model.glucoseListLiveData.observe(viewLifecycleOwner,
                Observer { glucoseList ->
                    glucoseList?.let {
                        Log.i(TAG, "Got GlucoseList ${glucoseList.size}")
                        updateUI(glucoseList)
                    }
                })

        return view
    }

    // Set a new data list for the recycler view (used in the observer of live data for the glucose history database)
    private fun updateUI(glucoseList: List<Glucose>) {
        adapter = GlucoseRViewAdapter(glucoseList)
        rView.adapter = adapter
    }
}
