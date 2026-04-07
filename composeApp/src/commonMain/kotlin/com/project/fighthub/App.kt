package com.project.fighthub

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import com.project.fighthub.screens.CreateAccountScreen
import com.project.fighthub.screens.DiscoveryScreen
import com.project.fighthub.screens.MatchScreen
import com.project.fighthub.screens.CommunityScreen
import com.project.fighthub.screens.MyFightsScreen

enum class Screen { Auth, Discovery, Match, Community, MyFights }

@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf(Screen.Auth) }

        Scaffold(
            bottomBar = {
                if (currentScreen != Screen.Auth && currentScreen != Screen.Match) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Text("👥") },
                            label = { Text("Community") },
                            selected = currentScreen == Screen.Community,
                            onClick = { currentScreen = Screen.Community }
                        )
                        NavigationBarItem(
                            icon = { Text("🔍") },
                            label = { Text("Discovery") },
                            selected = currentScreen == Screen.Discovery,
                            onClick = { currentScreen = Screen.Discovery }
                        )
                        NavigationBarItem(
                            icon = { Text("🥊") },
                            label = { Text("My Fights") },
                            selected = currentScreen == Screen.MyFights,
                            onClick = { currentScreen = Screen.MyFights }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    Screen.Auth -> {
                        CreateAccountScreen(
                            onNavigateToLogin = {},
                            onSignUpClick = { _, _, _ -> currentScreen = Screen.Discovery }
                        )
                    }
                    Screen.Discovery -> {
                        DiscoveryScreen(
                            onMatchFound = { currentScreen = Screen.Match }
                        )
                    }
                    Screen.Match -> {
                        MatchScreen()
                    }
                    Screen.Community -> CommunityScreen()
                    Screen.MyFights -> MyFightsScreen()
                }
            }
        }
    }
}