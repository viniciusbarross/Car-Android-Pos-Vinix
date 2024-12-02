package com.example.myapitest.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun GoogleMapView(
    lat: Double,
    long: Double,
    onMarkerPositionChanged: (LatLng) -> Unit,
    editableMap: Boolean
) {
    var markerPosition by remember { mutableStateOf(LatLng(lat, long)) }

    GoogleMap(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .height(200.dp),
        onMapClick = {
            if (editableMap) {
                markerPosition = it
                onMarkerPositionChanged(it)
            }
        },
        cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(lat, long), 14f)
        }
    ) {
        Marker(
            state = MarkerState(position = markerPosition),
            draggable = true
        )
    }
}
