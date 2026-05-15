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
    sideContentOnTop: Boolean = false,
    modifier: Modifier = Modifier,
    sideContent: @Composable ColumnScope.() -> Unit = {},
    mainContent: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val windowSize = LocalWindowSize.current
    val unifiedContent = @Composable {
        Column(Modifier.scrollable(scrollState, Orientation.Vertical).fillMaxSize()) {
            if (sideContentOnTop) {
                sideContent()
                mainContent()
            } else {
                mainContent()
                sideContent()
            }
        }
    }

    when (windowSize) {
        COMPACT -> {
            Column(modifier.fillMaxSize()) {
                Header(title, showBackButton, onBack)
                unifiedContent()
            }
        }

        MEDIUM -> {
            Row(modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(1f))
                Row(Modifier.widthIn(max = 600.dp)) {
                    if (showBackButton) {
                        IconButton(
                            onBack, Modifier.align(Alignment.Top).padding(12.dp)
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
            Row(modifier.fillMaxSize()) {
                if (showBackButton) {
                    IconButton(
                        onBack, Modifier.align(Alignment.Top).padding(12.dp)
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