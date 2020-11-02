package edu.arizona.cast.austinramsay.glucosemonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer

/*
 * The main activity provides the main activity for switching between the input view fragment and
 * the history view fragment. Each of these different views provide ways for a user to enter
 * glucose values for that specific day and then store these values for later use. The main activity
 * provides the base foundation for switching between these different functions and outputting
 * various details to interpret values for the user.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var outputView: TextView
    private lateinit var clearButton: Button
    private lateinit var historyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // Initialize our glucose history database
        GlucoseRepository.initialize(this)

        setContentView(R.layout.activity_main)

        // Get both our input view fragment and history view fragment
        val inputFragment = InputFragment()
        val historyFragment = HistoryFragment()

        outputView = findViewById(R.id.output_view)
        clearButton = findViewById(R.id.clear_button)
        historyButton = findViewById(R.id.history_button)

        // Get the shared view model that we use between both fragments
        val model: SharedViewModel by viewModels()

        // View model 'glucose' observer event handler
        // Whenever a glucose calculation is made, the info is stored in the view model. Update the UI
        // according to it's values when it is made.
        model.glucose.observe(this, Observer<Glucose> { newGlucose ->

            if (newGlucose != null) {
                val date = newGlucose.date
                // TODO: should this be fine?
                // val dateStr = date.format(DateTimeFormatter.ofPattern("EEEE MMM dd, YYYY"))
                val dateStr = date.toString()
                /*outputView.text = String.format(
                    "Report for %s:\n\nFasting: %s\nBreakfast: %s\nLunch: %s\nDinner: %s",
                    dateStr,
                    newGlucose.fastingStatus,
                    newGlucose.breakfastStatus,
                    newGlucose.lunchStatus,
                    newGlucose.dinnerStatus)
                 */
                outputView.text = String.format(
                    "Report for %s:\n\nFasting: %d\nBreakfast: %d\nLunch: %d\nDinner: %d",
                    dateStr,
                    newGlucose.fasting,
                    newGlucose.breakfast,
                    newGlucose.lunch,
                    newGlucose.dinner
                )
            }

        })

        // History button event handler
        // Upon clicking 'History', we switch to the history view fragment and add it to the back
        // stack.
        historyButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, historyFragment)
                addToBackStack(null)
                commit()
            }
        }

        // Clear button event handler - clear out all input fields
        clearButton.setOnClickListener {
            model.updateGlucose(null)
            outputView.text = null
        }

        // Set the input fragment as the primary fragment that appears
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, inputFragment)
            commit()
        }
    }
}
