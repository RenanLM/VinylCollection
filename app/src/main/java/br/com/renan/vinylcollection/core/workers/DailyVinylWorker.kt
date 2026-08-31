package br.com.renan.vinylcollection.core.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import br.com.renan.vinylcollection.core.notifications.VinylNotificationManager
import br.com.renan.vinylcollection.data.local.dao.VinylRecordDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyVinylWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val vinylDao: VinylRecordDao,
    private val notificationManager: VinylNotificationManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("DailyVinylWorker", "Executando worker diário...")
        return try {
            val randomRecord = vinylDao.getRandomVinylRecord()
            Log.d("DailyVinylWorker", "Disco sorteado: ${randomRecord?.title ?: "Nenhum"}")

            if (randomRecord != null) {
                notificationManager.showDailyVinylNotification(
                    vinylId = randomRecord.id,
                    title = randomRecord.title,
                    artist = randomRecord.artist
                )
            }

            Result.success()
        } catch (_: Exception) {
            Log.e("DailyVinylWorker", "Erro ao executar worker")
            Result.retry()
        }
    }
}