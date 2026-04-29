package com.project.fighthub

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.fighthub.location.LocationProvider
import com.project.fighthub.location.LocationStatus
import com.project.fighthub.location.StubLocationProvider
import com.project.fighthub.screens.*
import com.project.fighthub.viewmodels.AuthViewModel
import com.project.fighthub.viewmodels.AuthState
import com.project.fighthub.viewmodels.DiscoveryViewModel
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.util.DebugLogger
import kotlinx.coroutines.launch

enum class Screen { Auth, Discovery, Match, Community, MyFights, Profile, MyProfile }

@Composable
fun App(locationProvider: LocationProvider = StubLocationProvider) {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .logger(DebugLogger())
            .build()
    }

    val authViewModel: AuthViewModel = viewModel { AuthViewModel() }
    val authState by authViewModel.authState.collectAsState()

    val userProfile by authViewModel.userProfile.collectAsState()

    val discoveryViewModel: DiscoveryViewModel = viewModel { DiscoveryViewModel() }
    val discoveryCurrent by discoveryViewModel.current.collectAsState()
    val isMatch by discoveryViewModel.isMatch.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.Auth) }
    var isLocationRequired by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.AuthenticatedLogin -> {
                authViewModel.fetchProfile()
                currentScreen = Screen.Discovery
            }
            is AuthState.AuthenticatedRegister -> currentScreen = Screen.Profile
            is AuthState.ProfileUpdated -> {
                authViewModel.fetchProfile()
                currentScreen = Screen.Discovery
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Short
                )
                authViewModel.resetError()
            }
            else -> {}
        }
    }

    LaunchedEffect(currentScreen) {
        if (currentScreen == Screen.Discovery) {
            locationProvider.requestLocation { status ->
                when (status) {
                    is LocationStatus.Available -> {
                        isLocationRequired = false
                        authViewModel.updateLocation(status.lat, status.lng)
                        discoveryViewModel.loadCandidates(status.lat, status.lng, radiusKm = 10.0)
                    }
                    LocationStatus.Disabled -> {
                        isLocationRequired = true
                        snackbarScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Location services are disabled. Enable location to discover nearby fighters.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                    LocationStatus.PermissionDenied -> {
                        isLocationRequired = true
                        snackbarScope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Location permission is required to discover nearby fighters.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                    is LocationStatus.Error -> {
                        isLocationRequired = true
                        snackbarScope.launch {
                            snackbarHostState.showSnackbar(
                                message = status.message ?: "Unable to read location.",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(isMatch) {
        if (isMatch) {
            currentScreen = Screen.Match
            discoveryViewModel.clearMatchFlag()
        }
    }

    MaterialTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (currentScreen != Screen.Auth && currentScreen != Screen.Match && currentScreen != Screen.Profile) {
                    NavigationBar(containerColor = Color(0xFF121212)) {
                        NavigationBarItem(
                            icon = { Text("👥") },
                            selected = currentScreen == Screen.Community,
                            onClick = { currentScreen = Screen.Community }
                        )
                        NavigationBarItem(
                            icon = { Text("🔥") },
                            selected = currentScreen == Screen.Discovery,
                            onClick = { currentScreen = Screen.Discovery }
                        )
                        NavigationBarItem(
                            icon = { Text("🥊") },
                            selected = currentScreen == Screen.MyFights,
                            onClick = { currentScreen = Screen.MyFights }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    Screen.Auth -> AuthScreen(
                        authState = authState,
                        onLoginClick = { email, pass -> authViewModel.signIn(email, pass) },
                        onSignUpClick = { name, email, pass -> authViewModel.signUp(name, email, pass) }
                    )
                    Screen.Profile -> ProfileScreen(
                        currentProfile = userProfile,
                        onSaveProfile = { age, height, weight, avatarBytes ->
                            authViewModel.saveProfile(age, height, weight, avatarBytes)
                        },
                        onBackClick = {
                            currentScreen = Screen.MyProfile
                        }
                    )
                    Screen.MyProfile -> {
                        LaunchedEffect(Unit) {
                            authViewModel.fetchProfile()
                        }

                        MyProfileScreen(
                            profile = userProfile,
                            onEditClick = { currentScreen = Screen.Profile },
                            onLogoutClick = {
                                authViewModel.signOut()
                                currentScreen = Screen.Auth
                            }
                        )
                    }
                    Screen.Discovery -> DiscoveryScreen(
                        userProfile = userProfile,
                        currentProfile = discoveryCurrent,
                        onSwipeLeft = { discoveryViewModel.swipeLeft() },
                        onSwipeRight = { discoveryViewModel.swipeRight() },
                        isLocationRequired = isLocationRequired,
                        onProfileClick = { currentScreen = Screen.MyProfile }
                    )
                    Screen.Match -> MatchScreen()
                    Screen.Community -> CommunityScreen()
                    Screen.MyFights -> MyFightsScreen()
                }
            }
        }
    }
}