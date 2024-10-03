package io.github.junrdev.hiddengems.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.junrdev.hiddengems.data.model.Gem

const val dbname = "hiddengemdb"

@Database(
    entities = [Gem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HiddenGemDb : RoomDatabase() {

    @Volatile
    private var db: HiddenGemDb? = null

    fun getDb(context: Context) = db ?: synchronized(this) {
        db = buildDatabase(context)
        db!!
    }

    private fun buildDatabase(context: Context) =
        Room.databaseBuilder(context, HiddenGemDb::class.java, dbname)
            .build()

}