package com.project.fighthub.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateAccountScreen(
    onNavigateToLogin: () -> Unit,
    onSignUpClick: (name: String, email: String, pass: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE0E0E0),
            Color(0xFF8B0000),
            Color(0xFF3E0000)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Create\nAccount",
                color = Color(0xFF5D1512),
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 44.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.padding(bottom = 48.dp)) {
                Text(text = "Already registered? ", color = Color.Gray)
                Text(
                    text = "Log in here.",
                    color = Color(0xFF8B0000),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            GlassTextField(
                value = name,
                onValueChange = { name = it },
                label = "NAME",
                placeholder = "Dan Boicea"
            )

            Spacer(modifier = Modifier.height(16.dp))

            GlassTextField(
                value = email,
                onValueChange = { email = it },
                label = "EMAIL",
                placeholder = "hello@app.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            GlassTextField(
                value = password,
                onValueChange = { password = it },
                label = "PASSWORD",
                placeholder = "******",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(48.dp))

            OutlinedButton(
                onClick = { onSignUpClick(name, email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                border = BorderStroke(2.dp, Color.White)
            ) {
                Text(text = "Sign up", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color.White.copy(alpha = 0.6f)) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
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