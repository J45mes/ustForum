package com.example.hkustforum.ui.alerts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hkustforum.data.remote.dto.AlertDto
import com.example.hkustforum.data.repo.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val repo: ForumRepository
) : ViewModel() {

    val alerts = mutableStateOf<List<AlertDto>>(emptyList())


    var isRefreshing by mutableStateOf(false); private set

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        isRefreshing = true                       // start spinner
        alerts.value  = repo.listAlerts()
        isRefreshing = false                      // stop spinner
    }

    fun markSeen(id: Long) = viewModelScope.launch {
        repo.markAlertSeen(id)
        refresh()
    }
}
