package com.project.fighthub.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.fighthub.viewmodels.MatchViewModel

@Composable
fun MyFightsScreen(viewModel: MatchViewModel = viewModel { MatchViewModel() }) {
    val fights by viewModel.uiMatches.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFights()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Fight History", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

        if (fights.isEmpty()) {
            Text("No matches yet. Swipe on discovery to find opponents!", color = Color.Gray)
        } else {
            LazyColumn {
                items(fights) { match ->
                    val isCompleted = match.status == "completed"

                    val (statusText, statusColor) = when {
                        !isCompleted -> "ACTIVE" to Color(0xFFFF9800)
                        match.didIWin == true -> "WON" to Color(0xFF4CAF50)
                        else -> "LOST" to Color(0xFFF44336)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Column {
                                    Text("vs ${match.opponentName}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Match ID: ${match.matchId.take(8)}", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text(statusText, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = statusColor)
                            }

                            if (match.status == "active") {
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    Button(
                                        onClick = { viewModel.submitResult(match.matchId, iWon = true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                    ) { Text("I Won", color = Color.White, fontWeight = FontWeight.Bold) }

                                    Button(
                                        onClick = { viewModel.submitResult(match.matchId, iWon = false) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                                    ) { Text("I Lost", color = Color.White, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}