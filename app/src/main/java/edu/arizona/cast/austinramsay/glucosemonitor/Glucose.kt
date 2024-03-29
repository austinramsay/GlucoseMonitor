package edu.arizona.cast.austinramsay.glucosemonitor

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Glucose(@PrimaryKey var date:Date = GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR),
    Calendar.getInstance().get(Calendar.MONTH),
    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).time,
                   var fasting:Int = 0,
                   var breakfast:Int = 0,
                   var lunch:Int = 0,
                   var dinner:Int = 0)