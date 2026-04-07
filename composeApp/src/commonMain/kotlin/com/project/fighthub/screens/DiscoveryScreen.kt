package com.project.fighthub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class UserProfile(val name: String, val age: Int, val details: String, val distance: String)

@Composable
fun DiscoveryScreen(onMatchFound: () -> Unit) {
    val profiles = listOf(
        UserProfile("John", 38, "1.72m, 73kg", "2km away"),
        UserProfile("Mike", 29, "1.85m, 85kg", "5km away"),
        UserProfile("Alex", 31, "1.80m, 78kg", "1km away")
    )
    var currentIndex by remember { mutableStateOf(0) }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFF0F0F0), Color(0xFFD7D7D7))
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient).padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("HUB APP", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF8B0000))
                Text("0 POINTS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8B0000))
            }

            if (currentIndex < profiles.size) {
                val currentProfile = profiles[currentIndex]

                Card(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
                        Box(modifier = Modifier.fillMaxSize().background(
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)), startY = 500f)
                        ))

                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${currentProfile.name}, ${currentProfile.age}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                Text(currentProfile.distance, color = Color.White, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(currentProfile.details, color = Color.White.copy(alpha = 0.8f), fontSize = 18.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { currentIndex++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.size(80.dp)
                    ) { Text("X", color = Color.Red, fontSize = 24.sp, fontWeight = FontWeight.Bold) }

                    Button(
                        onClick = onMatchFound,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.size(80.dp)
                    ) { Text("V", color = Color.Green, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Nu mai există utilizatori în zonă.", fontSize = 18.sp, color = Color.Gray)
                }
            }
        }
    }
}