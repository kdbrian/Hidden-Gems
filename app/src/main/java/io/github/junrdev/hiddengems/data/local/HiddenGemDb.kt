package io.github.junrdev.hiddengems.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.junrdev.hiddengems.data.model.Gem

const val dbname = "hiddengemdb"

@Database(
    entities = [Gem::class],
    version = 2,
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

    companion object{
        private val MIG1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.beginTransaction()
                db.query(
                    """
                       ALTER TABLE Gem
                        ADD COLUMN addedBy TEXT
                    """.trimIndent()
                )
                db.endTransaction()
            }

        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, HiddenGemDb::class.java, dbname)
                .addMigrations(MIG1_2)
                .build()
    }

}