package com.ferosburn.project_management.component.scaffold

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ferosburn.project_management.component.header.Header
import com.ferosburn.project_management.utils.LocalWindowSize
import com.ferosburn.project_management.utils.WindowSize.*

@Composable
fun AppScaffold2(
    title: String,
    onBack: () -> Unit = {},
    showBackButton: Boolean = true,
    modifier: Modifier = Modifier,
    overlays: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    val windowSize = LocalWindowSize.current

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        when (windowSize) {
            COMPACT -> {
                Column(modifier.fillMaxSize()) {
                    Header(title, showBackButton, onBack)
                    content()
                }
            }

            MEDIUM -> {
                Row(modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.weight(1f))
                    Row(Modifier.widthIn(max = 800.dp)) {
                        if (showBackButton) {
                            IconButton(
                                onBack, Modifier.align(Alignment.Top).padding(12.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                        }
                        Column(Modifier.weight(1f)) { content() }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            EXPANDED -> {
                Row(modifier.fillMaxSize()) {
                    if (showBackButton) {
                        IconButton(
                            onBack, Modifier.align(Alignment.Top).padding(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                    Column(Modifier.weight(1f)) { content() }
                }
            }
        }

        overlays()
    }
}