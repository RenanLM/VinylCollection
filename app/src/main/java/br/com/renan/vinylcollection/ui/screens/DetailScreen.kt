package br.com.renan.vinylcollection.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.renan.vinylcollection.data.local.entity.VinylRecord
import br.com.renan.vinylcollection.ui.viewmodel.SearchUiState
import br.com.renan.vinylcollection.ui.viewmodel.VinylViewModel
import coil.compose.AsyncImage

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    vinylId: Int,
    viewModel: VinylViewModel,
    onBackClick: () -> Unit
) {
    val searchState by viewModel.searchState.collectAsState()
    val myCollection by viewModel.myCollection.collectAsState()
    val context = LocalContext.current

    val localItem = remember(myCollection, vinylId) {
        myCollection.find { it.id == vinylId }
    }

    val remoteItem = remember(searchState, vinylId) {
        if (localItem == null && searchState is SearchUiState.Success) {
            (searchState as SearchUiState.Success).results.find { it.id == vinylId }
        } else null
    }

    val title = localItem?.title ?: remoteItem?.title ?: ""
    val coverUrl = localItem?.coverUrl ?: remoteItem?.coverImage ?: ""
    val isSaved = localItem != null

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
            if (isSaved) {
                FloatingActionButton(
                    onClick = {
                        localItem.let {
                            viewModel.removeVinylFromLocalCollection(it)
                            Toast.makeText(context, "Disco removido!", Toast.LENGTH_SHORT).show()
                            onBackClick()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover da Coleção")
                }
            } else if (remoteItem != null) {
                FloatingActionButton(
                    onClick = {
                        val parts = remoteItem.title.split(" - ", limit = 2)
                        val artist = if (parts.size > 1) parts[0] else "Desconhecido"
                        val titleParsed = if (parts.size > 1) parts[1] else remoteItem.title

                        val newRecord = VinylRecord(
                            discogsId = remoteItem.id,
                            title = titleParsed,
                            artist = artist,
                            coverUrl = remoteItem.coverImage,
                            barcode = remoteItem.barcode?.firstOrNull(),
                            condition = "Novo",
                            year = remoteItem.year,
                            genre = remoteItem.genre?.joinToString(" • ")
                        )
                        viewModel.addVinylToLocalCollection(newRecord)
                        Toast.makeText(context, "Disco salvo na sua coleção!", Toast.LENGTH_SHORT).show()
                        onBackClick()
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Salvar na Coleção")
                }
            }
        }
    ) { paddingValues ->
        if (localItem == null && remoteItem == null) {
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
                    model = coverUrl,
                    contentDescription = "Capa do Disco",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(16.dp)
                        .shadow(12.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                val displayYear = localItem?.year ?: remoteItem?.year
                val displayGenre = localItem?.genre ?: remoteItem?.genre?.joinToString(" • ")

                if (!displayYear.isNullOrBlank()) {
                    Text(text = "Ano: $displayYear", style = MaterialTheme.typography.bodyLarge)
                }
                if (!displayGenre.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = displayGenre,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (isSaved) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Artista: ${localItem?.artist}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "Condição: ${localItem?.condition}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}