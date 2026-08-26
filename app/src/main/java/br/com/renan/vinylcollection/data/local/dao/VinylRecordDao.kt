package br.com.renan.vinylcollection.data.local.dao

import androidx.room.*
import br.com.renan.vinylcollection.data.local.entity.VinylRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface VinylRecordDao {
    @Query("SELECT * FROM vinyl_records ORDER BY title ASC")
    fun getAllVinylRecords(): Flow<List<VinylRecord>>

    @Query("SELECT * FROM vinyl_records WHERE id = :id")
    fun getVinylRecordById(id: Int): Flow<VinylRecord?>

    @Query("SELECT * FROM vinyl_records ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomVinylRecord(): VinylRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVinylRecord(vinylRecord: VinylRecord): Long

    @Update
    suspend fun updateVinylRecord(vinylRecord: VinylRecord)

    @Delete
    suspend fun deleteVinylRecord(vinylRecord: VinylRecord)
}