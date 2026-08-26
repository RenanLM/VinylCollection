package br.com.renan.vinylcollection

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.renan.vinylcollection.ui.navigation.AppNavigation
import br.com.renan.vinylcollection.ui.theme.VinylCollectionTheme
import br.com.renan.vinylcollection.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import br.com.renan.vinylcollection.core.workers.DailyVinylWorker
import java.util.concurrent.TimeUnit
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleDailyVinylWork()

        setContent {
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            val context = LocalContext.current

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { }
                )

                LaunchedEffect(Unit) {
                    if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            VinylCollectionTheme(darkTheme = isDarkMode) {
                AppNavigation()
            }
        }
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

        Log.d("MainActivity", "Agendando DailyVinylWorker para ${targetTime}. Delay inicial: ${initialDelay / 1000 / 60} min")

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "DailyVinylRoulette",
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )

        // Limpa o worker de teste único
        WorkManager.getInstance(applicationContext).cancelUniqueWork("DailyVinylRouletteTest")
    }
}