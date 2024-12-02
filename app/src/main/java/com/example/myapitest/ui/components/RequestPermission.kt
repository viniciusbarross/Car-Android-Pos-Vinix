package com.example.myapitest.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.*

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermission(
    permission: String,
    rationaleMessage: String,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission)
    LaunchedEffect(permissionState.status) {
        when {
            permissionState.status.isGranted -> {
                onPermissionGranted()
            }
            else -> {
                permissionState.launchPermissionRequest()
            }
        }
    }
}
