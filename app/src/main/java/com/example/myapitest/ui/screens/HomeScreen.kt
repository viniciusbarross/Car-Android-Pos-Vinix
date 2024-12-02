package com.example.myapitest.ui.screens

import NoRecordsFound
import PicassoImage
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapitest.R
import com.example.myapitest.data.model.Vehicle
import com.example.myapitest.ui.components.LoadingGif
import com.example.myapitest.ui.viewModel.CarsListViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: CarsListViewModel
) {
    val cars = viewModel.cars.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFf5f5f5),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White
                ),
                title = { Text("Carros", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more_options_button)
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = Color.White,
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.logout_button)) },
                            onClick = {
                                expanded = false
                                onLogout(navController)
                            }
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        LoadingGif()
                    }

                    cars.value.isEmpty() -> {
                        NoRecordsFound(stringResource(R.string.no_records_found))
                    }

                    else -> {
                        CarList(cars = cars.value, modifier = Modifier.fillMaxSize(), navController)
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addCar") },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_button))
            }
        },
        floatingActionButtonPosition = FabPosition.End
    )
}

@Composable
fun CarList(cars: List<Vehicle>, modifier: Modifier = Modifier, navController: NavController) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(cars) { car ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        navController.navigate("carDetails/${car.id}")
                    },
                shape = MaterialTheme.shapes.large,
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = car.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF6200EE)
                        )
                    },
                    supportingContent = {
                        Column {
                            Text(text = "Ano: ${car.year}", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Placa: ${car.licence}", style = MaterialTheme.typography.bodyMedium)
                        }
                    },
                    leadingContent = {
                        PicassoImage(
                            url = car.imageUrl,
                            contentDescription = car.name,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.secondary)
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.White
                    ),
                )
            }
        }
    }
}

fun onLogout(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    auth.signOut()

    Toast.makeText(navController.context, "Você saiu", Toast.LENGTH_SHORT).show()

    navController.navigate("login") {
        popUpTo("home") { inclusive = true }
    }
}
