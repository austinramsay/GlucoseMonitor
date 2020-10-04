package edu.arizona.cast.austinramsay.glucosemonitor

import java.time.LocalDate

data class Glucose(val date: LocalDate,
                   val fasting: Int,
                   val breakfast: Int,
                   val lunch: Int,
                   val dinner: Int) {

 }




