package com.project.fighthub.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen() {
    var message by remember { mutableStateOf("") }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF5F5F5),
            Color(0xFFE0E0E0),
            Color(0xFF8B0000).copy(alpha = 0.8f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🥊", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(48.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = (-40).dp, y = 10.dp)
                            .rotate(-10f)
                            .size(width = 150.dp, height = 220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.DarkGray)
                            .zIndex(1f)
                    ) {
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = 40.dp, y = (-10).dp)
                            .rotate(10f)
                            .size(width = 150.dp, height = 220.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Gray)
                            .zIndex(2f)
                    ) {
                    }

                    Text(
                        text = "VS",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.zIndex(3f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "IT'S ON",
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFC0392B),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "You have 24 hours at hand to set up a location\nfor the meeting.",
                    textAlign = TextAlign.Center,
                    color = Color(0xFF5D1512),
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }

            TextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Write a message...", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(28.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }
    }
}