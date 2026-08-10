package br.com.renan.vinylcollection.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.renan.vinylcollection.ui.viewmodel.VinylViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: VinylViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    // Coleta o estado da coleção local
    val myCollection by viewModel.myCollection.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minha Coleção") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToSearch) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Disco")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (myCollection.isEmpty()) {
                Text("Sua coleção está vazia.")
                Text("Toque no botão + para pesquisar.")
            } else {
                Text("Você tem ${myCollection.size} disco(s) na coleção!")
            }
        }
    }
}