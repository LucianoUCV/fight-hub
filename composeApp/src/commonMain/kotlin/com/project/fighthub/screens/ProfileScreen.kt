package com.project.fighthub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.project.fighthub.ui.ProfileSlider

@Composable
fun ProfileScreen(onSaveProfile: (Int, Int, Int) -> Unit) {
    var age by remember { mutableStateOf(25f) }
    var height by remember { mutableStateOf(175f) }
    var weight by remember { mutableStateOf(75f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Setup Profile", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 40.dp, bottom = 32.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text("TAP TO ADD", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(48.dp))

        ProfileSlider(label = "Age", value = age, range = 18f..60f, unit = "yrs", onValueChange = { age = it })
        ProfileSlider(label = "Height", value = height, range = 150f..220f, unit = "cm", onValueChange = { height = it })
        ProfileSlider(label = "Weight", value = weight, range = 50f..150f, unit = "kg", onValueChange = { weight = it })

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onSaveProfile(age.toInt(), height.toInt(), weight.toInt()) },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
        ) {
            Text("SAVE PROFILE", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
