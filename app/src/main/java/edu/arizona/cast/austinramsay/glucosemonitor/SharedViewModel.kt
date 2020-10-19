package edu.arizona.cast.austinramsay.glucosemonitor

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.util.*

class SharedViewModel : ViewModel() {
    val glucose = MutableLiveData<Glucose>()
    var glucoseHistory: List<Glucose> = listOf()
    private val glucoseRepository = GlucoseRepository.get()
    val glucoseListLiveData = glucoseRepository.getGlucoseList()
    var fastingStatusColor: Int = Color.BLACK
    var breakfastStatusColor: Int = Color.BLACK
    var lunchStatusColor: Int = Color.BLACK
    var dinnerStatusColor: Int = Color.BLACK

    // Sets a new Glucose instance as the result to be shared across fragments/parent activity
    // Sets the status colors in the input fields according to the result values (green for normal / red otherwise)
    fun updateGlucose(newGlucose: Glucose?) {

        // If the glucose is null, set to default values
        if (newGlucose == null) {
            fastingStatusColor = Color.BLACK
            breakfastStatusColor = Color.BLACK
            lunchStatusColor = Color.BLACK
            dinnerStatusColor = Color.BLACK
        } else {
            // Set the fasting result color
            if (newGlucose.fastingStatus == Glucose.STATUS_NORMAL) {
                fastingStatusColor = Color.rgb(45, 140, 60)
            } else {
                fastingStatusColor = Color.RED
            }

            // Set the breakfast result color
            if (newGlucose.breakfastStatus == Glucose.STATUS_NORMAL) {
                breakfastStatusColor = Color.rgb(45, 140, 60)
            } else {
                breakfastStatusColor = Color.RED
            }

            // Set the lunch result color
            if (newGlucose.lunchStatus == Glucose.STATUS_NORMAL) {
                lunchStatusColor = Color.rgb(45, 140, 60)
            } else {
                lunchStatusColor = Color.RED
            }

            // Set the dinner result color
            if (newGlucose.dinnerStatus == Glucose.STATUS_NORMAL) {
                dinnerStatusColor = Color.rgb(45, 140, 60)
            } else {
                dinnerStatusColor = Color.RED
            }
        }

        // Update the view model glucose (observers will catch this event)
        glucose.value = newGlucose
    }

    // TODO: Remove this function after testing
    fun setRandomGlucose() {

        val today: LocalDateTime = LocalDateTime.now()

        // Begin creating objects at the 100th day prior to today
        val begin: LocalDateTime = today.minusDays(100)

        // Create a mutable list to hold our glucose objects
        val glucoseList = mutableListOf<Glucose>()

        // Create 100 random glucose objects offsetting the beginning date by the loop index
        for (offset in 0..99) {
            val randomGlucose = Glucose(
                UUID.randomUUID(),
                begin.plusDays(offset.toLong()),
                Glucose.getRandomGlucoseLevel(),
                Glucose.getRandomGlucoseLevel(),
                Glucose.getRandomGlucoseLevel(),
                Glucose.getRandomGlucoseLevel()
            )

            glucoseList.add(randomGlucose)
        }

        glucoseHistory = glucoseList
    }
}