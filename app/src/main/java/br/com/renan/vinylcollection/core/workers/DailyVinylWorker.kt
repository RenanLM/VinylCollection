package br.com.renan.vinylcollection.core.workers

import android.content.Context
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
    private val vinylDao: VinylRecordDao, // Injeta o banco de dados
    private val notificationManager: VinylNotificationManager // Injeta o disparador de notificação
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val randomRecord = vinylDao.getRandomVinylRecord()

            if (randomRecord != null) {
                notificationManager.showDailyVinylNotification(
                    title = randomRecord.title,
                    artist = randomRecord.artist
                )
            }

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}