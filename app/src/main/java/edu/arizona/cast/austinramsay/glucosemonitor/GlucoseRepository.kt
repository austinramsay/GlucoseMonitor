package edu.arizona.cast.austinramsay.glucosemonitor

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import edu.arizona.cast.austinramsay.glucosemonitor.database.GlucoseDatabase
import java.util.*

private const val GLUCOSE_DB = "glucose-database"

/*
 * The glucose repository provides a layer of accessibility to the database. Here we define what
 * values we should retrieve or store from the database. We will utilize this in our model to
 * retrieve data to populate the UI.
 */
class GlucoseRepository private constructor(context: Context) {

    private val database: GlucoseDatabase = Room.databaseBuilder(
            context.applicationContext,
            GlucoseDatabase::class.java,
            GLUCOSE_DB).build()

    private val glucoseDao = database.glucoseDao()

    fun getGlucoseList(): LiveData<List<Glucose>> = glucoseDao.getGlucoseList()
    // fun getGlucose(id: UUID): LiveData<Glucose?> = glucoseDao.getGlucose(id)

    companion object {

        private var INSTANCE: GlucoseRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = GlucoseRepository(context)
            }
        }

        fun get(): GlucoseRepository {
            return INSTANCE ?:
            throw IllegalStateException("GlucoseRepository must be initialized.")
        }
    }
}