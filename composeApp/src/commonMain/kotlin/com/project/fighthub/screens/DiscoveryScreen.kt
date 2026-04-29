package com.project.fighthub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.project.fighthub.data.model.Profile
import kotlin.math.roundToInt

@Composable
fun DiscoveryScreen(
    userProfile: Profile?,
    onMatchInitiated: (Profile) -> Unit,
    onProfileClick: () -> Unit
) {
    val dummyProfiles = listOf(
        Profile(id = "1", email = "john@test.com", name = "John", age = 38, height = 172, weight = 73, eloPoints = 1200),
        Profile(id = "2", email = "mike@test.com", name = "Mike", age = 29, height = 185, weight = 85, eloPoints = 1450)
    )

    var currentIndex by remember { mutableStateOf(0) }
    var offsetX by remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A)).padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("DISCOVER", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 2.sp)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(16.dp), color = Color(0xFFE53935).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "${userProfile?.eloPoints ?: 1000} PTS",
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray)
                            .border(1.dp, Color(0xFFE53935), CircleShape)
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (userProfile?.avatarUrl != null) {
                            AsyncImage(
                                model = userProfile.avatarUrl,
                                contentDescription = "My Profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = "My Profile", tint = Color.White, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }

            if (currentIndex < dummyProfiles.size) {
                val currentProfile = dummyProfiles[currentIndex]
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .offset { IntOffset(offsetX.roundToInt(), 0) }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    if (offsetX > 250) { onMatchInitiated(currentProfile); currentIndex++ }
                                    else if (offsetX < -250) { currentIndex++ }
                                    offsetX = 0f
                                },
                                onDrag = { change, dragAmount -> change.consume(); offsetX += dragAmount.x }
                            )
                        }
                        .shadow(20.dp, RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.DarkGray)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)), startY = 500f)))
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        Text("${currentProfile.name}, ${currentProfile.age}", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${currentProfile.height} cm • ${currentProfile.weight} kg", color = Color.LightGray, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("You've caught up!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Check back later for new challengers.", fontSize = 16.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}