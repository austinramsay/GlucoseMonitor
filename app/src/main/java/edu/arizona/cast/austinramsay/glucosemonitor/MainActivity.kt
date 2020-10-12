package edu.arizona.cast.austinramsay.glucosemonitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var outputView: TextView
    private lateinit var clearButton: Button
    private lateinit var historyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputFragment = InputFragment()
        val historyFragment = HistoryFragment()

        outputView = findViewById(R.id.output_view)
        clearButton = findViewById(R.id.clear_button)
        historyButton = findViewById(R.id.history_button)

        // Get the shared view model
        val model: SharedViewModel by viewModels()

        // View model 'glucose' observer event handler
        // Whenever a glucose calculation is made, the info is stored in the view model. Update the UI
        // according to it's values when it is made.
        model.glucose.observe(this, Observer<Glucose> { newGlucose ->

            if (newGlucose != null) {
                val date = newGlucose.date
                val dateStr = date.format(DateTimeFormatter.ofPattern("EEEE MMM dd, YYYY"))
                outputView.text = String.format(
                    "Report for %s:\n\nFasting: %s\nBreakfast: %s\nLunch: %s\nDinner: %s",
                    dateStr,
                    newGlucose.fastingStatus,
                    newGlucose.breakfastStatus,
                    newGlucose.lunchStatus,
                    newGlucose.dinnerStatus)
            }

        })

        // History button event handler
        historyButton.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, historyFragment)
                addToBackStack(null)
                commit()
            }
        }

        // Clear button event handler
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
