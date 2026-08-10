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

    // ==========================================
    // OPERAÇÕES LOCAIS (Sua Coleção Offline)
    // ==========================================

    /**
     * Retorna a coleção inteira.
     * Como é um Flow, a UI vai se atualizar sozinha sempre que um disco novo entrar!
     */
    fun getMyCollection(): Flow<List<VinylRecord>> {
        return vinylDao.getAllVinylRecords()
    }

    suspend fun saveVinylToCollection(vinylRecord: VinylRecord) {
        vinylDao.insertVinylRecord(vinylRecord)
    }

    suspend fun removeVinylFromCollection(vinylRecord: VinylRecord) {
        vinylDao.deleteVinylRecord(vinylRecord)
    }

    // Gerenciamento de Tasks (Ex: "Limpar disco", "Trocar plástico")
    fun getTasksForVinyl(vinylId: Int): Flow<List<Task>> {
        return taskDao.getTasksByVinylRecordId(vinylId)
    }

    suspend fun saveTask(task: Task) {
        taskDao.insertTask(task)
    }

    /**
     * Realiza a busca no servidor do Discogs.
     */
    suspend fun searchDiscogs(query: String): Result<List<SearchResultItem>> {
        return try {
            val response = apiService.searchVinyls(query)
            if (response.isSuccessful) {
                // Retorna a lista de resultados, ou uma lista vazia caso seja nulo
                Result.success(response.body()?.results ?: emptyList())
            } else {
                Result.failure(Exception("Erro na busca: Código ${response.code()}"))
            }
        } catch (e: Exception) {
            // Captura erros de rede
            Result.failure(e)
        }
    }
}