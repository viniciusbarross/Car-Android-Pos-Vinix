package com.example.myapitest.ui.screens


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapitest.R
import com.example.myapitest.data.model.Vehicle
import com.example.myapitest.data.model.Location
import com.example.myapitest.ui.components.VehicleForm
import com.example.myapitest.ui.components.LoadingGif
import com.example.myapitest.ui.viewModel.DetailsVehicleViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailsVehicleScreen(
    navController: NavController,
    viewModel: DetailsVehicleViewModel = viewModel(),
    vehicleId: String
) {
    val context = LocalContext.current
    val currentLocation by viewModel.currentLocation.observeAsState()
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val vehicle by viewModel.vehicle.observeAsState(
        Vehicle("", "", "", "", "", Location(0.0, 0.0))
    )
    val isLoading by viewModel.isLoading.observeAsState(false)

    LaunchedEffect(vehicleId) {
        viewModel.fetchVehicleById(vehicleId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Veículo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        if (isLoading) {
            LoadingGif()
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                VehicleFormContent(
                    vehicle = vehicle,
                    currentLocation = currentLocation,
                    fusedLocationClient = fusedLocationClient,
                    viewModel = viewModel,
                    onEditClick = {
                        viewModel.editVehicle(vehicle)
                        redirectHome(navController)
                    },
                    onDeleteClick = {
                        viewModel.deleteVehicle(vehicleId)
                        redirectHome(navController)
                    }
                )
            }
        }
    }
}

@Composable
fun VehicleFormContent(
    modifier: Modifier = Modifier,
    vehicle: Vehicle,
    currentLocation: Location?,
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: DetailsVehicleViewModel,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        val selectedImageUri by viewModel.selectedImageUri.observeAsState()
        VehicleForm(
            currentLocation = currentLocation,
            fusedLocationClient = fusedLocationClient,
            onImageChange = { viewModel.onImageSelected(it) },
            onLocationChange = { viewModel.updateVehicleLocation(it) },
            onLicenceChange = { viewModel.updateVehicleLicence(it) },
            onNameChange = { viewModel.updateVehicleName(it) },
            onYearChange = { viewModel.updateVehicleYear(it) },
            vehicle = vehicle,
            fetchCurrentLocation = { viewModel.fetchCurrentLocation(it) },
            selectedImageUri = selectedImageUri,
            editableMap = true
        )

        Row {
            Button(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .weight(1f),
                onClick = onDeleteClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = Color.White
                )
            ) {
                Text("Excluir Veículo")
            }

            Button(
                modifier = Modifier
                    .padding(top = 16.dp, start = 8.dp)
                    .weight(1f),
                onClick = onEditClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("Salvar")
            }
        }
    }
}

private fun redirectHome(navController: NavController) {
    navController.navigate("home") {
        popUpTo("home") { inclusive = true }
    }
}
