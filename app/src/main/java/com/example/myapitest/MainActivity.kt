package com.example.myapitest

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.myapitest.navigation.SetupNavGraph
import com.example.myapitest.ui.viewModel.LoginViewModel
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)

        setContent {
            MyApp()
        }
        requestLocationPermission()
        setupView()

    }

    override fun onResume() {
        super.onResume()
        fetchItems()
    }

    private fun setupView() {
        // TODO
    }

    private fun requestLocationPermission() {
        // TODO
    }

    private fun fetchItems() {
        // TODO
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Composable
    fun MyApp() {
        val navController = rememberNavController()
        val loginViewModel = LoginViewModel()

        val startDestination = if (loginViewModel.checkUserAuthentication()) {
            "home"
        } else {
            "login"
        }

        SetupNavGraph(navController = navController, startDestination = startDestination)
    }
}
