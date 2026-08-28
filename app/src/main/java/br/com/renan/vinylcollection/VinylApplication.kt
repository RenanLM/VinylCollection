package br.com.renan.vinylcollection

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import br.com.renan.vinylcollection.core.workers.DailyVinylWorker
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class VinylApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        scheduleDailyVinylWork()
    }

    private fun scheduleDailyVinylWork() {
        val now = LocalDateTime.now()
        val targetTime = LocalTime.of(20, 0)
        var target = LocalDateTime.of(now.toLocalDate(), targetTime)

        if (now.isAfter(target)) {
            target = target.plusDays(1)
        }

        val initialDelay = Duration.between(now, target).toMillis()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyVinylWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        Log.d("VinylApplication", "Agendando DailyVinylWorker para $targetTime. Delay inicial: ${initialDelay / 1000 / 60} min")

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyVinylRoulette",
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )

        WorkManager.getInstance(this).cancelUniqueWork("DailyVinylRouletteTest")
    }
}