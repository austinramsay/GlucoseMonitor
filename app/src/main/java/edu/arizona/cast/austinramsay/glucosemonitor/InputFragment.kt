package edu.arizona.cast.austinramsay.glucosemonitor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InputFragment : Fragment(R.layout.input_view) {

    var date: String? = null
    private lateinit var inputDateButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.input_view, container, false)

        inputDateButton = view.findViewById(R.id.input_date_button)

        // Set the date value for the top input button
        date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, YYYY"))
        inputDateButton.text = date

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

}
