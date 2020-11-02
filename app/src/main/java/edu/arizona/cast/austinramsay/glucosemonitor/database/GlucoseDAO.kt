package edu.arizona.cast.austinramsay.glucosemonitor.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import edu.arizona.cast.austinramsay.glucosemonitor.Glucose
import java.util.*

/*
 * The DAO decides how data is accessed from the database. Here we define what
 * values are queried depending on what function is called in the DAO.
 */
@Dao
interface GlucoseDAO {

    // Select all glucose items in the database
    @Query("SELECT * FROM glucose")
    fun getGlucoseList(): LiveData<List<Glucose>>

    // Select a unique glucose item in the database using an ID
    /*
    @Query("SELECT * FROM glucose WHERE id=(:id)")
    fun getGlucose(id: UUID): LiveData<Glucose?>
     */
}