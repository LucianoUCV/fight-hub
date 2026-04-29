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
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun DiscoveryScreen(
    userProfile: Profile?,
    currentProfile: Profile?,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    isLocationRequired: Boolean,
    onProfileClick: () -> Unit,
    userLat: Double? = userProfile?.lat,
    userLng: Double? = userProfile?.lng
) {
    var offsetX by remember { mutableStateOf(0f) }

    LaunchedEffect(currentProfile?.id) {
        offsetX = 0f
    }

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

            if (currentProfile != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .offset { IntOffset(offsetX.roundToInt(), 0) }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    if (offsetX > 250) { onSwipeRight() }
                                    else if (offsetX < -250) { onSwipeLeft() }
                                    offsetX = 0f
                                },
                                onDrag = { change, dragAmount -> change.consume(); offsetX += dragAmount.x }
                            )
                        }
                        .shadow(20.dp, RoundedCornerShape(32.dp))
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.DarkGray)
                ) {
                    if (currentProfile.avatarUrl != null) {
                        AsyncImage(
                            model = currentProfile.avatarUrl,
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)), startY = 500f)))
                    Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
                        val displayName = currentProfile.name ?: "Unknown"
                        val displayAge = currentProfile.age ?: "--"
                        Text("$displayName, $displayAge", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            val displayHeight = currentProfile.height ?: "--"
                            val displayWeight = currentProfile.weight ?: "--"
                            Text("$displayHeight cm • $displayWeight kg", color = Color.LightGray, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        }
                        val proximityText = remember(userLat, userLng, currentProfile.lat, currentProfile.lng) {
                            formatProximity(userLat, userLng, currentProfile.lat, currentProfile.lng)
                        }
                        if (proximityText != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(proximityText, color = Color.LightGray, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        if (isLocationRequired) {
                            Text("Location required", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Enable location to discover nearby fighters.", fontSize = 16.sp, color = Color.Gray)
                        } else {
                            Text("You've caught up!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Check back later for new challengers.", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

private fun formatProximity(userLat: Double?, userLng: Double?, otherLat: Double?, otherLng: Double?): String? {
    if (userLat == null || userLng == null || otherLat == null || otherLng == null) return null
    val distanceKm = distanceKm(userLat, userLng, otherLat, otherLng)
    return if (distanceKm < 1) {
        "${(distanceKm * 1000).toInt()} m away"
    } else {
        "${"%.1f".format(distanceKm)} km away"
    }
}

private fun distanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2.0)
    val c = 2 * asin(sqrt(a))
    return earthRadiusKm * c
}
