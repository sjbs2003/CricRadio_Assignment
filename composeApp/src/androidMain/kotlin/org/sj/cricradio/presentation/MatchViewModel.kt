package org.sj.cricradio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sj.cricradio.data.model.MiniMatchCardResponse
import org.sj.cricradio.data.model.VenueInfoResponse
import org.sj.cricradio.data.repository.MatchRepoImpl
import org.sj.cricradio.data.repository.MatchRepository


sealed interface MatchUiState {
    data object Loading: MatchUiState
    data class Success(
        val miniMatchCard: MiniMatchCardResponse? = null,
        val venueInfo: VenueInfoResponse? = null,
        val webSocketMessage: String = "",
        val isWebSocketConnected: Boolean = false
    ) : MatchUiState
    data class Error(val message: String): MatchUiState
}

class MatchViewModel(
    private val repository: MatchRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<MatchUiState>(MatchUiState.Loading)
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    private val matchKey = "SA_vs_SL_2024-12-05_1732276435.300452"

    init {
        loadMatchData()
        setupWebSocket()
    }

    private fun loadMatchData() {
        viewModelScope.launch {
            try {
                val miniMatchResult = repository.getMiniMatchCard(matchKey)
                val venueInfoResult = repository.getVenueInfo(matchKey)

                when {
                    miniMatchResult.isSuccess && venueInfoResult.isSuccess -> {
                        _uiState.update { currentState ->
                            when(currentState) {
                                is MatchUiState.Success -> currentState.copy(
                                    miniMatchCard = miniMatchResult.getOrNull(),
                                    venueInfo = venueInfoResult.getOrNull()
                                )
                                else -> MatchUiState.Success(
                                    miniMatchCard = miniMatchResult.getOrNull(),
                                    venueInfo = venueInfoResult.getOrNull()
                                )
                            }
                        }
                    }
                    else -> {
                        val error = miniMatchResult.exceptionOrNull()?.message
                            ?: venueInfoResult.exceptionOrNull()?.message
                            ?: "Unknown error occurred"
                        _uiState.value = MatchUiState.Error(error)
                    }
                }
            } catch (e:Exception) {
                _uiState.value = MatchUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun setupWebSocket() {
        viewModelScope.launch {
            repository.connectToWebSocket().onSuccess {
                updateWebSocketMessages(true)
                observeWebSocketMessages()
            }.onFailure { exception ->
                _uiState.update { currentState ->
                    MatchUiState.Error("WebSocket connection failed: ${exception.message}")
                }
            }
        }
    }

    private fun updateWebSocketMessages(isConnected: Boolean) {
        _uiState.update { currentState ->
            when(currentState) {
                is MatchUiState.Success -> currentState.copy(isWebSocketConnected = isConnected)
                else -> MatchUiState.Success(isWebSocketConnected = isConnected)
            }
        }
    }

    private fun observeWebSocketMessages() {
        viewModelScope.launch {
            repository.observeWebSocketMessage().collect { message ->
                _uiState.update { currentState ->
                    when(currentState) {
                        is MatchUiState.Success -> currentState.copy(webSocketMessage = message)
                        else -> MatchUiState.Success(webSocketMessage = message)
                    }
                }
            }
        }
    }

    fun sendWebSocketMessage(message: String) {
        viewModelScope.launch {
            repository.sendMessage(message)
        }
    }

    fun retryLoadingData() {
        loadMatchData()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            (repository as? MatchRepoImpl)?.cleanup()
        }
    }
}