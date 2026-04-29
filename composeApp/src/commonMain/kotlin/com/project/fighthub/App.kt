package com.project.fighthub

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.fighthub.screens.*
import com.project.fighthub.viewmodels.AuthViewModel
import com.project.fighthub.viewmodels.AuthState
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.util.DebugLogger

enum class Screen { Auth, Discovery, Match, Community, MyFights, Profile, MyProfile }

@Composable
fun App() {
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

    var currentScreen by remember { mutableStateOf(Screen.Auth) }

    val snackbarHostState = remember { SnackbarHostState() }

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
                        onMatchInitiated = { currentScreen = Screen.Match },
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