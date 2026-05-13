package com.ferosburn.project_management.component.scaffold

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
fun AppScaffold(
    title: String,
    onBack: () -> Unit = {},
    showBackButton: Boolean = true,
    sideContent: @Composable ColumnScope.() -> Unit = {},
    mainContent: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val windowSize = LocalWindowSize.current
    val unifiedContent = @Composable {
        Column(Modifier.scrollable(scrollState, Orientation.Vertical).fillMaxSize().padding(20.dp)) {
            mainContent()
            sideContent()
        }
    }

    when (windowSize) {
        COMPACT -> {
            Column {
                Header(title, showBackButton, onBack)
                unifiedContent()
            }
        }

        MEDIUM -> {
            Row(Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(1f))
                Row(Modifier.widthIn(max = 600.dp)) {
                    if (showBackButton) {
                        IconButton(
                            onBack, Modifier.align(Alignment.Top)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                    unifiedContent()
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        EXPANDED -> {
            Row {
                if (showBackButton) {
                    IconButton(
                        onBack, Modifier.align(Alignment.Top)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
                Column(Modifier.weight(1f)) { mainContent() }
                Column(Modifier.weight(1f)) { sideContent() }
            }
        }
    }
}