package edu.arizona.cast.austinramsay.glucosemonitor.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import edu.arizona.cast.austinramsay.glucosemonitor.Glucose
import java.util.*

@Dao
interface GlucoseDAO {

    @Query("SELECT * FROM glucose")
    fun getGlucoseList(): LiveData<List<Glucose>>

    @Query("SELECT * FROM glucose WHERE id=(:id)")
    fun getGlucose(id: UUID): LiveData<Glucose?>
}