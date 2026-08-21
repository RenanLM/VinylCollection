package br.com.renan.vinylcollection.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.renan.vinylcollection.data.local.entity.VinylRecord
import br.com.renan.vinylcollection.data.repository.VinylRepository
import br.com.renan.vinylcollection.data.network.dto.SearchResultItem
import br.com.renan.vinylcollection.core.notifications.VinylNotificationManager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import br.com.renan.vinylcollection.data.preferences.SettingsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

// Estado da UI para a busca na API
sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(val results: List<SearchResultItem>) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@HiltViewModel
class VinylViewModel @Inject constructor(
    private val repository: VinylRepository,
    private val notificationManager: VinylNotificationManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val myCollection: StateFlow<List<VinylRecord>> = combine(
        repository.getMyCollection(),
        settingsRepository.sortOrder
    ) { records, sortOrder ->
        when (sortOrder) {
            "TITLE" -> records.sortedBy { it.title }
            "ARTIST" -> records.sortedBy { it.artist }
            else -> records.sortedByDescending { it.id } // RECENT
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Busca Remota (Discogs API)
    private val _searchState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

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
            notificationManager.showItemSavedNotification(vinylRecord.title)
        }
    }

    fun removeVinylFromLocalCollection(vinylRecord: VinylRecord) {
        viewModelScope.launch {
            repository.removeVinylFromCollection(vinylRecord)
        }
    }
}