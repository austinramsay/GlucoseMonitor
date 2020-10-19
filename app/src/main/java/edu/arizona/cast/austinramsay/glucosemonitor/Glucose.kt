package edu.arizona.cast.austinramsay.glucosemonitor

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity
data class Glucose(@PrimaryKey var id: UUID = UUID.randomUUID(),
                   var date: LocalDateTime = LocalDateTime.now(),
                   var fasting: Int = 0,
                   var breakfast: Int = 0,
                   var lunch: Int = 0,
                   var dinner: Int = 0) {

    var fastingStatus: String?
    var breakfastStatus: String?
    var lunchStatus: String?
    var dinnerStatus: String?
    var average: Int?
    var overallStatus: String?

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
            date.toLocalDate().toString(),
            fasting,
            breakfast,
            lunch,
            dinner)
    }
 }




