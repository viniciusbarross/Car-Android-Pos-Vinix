package com.example.myapitest.navigation

import AddCarScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapitest.ui.screens.DetailsVehicleScreen
import com.example.myapitest.ui.screens.HomeScreen
import com.example.myapitest.ui.screens.LoginScreen
import com.example.myapitest.ui.viewModel.CarsListViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController(), startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") { LoginScreen(navController) }

        composable("home") {
            val carsListViewModel: CarsListViewModel = viewModel()
            HomeScreen(navController, carsListViewModel)
        }

        composable("carDetails/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId")
            if (carId != null) {
                DetailsVehicleScreen(navController = navController, vehicleId = carId)
            }
        }

        composable("addCar") { AddCarScreen(navController) }
    }
}
