package edu.arizona.cast.austinramsay.glucosemonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InputFragment : Fragment(R.layout.input_view) {

    private val model: SharedViewModel by activityViewModels()
    private lateinit var inputDateButton: Button
    private lateinit var calculateButton: Button
    private lateinit var fastingInput: EditText
    private lateinit var breakfastInput: EditText
    private lateinit var lunchInput: EditText
    private lateinit var dinnerInput: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.input_view, container, false)

        inputDateButton = view.findViewById(R.id.input_date_button)
        calculateButton = view.findViewById(R.id.calculate_button)
        fastingInput = view.findViewById(R.id.et_fasting)
        breakfastInput = view.findViewById(R.id.et_breakfast)
        lunchInput = view.findViewById(R.id.et_lunch)
        dinnerInput = view.findViewById(R.id.et_dinner)

        // Set the date value for the top input button
        val date = LocalDate.now()
        val dateStr = date.format(DateTimeFormatter.ofPattern("MMM dd, YYYY"))
        inputDateButton.text = dateStr

        model.glucose.observe(this, Observer<Glucose> { newGlucose ->

            // Clear input fields if Glucose is cleared and sync colors to view model
            if (newGlucose == null) {
                syncColors()
                clearInputFields()
            }
        })

        // Set the calculate button listener
        calculateButton.setOnClickListener {

            // Extract all input fields for required glucose info
            val fasting = fastingInput.text.toString()
            val breakfast = breakfastInput.text.toString()
            val lunch = lunchInput.text.toString()
            val dinner = dinnerInput.text.toString()

            // Verify required info is filled in
            if (fasting.isBlank() || breakfast.isBlank() || lunch.isBlank() || dinner.isBlank()) {

                // Display user notification that input is invalid/missing
                Toast.makeText(
                    activity,
                    "Please verify input is correct!",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                try {

                    val currentDate = LocalDate.now()

                    // All input is verified, cast to ints and update the view model
                    val glucose = Glucose(
                        currentDate,
                        fasting.toInt(),
                        breakfast.toInt(),
                        lunch.toInt(),
                        dinner.toInt()
                    )

                    model.updateGlucose(glucose)
                    syncColors()

                }  catch (e: Exception) {

                    // Catch number format exceptions if somehow there is text in the number fields
                    Toast.makeText(
                        activity,
                        "Invalid input types for glucose values!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // Sync the input field colors to the view model values
    private fun syncColors() {
        fastingInput.setTextColor(model.fastingStatusColor)
        breakfastInput.setTextColor(model.breakfastStatusColor)
        lunchInput.setTextColor(model.lunchStatusColor)
        dinnerInput.setTextColor(model.dinnerStatusColor)
    }

    // Clear all input fields in the InputFragment
    private fun clearInputFields() {
        fastingInput.text = null
        breakfastInput.text = null
        lunchInput.text = null
        dinnerInput.text = null
    }

}
