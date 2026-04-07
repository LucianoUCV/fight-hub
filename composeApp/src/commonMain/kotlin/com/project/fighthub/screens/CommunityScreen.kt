package com.project.fighthub.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommunityScreen() {
    val leaderboard = listOf(
        Pair("Alexandru", 1500),
        Pair("Mihai", 1420),
        Pair("Ion", 1350),
        Pair("Dan", 1200),
        Pair("Andrei", 950)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Local Leaderboard", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

        LazyColumn {
            itemsIndexed(leaderboard) { index, user ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Text(user.first, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            text = "${user.second} pts",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}