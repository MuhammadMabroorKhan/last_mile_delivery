package com.example.lastmiledelivery.viewmodels.common

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.Cities
import com.example.lastmiledelivery.data.models.StatusesResponse
import com.example.lastmiledelivery.data.repository.common.CitiesRepository
import com.example.lastmiledelivery.data.repository.common.StatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val repository: StatusRepository
) : ViewModel() {

    private val _statuses = mutableStateOf<StatusesResponse?>(null)
    val statuses: State<StatusesResponse?> = _statuses

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun loadStatuses() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _statuses.value = repository.fetchStatuses()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
