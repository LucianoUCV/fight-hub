package com.project.fighthub.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyFightsScreen() {
    val fights = listOf(
        Triple("Mihai", "Won", "12 Oct 2023"),
        Triple("Alexandru", "Lost", "05 Oct 2023"),
        Triple("Ion", "Disputed", "01 Oct 2023")
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Fight History", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))

        LazyColumn {
            items(fights) { fight ->
                val statusColor = when(fight.second) {
                    "Won" -> Color(0xFF4CAF50)
                    "Lost" -> Color(0xFFF44336)
                    else -> Color(0xFFFF9800)
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("vs ${fight.first}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(fight.third, fontSize = 14.sp, color = Color.Gray)
                        }
                        Text(
                            text = fight.second,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = statusColor
                        )
                    }
                }
            }
        }
    }
}