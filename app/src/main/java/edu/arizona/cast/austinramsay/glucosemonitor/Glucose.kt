package edu.arizona.cast.austinramsay.glucosemonitor

import java.time.LocalDate

data class Glucose(val date: LocalDate,
                   val fasting: Int,
                   val breakfast: Int,
                   val lunch: Int,
                   val dinner: Int) {

    val fastingStatus: String?
    val breakfastStatus: String?
    val lunchStatus: String?
    val dinnerStatus: String?

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
    }
 }




