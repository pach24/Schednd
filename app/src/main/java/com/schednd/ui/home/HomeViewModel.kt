package com.schednd.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schednd.data.repository.AuthRepository
import com.schednd.data.repository.EventRepository
import com.schednd.data.repository.RecentEventsRepository
import com.schednd.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isAuthReady: Boolean = false,
    val recentEvents: List<Event> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val eventRepository: EventRepository,
    private val recentEventsRepository: RecentEventsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        viewModelScope.launch {
            try {
                authRepository.ensureSignedIn()
                _uiState.value = _uiState.value.copy(isAuthReady = true)
                loadRecentEvents()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch { loadRecentEvents() }
    }

    private suspend fun loadRecentEvents() {
        val codes = recentEventsRepository.getSavedCodes()
        if (codes.isEmpty()) return
        val events = eventRepository.getEvents(codes)
        _uiState.value = _uiState.value.copy(recentEvents = events.sortedByDescending { it.createdAt })
    }
}
