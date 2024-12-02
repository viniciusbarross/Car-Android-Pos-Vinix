package com.example.myapitest.ui.components

import ImagePickerCircle
import android.Manifest
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.myapitest.R
import com.example.myapitest.data.model.Vehicle
import com.example.myapitest.data.model.Location
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun VehicleForm(
    currentLocation: Location?,
    fusedLocationClient: FusedLocationProviderClient,
    onNameChange: (String) -> Unit = {},
    onYearChange: (String) -> Unit = {},
    onLicenceChange: (String) -> Unit = {},
    onImageChange: (Uri) -> Unit = {},
    onLocationChange: (Location) -> Unit = {},
    vehicle: Vehicle,
    fetchCurrentLocation: (fusedLocationClient: FusedLocationProviderClient) -> Unit = {},
    editableMap: Boolean = true,
    selectedImageUri: Uri?
) {
    Column {
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            ImagePickerCircle(onImageChange, selectedImageUri)
        }
        OutlinedTextField(
            value = vehicle.name,
            onValueChange = { onNameChange(it) },
            label = { Text(text = stringResource(R.string.car_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vehicle.year,
            onValueChange = { onYearChange(it) },
            label = { Text(text = stringResource(R.string.car_year)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vehicle.licence,
            onValueChange = { onLicenceChange(it) },
            label = { Text(text = stringResource(R.string.license_plate)) },
            modifier = Modifier.fillMaxWidth()
        )

        RequestPermission(
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            rationaleMessage = stringResource(R.string.location_permission_message),
            onPermissionGranted = {
                fetchCurrentLocation(fusedLocationClient)
            })

        currentLocation?.let {
            val (lat, long) = if (vehicle.place.lat != 0.0 && vehicle.place.long != 0.0) {
                vehicle.place.lat to vehicle.place.long
            } else {
                it.lat to it.long
            }

            GoogleMapView(
                lat,
                long,
                onMarkerPositionChanged = { place ->
                    onLocationChange(Location(place.latitude, place.longitude))
                },
                editableMap
            )
        }
    }
}
