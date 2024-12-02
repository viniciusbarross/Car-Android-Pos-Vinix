package com.example.myapitest.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapitest.data.api.RetrofitClient
import com.example.myapitest.data.model.Vehicle
import com.example.myapitest.data.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarsListViewModel(
    private val carRepository: VehicleRepository = VehicleRepository(apiService = RetrofitClient.apiService)
) : ViewModel() {
    private val _cars = MutableStateFlow<List<Vehicle>>(emptyList())
    val cars: StateFlow<List<Vehicle>> = _cars
    val isLoading = MutableStateFlow(true)
    init {
        fetchCars()
    }

    fun fetchCars() {
        viewModelScope.launch {
            carRepository.fetchVehicles()?.let {
                _cars.value = it
            }
            isLoading.value = false
        }
    }
}