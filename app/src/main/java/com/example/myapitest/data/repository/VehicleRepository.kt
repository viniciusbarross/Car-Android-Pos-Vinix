package com.example.myapitest.data.repository

import android.net.Uri
import android.util.Log
import com.example.myapitest.data.api.ApiService
import com.example.myapitest.data.api.safeApiCall
import com.example.myapitest.data.model.Vehicle
import com.example.myapitest.data.api.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class VehicleRepository(
    private val apiService: ApiService
) {

    suspend fun updateVehicle(vehicle: Vehicle) {
        val response = safeApiCall { apiService.editVehicle(vehicle.id, vehicle) }
        when (response) {
            is Result.Success -> Log.d("VehicleRepository", "Vehicle updated successfully")
            is Result.Error -> Log.e("VehicleRepository", "Error updating vehicle: ${response.message}")
        }
    }

    suspend fun addNewVehicle(vehicle: Vehicle) {
        val updatedVehicle = vehicle.copy(
            id = UUID.randomUUID().toString(),
            imageUrl = "https://example.com/vehicle4.jpg"
        )
        val response = safeApiCall { apiService.addVehicle(updatedVehicle) }
        when (response) {
            is Result.Success -> Log.d("VehicleRepository", "Vehicle added successfully")
            is Result.Error -> Log.e("VehicleRepository", "Error adding vehicle: ${response.message}")
        }
    }

    suspend fun fetchVehicles(): List<Vehicle>? = withContext(Dispatchers.IO) {
        val result = safeApiCall { apiService.getVehicles() }
        when (result) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.e("VehicleRepository", "Error fetching vehicles: ${result.message}")
                null
            }
        }
    }

    suspend fun removeVehicle(vehicleId: String) {
        val result = safeApiCall { apiService.deleteVehicle(vehicleId) }
        when (result) {
            is Result.Success -> Log.d("VehicleRepository", "Vehicle removed successfully")
            is Result.Error -> Log.e("VehicleRepository", "Error removing vehicle: ${result.message}")
        }
    }

    suspend fun fetchVehicleById(vehicleId: String): Vehicle? {
        val result = safeApiCall {
            apiService.getVehicles().find { it.id == vehicleId }
        }
        return when (result) {
            is Result.Success -> result.data
            is Result.Error -> {
                Log.e("VehicleRepository", "Error fetching vehicle by ID: ${result.message}")
                null
            }
        }
    }

}
