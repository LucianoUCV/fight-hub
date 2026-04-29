package com.project.fighthub.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import com.project.fighthub.data.model.Profile
import com.project.fighthub.ui.ProfileSlider
import com.project.fighthub.ui.GlassTextField

@Composable
fun ProfileScreen(
    currentProfile: Profile?,
    onSaveProfile: (String, Int, Int, Int, ByteArray?) -> Unit,
    onBackClick: () -> Unit
) {
    var displayName by remember { mutableStateOf(currentProfile?.name ?: "") }
    var age by remember { mutableStateOf(currentProfile?.age?.toFloat() ?: 25f) }
    var height by remember { mutableStateOf(currentProfile?.height?.toFloat() ?: 175f) }
    var weight by remember { mutableStateOf(currentProfile?.weight?.toFloat() ?: 75f) }

    var selectedImageBytes by remember { mutableStateOf<ByteArray?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val scope = rememberCoroutineScope()
    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                selectedImageBytes = it
                selectedImageBitmap = it.toImageBitmap()
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF121212)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Edit Profile", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color.DarkGray)
                .border(2.dp, Color(0xFFE53935), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageBitmap != null) {
                Image(bitmap = selectedImageBitmap!!, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else if (currentProfile?.avatarUrl != null) {
                AsyncImage(
                    model = currentProfile.avatarUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(60.dp))
            }
        }

        Text(
            text = "CHANGE PHOTO",
            color = Color(0xFFE53935),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { imagePicker.launch() }
        )

        Spacer(modifier = Modifier.height(40.dp))

        GlassTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = "Fighter Name",
            placeholder = "e.g. Dan Boicea"
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileSlider(label = "Age", value = age, range = 18f..60f, unit = "yrs", onValueChange = { age = it })
        ProfileSlider(label = "Height", value = height, range = 150f..220f, unit = "cm", onValueChange = { height = it })
        ProfileSlider(label = "Weight", value = weight, range = 50f..150f, unit = "kg", onValueChange = { weight = it })

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onSaveProfile(displayName.trim(), age.toInt(), height.toInt(), weight.toInt(), selectedImageBytes) },
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
        ) {
            Text("SAVE CHANGES", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}