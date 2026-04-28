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

enum class Screen { Auth, Discovery, Match, Community, MyFights, Profile }

@Composable
fun App() {
    val authViewModel: AuthViewModel = viewModel { AuthViewModel() }
    val authState by authViewModel.authState.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.Auth) }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> currentScreen = Screen.Profile
            is AuthState.ProfileUpdated -> currentScreen = Screen.Discovery
            else -> {}
        }
    }

    MaterialTheme {
        Scaffold(
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
                        onSaveProfile = { age, height, weight ->
                            authViewModel.saveProfile(age, height, weight)
                        }
                    )
                    Screen.Discovery -> DiscoveryScreen(
                        onMatchInitiated = { currentScreen = Screen.Match }
                    )
                    Screen.Match -> MatchScreen()
                    Screen.Community -> CommunityScreen()
                    Screen.MyFights -> MyFightsScreen()
                }
            }
        }
    }
}