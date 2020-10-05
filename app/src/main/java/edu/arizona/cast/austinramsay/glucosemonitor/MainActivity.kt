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

        outputView = findViewById(R.id.output_view)
        clearButton = findViewById(R.id.clear_button)
        historyButton = findViewById(R.id.history_button)

        // Get the shared view model
        val model: SharedViewModel by viewModels()

        // View model 'glucose' observer event handler
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

        // Clear button event handler
        clearButton.setOnClickListener {
            model.updateGlucose(null)

            outputView.text = null
        }
    }
}
