package com.example.lastmiledelivery.viewmodels.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lastmiledelivery.data.models.Cities
import com.example.lastmiledelivery.data.repository.common.CitiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(private val repository: CitiesRepository):ViewModel() {

    private val _cities= MutableStateFlow<List<Cities>>(emptyList())
    val cities :StateFlow<List<Cities>> = _cities

    init {
        getAllCities()
    }

    fun getAllCities(){
        viewModelScope.launch {
repository.getAllCities()?.let {
    _cities.value=it
}
        }
    }
}