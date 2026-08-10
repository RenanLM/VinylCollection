package br.com.renan.vinylcollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.renan.vinylcollection.data.local.entity.VinylRecord
import br.com.renan.vinylcollection.data.repository.VinylRepository
import br.com.renan.vinylcollection.data.network.dto.SearchResultItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

// Estado da UI para a busca na API
sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<SearchResultItem>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class VinylViewModel(
    private val repository: VinylRepository
) : ViewModel() {

    // Coleção Local (Room)
    private val _myCollection = MutableStateFlow<List<VinylRecord>>(emptyList())
    val myCollection: StateFlow<List<VinylRecord>> = _myCollection.asStateFlow()

    // Busca Remota (Discogs API)
    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    init {
        loadMyCollection()
    }

    private fun loadMyCollection() {
        viewModelScope.launch {
            repository.getMyCollection()
                .catch { /* Lidar com erro de banco, se necessário */ }
                .collect { records ->
                    _myCollection.value = records
                }
        }
    }

    fun searchVinylOnDiscogs(query: String) {
        viewModelScope.launch {
            _searchState.value = SearchUiState.Loading

            val result = repository.searchDiscogs(query)

            result.onSuccess { items ->
                _searchState.value = SearchUiState.Success(items)
            }.onFailure { exception ->
                _searchState.value = SearchUiState.Error(exception.message ?: "Erro desconhecido")
            }
        }
    }

    fun addVinylToLocalCollection(vinylRecord: VinylRecord) {
        viewModelScope.launch {
            repository.saveVinylToCollection(vinylRecord)
        }
    }
}