package com.project.fighthub.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.fighthub.ui.GlassTextField
import com.project.fighthub.viewmodels.AuthState

@Composable
fun AuthScreen(
    authState: AuthState,
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: (String, String, String) -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading = authState is AuthState.Loading

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))
    ) {
        Box(
            modifier = Modifier.align(Alignment.TopEnd).size(400.dp)
                .background(Brush.radialGradient(colors = listOf(Color(0xFFE53935).copy(alpha = 0.15f), Color.Transparent), radius = 500f))
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp).animateContentSize(animationSpec = tween(durationMillis = 300)),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isLoginMode) "Welcome\nBack." else "Join the\nFight.",
                color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black, lineHeight = 52.sp, modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(modifier = Modifier.padding(bottom = 48.dp)) {
                Text(text = if (isLoginMode) "New challenger? " else "Already a fighter? ", color = Color.Gray, fontSize = 16.sp)
                Text(
                    text = if (isLoginMode) "Create account" else "Log in",
                    color = Color(0xFFE53935), fontSize = 16.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { isLoginMode = !isLoginMode }
                )
            }

            if (!isLoginMode) {
                GlassTextField(value = name, onValueChange = { name = it }, label = "Fighter Name", placeholder = "e.g. Dan Boicea")
            }

            GlassTextField(value = email, onValueChange = { email = it }, label = "Email Address", placeholder = "hello@fighthub.com")
            GlassTextField(value = password, onValueChange = { password = it }, label = "Password", placeholder = "••••••••", isPassword = true)

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    if (isLoginMode) onLoginClick(email, password) else onSignUpClick(name, email, password)
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(60.dp).shadow(if (isLoading) 0.dp else 16.dp, RoundedCornerShape(30.dp), spotColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    disabledContainerColor = Color(0xFFE53935).copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(text = if (isLoginMode) "ENTER ARENA" else "CREATE ACCOUNT", fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = Color.White)
                }
            }
        }
    }
}