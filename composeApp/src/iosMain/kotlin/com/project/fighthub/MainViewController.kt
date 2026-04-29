package com.project.fighthub

import androidx.compose.ui.window.ComposeUIViewController
import com.project.fighthub.location.IOSLocationProvider

fun MainViewController() = ComposeUIViewController { App(IOSLocationProvider()) }
