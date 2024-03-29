package edu.arizona.cast.austinramsay.glucosemonitor

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "HistoryFragment"
private const val REQUEST_DATE = "DialogDate"
private const val REMINDER_WORK = "ReminderWorker"

class HistoryFragment : Fragment(R.layout.history_view), FragmentResultListener {

    private val dbViewModel: DBViewModel by activityViewModels()
    private lateinit var rView: RecyclerView
    private lateinit var rViewManager: RecyclerView.LayoutManager
    private var rViewAdapter: GlucoseRViewAdapter? = GlucoseRViewAdapter(emptyList())
    private lateinit var dateDialog: DatePickerFragment
    private lateinit var reminderButton: MenuItem

    /*
     * Interface for switching between fragments
     */
    interface Callbacks {
        fun onGlucoseSelected(glucoseDate: Date)
    }

    private var callbacks: Callbacks? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    /*
     * RecyclerView Adapter and Holder classes
     */
    private inner class GlucoseHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var glucose: Glucose
        private val dateView: TextView = itemView.findViewById(R.id.glucose_date)
        private val avgView: TextView = itemView.findViewById(R.id.glucose_avg)
        private val statusCheck: CheckBox = itemView.findViewById(R.id.glucose_check)
        private val avgStatus: TextView = itemView.findViewById(R.id.glucose_status)

        fun bind(glucose: Glucose) {
            this.glucose = glucose

            dateView.text = DateFormatter.formatShort(this.glucose.date)

            avgView.text = GlucoseCalculator.getAverage(
                this.glucose.fasting,
                this.glucose.breakfast,
                this.glucose.lunch,
                this.glucose.dinner
            ).toString()

            avgStatus.text = GlucoseCalculator.getAverageStatus(
                this.glucose.fasting,
                this.glucose.breakfast,
                this.glucose.lunch,
                this.glucose.dinner)

            // If the status for each value (fasting, breakfast, lunch and dinner) are ALL normal, check the box
            // If ANY one of the values are NOT normal, do NOT check the box
            val fastingCheck = (GlucoseCalculator.getFastingStatus(this.glucose.fasting) == GlucoseCalculator.STATUS_NORMAL)
            val breakfastCheck = (GlucoseCalculator.getBreakfastStatus(this.glucose.breakfast) == GlucoseCalculator.STATUS_NORMAL)
            val lunchCheck = (GlucoseCalculator.getLunchStatus(this.glucose.lunch) == GlucoseCalculator.STATUS_NORMAL)
            val dinnerCheck = (GlucoseCalculator.getDinnerStatus(this.glucose.dinner) == GlucoseCalculator.STATUS_NORMAL)
            statusCheck.isChecked = when {
                (fastingCheck && breakfastCheck && lunchCheck && dinnerCheck) -> true
                else -> false
            }
        }
    }

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
                callbacks?.onGlucoseSelected(glucose.date)
            }
        }

        override fun getItemCount() = glucoseList.size
    }


    /*
     * Main fragment logic begins
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.history_view, container, false)

        rViewManager = LinearLayoutManager(context)

        rView = view.findViewById<RecyclerView>(R.id.history_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = rViewManager
            adapter = rViewAdapter
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbViewModel.glucoseList.observe(viewLifecycleOwner, { glucoseList ->
                glucoseList?.let {
                    updateUI(glucoseList)
                }
            }
        )

        // Attach the date dialog callback method
        childFragmentManager.setFragmentResultListener(REQUEST_DATE, viewLifecycleOwner, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_glucose_list, menu)

        reminderButton = menu.findItem(R.id.toggle_reminder_button)
        val reminderText = when (Preferences.isReminderEnabled(requireContext())) {
            false -> R.string.reminder_disabled
            true -> R.string.reminder_enabled
        }
        reminderButton.setTitle(reminderText)
    }

    // When the user selects the 'Add Glucose Entry' button in the menu bar,
    // open a date chooser dialog to allow the user to set the date for the new entry.
    // The date dialog handler function will take action after a date is selected.

    // When the user selects the 'Toggle Reminders' button in the menu bar,
    // (for the purpose of this app) create a work request with a 60 second delay
    // that will check for existing entries for this date in the database.
    // If there is no entry for today, notify the user.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_glucose_button -> {

                // Get the date as of now to open the dialog with today's date pre-selected
                val dateNow = GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).time
                dateDialog = DatePickerFragment.newInstance(dateNow, REQUEST_DATE)
                dateDialog.show(childFragmentManager, REQUEST_DATE)

                // No further processing necessary, mark done by returning true
                true
            }
            R.id.toggle_reminder_button -> {

                // Toggle the reminders enabled flag and set button subtext
                val reminderFlag = Preferences.isReminderEnabled(requireContext())
                Preferences.setReminderEnabled(requireContext(), !reminderFlag)

                if (Preferences.isReminderEnabled(requireContext())) {
                    val workRequest = OneTimeWorkRequest
                        .Builder(ReminderWorker::class.java)
                        .setInitialDelay(60, TimeUnit.SECONDS)
                        .build()
                    WorkManager.getInstance(requireContext()).enqueueUniqueWork(REMINDER_WORK, ExistingWorkPolicy.REPLACE, workRequest)
                    reminderButton.setTitle(R.string.reminder_enabled)
                    Toast.makeText(context, "Reminders enabled!", Toast.LENGTH_SHORT).show()
                } else {
                    WorkManager.getInstance(requireContext()).cancelAllWork()
                    reminderButton.setTitle(R.string.reminder_disabled)
                    Toast.makeText(context, "Reminders disabled!", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // User has selected a new date from the DateDialog
    // When the 'Add Glucose Entry' button is pressed and the user selects a date they want to use,
    // check if any entries exist for that date. If not, create a new entry and load it now.
    // If yes, load that existing entry and do not overwrite it.
    override fun onFragmentResult(requestCode: String, result: Bundle) {
        when (requestCode) {
            REQUEST_DATE -> {

                // Extract the user's selected date
                val selectedDate = DatePickerFragment.getSelectedDate(result)

                // Close the date dialog now
                dateDialog.dismiss()

                // If an entry exists with the selected date, request that it be loaded into the input fragment
                // If an entry does not exist, add a new entry to the database and load it into the input fragment
                val exists = dbViewModel.checkExists(selectedDate).observe(viewLifecycleOwner, { exists ->
                    if (exists) {
                        Toast.makeText(context, "A previous entry was found with the selected date and is now shown above.", Toast.LENGTH_LONG).show()
                        callbacks?.onGlucoseSelected(selectedDate)
                    } else {
                        // Extract the selected date and create a new glucose object using the date
                        val glucose = Glucose(date = selectedDate)

                        // Add to the database and load it into the input fragment now
                        dbViewModel.addGlucose(glucose)
                        Toast.makeText(context, "New entry created for ${DateFormatter.formatShort(selectedDate)}.", Toast.LENGTH_LONG).show()
                        callbacks?.onGlucoseSelected(glucose.date)
                    }
                })
            }
        }
    }

    private fun updateUI(glucoseList: List<Glucose>) {
        rViewAdapter = GlucoseRViewAdapter(glucoseList)
        rView.adapter = rViewAdapter
    }
}