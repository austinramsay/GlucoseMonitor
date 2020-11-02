package edu.arizona.cast.austinramsay.glucosemonitor

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/*
 * A Glucose instance contains a unique UUID (useful for our database), the date it was stored on,
 * and glucose values the user input for fasting, breakfast, lunch and dinner times. A status
 * message for each value is calculated and used in various parts of the program to interpret the
 * glucose values for the user.
 */
@Entity
data class Glucose(@PrimaryKey var date: Date = GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).time,
                   var fasting:Int = -1,
                   var breakfast:Int = -1,
                   var lunch:Int = -1,
                   var dinner:Int = -1) {

    companion object {
        const val STATUS_NORMAL = "Normal"
        const val STATUS_ABNORMAL = "Abnormal"
        const val STATUS_HYPOGLYCEMIC= "Hypoglycemic"

        fun getRandomGlucoseLevel(): Int {
            // Return a reasonable glucose level, which would be between 60 and 180
            return (60..180).random()
        }
    }

    // Upon instance creation, calculate the status properties using the provided glucose numbers
    // If fasting levels are less than 70 or above 100 the levels are abnormal
    // For between meals, values should be between 70 and 140
    /*
    init {
        // Set the fasting status
        fastingStatus = if (fasting < 70) {
            STATUS_HYPOGLYCEMIC
        } else if (fasting >= 70 && fasting < 100) {
            STATUS_NORMAL
        } else {
            STATUS_ABNORMAL
        }

        breakfastStatus = if (breakfast < 70) {
            STATUS_HYPOGLYCEMIC
        } else if (breakfast > 140) {
            STATUS_ABNORMAL
        } else {
            STATUS_NORMAL
        }

        lunchStatus = if (lunch < 70) {
            STATUS_HYPOGLYCEMIC
        } else if (lunch > 140) {
            STATUS_ABNORMAL
        } else {
            STATUS_NORMAL
        }

        dinnerStatus = if (dinner < 70) {
            STATUS_HYPOGLYCEMIC
        } else if (dinner > 140) {
            STATUS_ABNORMAL
        } else {
            STATUS_NORMAL
        }

        // Set the overall average values
        val testAverage = ((fasting + breakfast + lunch + dinner) / 4)
        overallStatus = if (testAverage < 70) {
            STATUS_HYPOGLYCEMIC
        } else if (testAverage > 140) {
            STATUS_ABNORMAL
        } else {
            STATUS_NORMAL
        }
        average = testAverage
    }

    override fun toString(): String {
        return String.format(
                "Date: %s\n" +
                "Fasting: %d\n" +
                "Breakfast: %d\n" +
                "Lunch: %d\n" +
                "Dinner: %d",
            date.toString(),
            fasting,
            breakfast,
            lunch,
            dinner)
    }

     */
 }




