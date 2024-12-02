package com.example.myapitest.data.model

data class Vehicle(
    val id: String,
    val imageUrl: String,
    val year: String,
    val name: String,
    val licence: String,
    val place: Location,
)

data class Location(
    val lat: Double,
    val long: Double,
)
