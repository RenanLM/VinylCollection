package br.com.renan.vinylcollection.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.renan.vinylcollection.data.local.dao.TaskDao
import br.com.renan.vinylcollection.data.local.dao.VinylRecordDao
import br.com.renan.vinylcollection.data.local.entity.Task
import br.com.renan.vinylcollection.data.local.entity.VinylRecord

@Database(
    entities = [VinylRecord::class, Task::class],
    version = 1,
    exportSchema = false
)
abstract class VinylCollectionDatabase : RoomDatabase() {

    abstract fun vinylRecordDao(): VinylRecordDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: VinylCollectionDatabase? = null

        fun getDatabase(context: Context): VinylCollectionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VinylCollectionDatabase::class.java,
                    "vinyl_collection_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}