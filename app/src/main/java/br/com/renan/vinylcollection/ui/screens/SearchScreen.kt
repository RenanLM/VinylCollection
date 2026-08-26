package br.com.renan.vinylcollection.ui.screens
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import br.com.renan.vinylcollection.data.network.dto.SearchResultItem
import br.com.renan.vinylcollection.ui.viewmodel.SearchUiState
import br.com.renan.vinylcollection.ui.viewmodel.VinylViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import br.com.renan.vinylcollection.ui.components.BarcodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Close

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: VinylViewModel,
    onBackClick: () -> Unit,
    onVinylClick: (Int) -> Unit
) {
    val searchState by viewModel.searchState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }

    var isScannerVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isScannerVisible = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar na Discogs") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de Pesquisa
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Nome do álbum ou artista...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val isCameraGranted = ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED

                            if (isCameraGranted) {
                                isScannerVisible = true
                            } else {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Escanear código de barras",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            viewModel.searchVinylOnDiscogs(searchQuery)
                        }
                    }
                )
            )

            if (isScannerVisible) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    BarcodeScanner(
                        onBarcodeScanned = { barcode ->
                            isScannerVisible = false
                            searchQuery = barcode
                            viewModel.searchVinylOnDiscogs(barcode)
                        }
                    )
                    IconButton(
                        onClick = { isScannerVisible = false },
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    when (val state = searchState) {
                        is SearchUiState.Idle -> {
                            Text("Digite o nome de um disco para começar.")
                        }
                        is SearchUiState.Loading -> {
                            CircularProgressIndicator()
                        }
                        is SearchUiState.Error -> {
                            Text("Erro: ${state.message}", color = MaterialTheme.colorScheme.error)
                        }
                        is SearchUiState.Success -> {
                            if (state.results.isEmpty()) {
                                Text("Nenhum resultado encontrado.")
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(state.results) { item ->
                                        SearchResultRow(
                                            item = item,
                                            onClick = { onVinylClick(item.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun SearchResultRow(item: SearchResultItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.coverImage,
                contentDescription = "Capa do Disco",
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}