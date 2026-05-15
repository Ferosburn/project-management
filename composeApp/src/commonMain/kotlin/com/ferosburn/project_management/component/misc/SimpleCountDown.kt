package com.ferosburn.project_management.component.misc

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun SimpleCountDown(time: Duration, onFinish: () -> Unit) {
    var countDown by remember { mutableStateOf(time.inWholeSeconds.toInt()) }

    LaunchedEffect(countDown > 0) {
        while (countDown > 0) {
            delay(1.seconds)
            countDown--
            if (countDown == 0) {
                onFinish()
            }
        }
    }

    Text("$countDown")
}