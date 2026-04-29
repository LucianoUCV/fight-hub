package com.project.fighthub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.project.fighthub.data.model.Profile

@Composable
fun MyProfileScreen(
    profile: Profile?,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A1A1A))
                .border(2.dp, Color(0xFFE53935), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (profile?.avatarUrl != null) {
                AsyncImage(
                    model = profile.avatarUrl,
                    contentDescription = "My Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = profile?.name ?: "Unknown Fighter",
            color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black
        )
        Text(
            text = "${profile?.age ?: "--"} years old",
            color = Color.Gray, fontSize = 16.sp, modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatCard(title = "HEIGHT", value = profile?.height?.toString() ?: "--", unit = "cm")
            StatCard(title = "WEIGHT", value = profile?.weight?.toString() ?: "--", unit = "kg")
            StatCard(title = "RATING", value = profile?.eloPoints?.toString() ?: "1000", unit = "pts", isHighlight = true)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935))
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("EDIT PROFILE", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935).copy(alpha = 0.1f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Transparent)
        ) {
            Text("LOG OUT", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
        }
    }
}

@Composable
fun StatCard(title: String, value: String, unit: String, isHighlight: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isHighlight) Color(0xFFE53935).copy(alpha = 0.1f) else Color(0xFF1A1A1A),
        border = if (isHighlight) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935)) else null,
        modifier = Modifier.size(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, color = if (isHighlight) Color(0xFFE53935) else Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(text = unit, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(start = 2.dp, bottom = 4.dp))
            }
        }
    }
}