package com.project.fighthub

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.project.fighthub.location.AndroidLocationProvider
import com.project.fighthub.location.StubLocationProvider

class MainActivity : ComponentActivity() {
    private lateinit var locationProvider: AndroidLocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            locationProvider.onPermissionResult(granted)
        }

        locationProvider = AndroidLocationProvider(this) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setContent {
            App(locationProvider)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(StubLocationProvider)
}