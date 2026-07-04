package br.com.renan.vinylcollection.data.local.dao

import androidx.room.*
import br.com.renan.vinylcollection.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE vinylRecordId = :vinylRecordId")
    fun getTasksByVinylRecordId(vinylRecordId: Int): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}