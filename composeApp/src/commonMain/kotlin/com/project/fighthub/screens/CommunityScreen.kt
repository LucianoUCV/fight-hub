package com.project.fighthub.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.fighthub.viewmodels.CommunityViewModel
import androidx.compose.ui.graphics.Color

@Composable
fun CommunityScreen(viewModel: CommunityViewModel = viewModel { CommunityViewModel() }) {
    val leaderboard by viewModel.leaderboard.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadLeaderboard()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Local Leaderboard", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

        if (leaderboard.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE53935))
            }
        } else {
            LazyColumn {
                itemsIndexed(leaderboard) { index, profile ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${index + 1}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                                Text(profile.name ?: "Unknown Fighter", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            }
                            Text(
                                text = "${profile.eloPoints} pts",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE53935)
                            )
                        }
                    }
                }
            }
        }
    }
}