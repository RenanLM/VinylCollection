package br.com.renan.vinylcollection.data.repository

import br.com.renan.vinylcollection.data.local.dao.TaskDao
import br.com.renan.vinylcollection.data.local.dao.VinylRecordDao
import br.com.renan.vinylcollection.data.local.entity.Task
import br.com.renan.vinylcollection.data.local.entity.VinylRecord
import br.com.renan.vinylcollection.data.network.api.DiscogsApiService
import br.com.renan.vinylcollection.data.network.dto.SearchResultItem
import kotlinx.coroutines.flow.Flow

class VinylRepository(
    private val apiService: DiscogsApiService,
    private val vinylDao: VinylRecordDao,
    private val taskDao: TaskDao
) {

    private var cachedUsername: String? = null

    private suspend fun getRemoteUsername(): String {
        cachedUsername?.let { return it }
        return try {
            val response = apiService.getUserIdentity()
            if (response.isSuccessful && response.body() != null) {
                val username = response.body()!!.username
                cachedUsername = username
                username
            } else {
                "me"
            }
        } catch (_: Exception) {
            "me"
        }
    }

    fun getMyCollection(): Flow<List<VinylRecord>> {
        return vinylDao.getAllVinylRecords()
    }

    suspend fun saveVinylToCollection(vinylRecord: VinylRecord): Long {
        return vinylDao.insertVinylRecord(vinylRecord)
    }

    suspend fun removeVinylFromCollection(vinylRecord: VinylRecord) {
        vinylDao.deleteVinylRecord(vinylRecord)
    }

    fun getTasksForVinyl(vinylId: Int): Flow<List<Task>> {
        return taskDao.getTasksByVinylRecordId(vinylId)
    }

    suspend fun saveTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    /**
     * Realiza a busca no servidor do Discogs.
     */
    suspend fun searchDiscogs(query: String): Result<List<SearchResultItem>> {
        return try {
            val response = apiService.searchVinyls(query)
            if (response.isSuccessful) {
                Result.success(response.body()?.results ?: emptyList())
            } else {
                Result.failure(Exception("Erro na busca: Código ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addVinylToRemoteCollection(releaseId: Int): Result<Long?> {
        return try {
            val username = getRemoteUsername()
            val response = apiService.addReleaseToCollection(username, releaseId)
            if (response.isSuccessful) {
                Result.success(response.body()?.instanceId)
            } else {
                Result.failure(Exception("Error adding to Discogs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeVinylFromRemoteCollection(releaseId: Int, providedInstanceId: Long? = null): Result<Unit> {
        return try {
            val username = getRemoteUsername()

            val targetInstanceId = providedInstanceId ?: run {
                val instancesResponse = apiService.getReleaseInstances(username, releaseId)
                if (instancesResponse.isSuccessful) {
                    instancesResponse.body()?.releases?.firstOrNull()?.instanceId
                } else {
                    null
                }
            } ?: 1L

            val response = apiService.removeReleaseFromCollection(username, releaseId, targetInstanceId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error removing from Discogs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}