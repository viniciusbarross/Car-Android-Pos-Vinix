package com.example.myapitest.data.api

import com.example.myapitest.data.model.Vehicle
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("car")
    suspend fun getVehicles(): List<Vehicle>

    @POST("car")
    suspend fun addVehicle(@Body vehicle: Vehicle)

    @PATCH("car/{id}")
    suspend fun editVehicle(@Path("id") id: String, @Body vehicle: Vehicle)

    @DELETE("car/{id}")
    suspend fun deleteVehicle(@Path("id") id: String)
}