package br.com.renan.vinylcollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.renan.vinylcollection.ui.navigation.AppNavigation
import br.com.renan.vinylcollection.ui.theme.VinylCollectionTheme
import br.com.renan.vinylcollection.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Instancia o ViewModel no topo do app
            val settingsViewModel = hiltViewModel<SettingsViewModel>()
            // Olha a configuração
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

            // Passa a variável isDarkMode para o tema escolhido
            VinylCollectionTheme(darkTheme = isDarkMode) {
                AppNavigation()
            }
        }
    }
}