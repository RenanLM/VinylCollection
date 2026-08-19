package br.com.renan.vinylcollection.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.renan.vinylcollection.data.local.entity.VinylRecord
import br.com.renan.vinylcollection.ui.viewmodel.SearchUiState
import br.com.renan.vinylcollection.ui.viewmodel.VinylViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    vinylId: Int,
    viewModel: VinylViewModel,
    onBackClick: () -> Unit
) {
    val searchState by viewModel.searchState.collectAsState()
    val context = LocalContext.current

    // Procura o disco específico na lista que já está carregada na memória pelo ViewModel
    val selectedItem = remember(searchState, vinylId) {
        if (searchState is SearchUiState.Success) {
            (searchState as SearchUiState.Success).results.find { it.id == vinylId }
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Disco") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            // Botão p/ salvar o disco no banco local
            FloatingActionButton(
                onClick = {
                    selectedItem?.let { item ->
                        // A API do Discogs costuma retornar "Artista - Título" no campo title.
                        // Tratamento simples dos nomes para salvar no banco local.
                        val parts = item.title.split(" - ", limit = 2)
                        val artist = if (parts.size > 1) parts[0] else "Desconhecido"
                        val title = if (parts.size > 1) parts[1] else item.title

                        val newRecord = VinylRecord(
                            discogsId = item.id,
                            title = title,
                            artist = artist,
                            coverUrl = item.coverImage,
                            barcode = item.barcode?.firstOrNull(),
                            condition = "Novo" // Condição padrão inicial
                        )

                        viewModel.addVinylToLocalCollection(newRecord)
                        Toast.makeText(context, "Disco salvo na sua coleção!", Toast.LENGTH_SHORT).show()
                        onBackClick() // Volta para a tela anterior após salvar
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Salvar na Coleção")
            }
        }
    ) { paddingValues ->
        if (selectedItem == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Erro ao carregar detalhes do disco.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = selectedItem.coverImage,
                    contentDescription = "Capa do Disco",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = selectedItem.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                if (!selectedItem.barcode.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Código de barras: ${selectedItem.barcode.first()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}