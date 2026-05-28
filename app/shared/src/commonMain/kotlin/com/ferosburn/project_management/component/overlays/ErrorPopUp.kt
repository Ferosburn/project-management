package com.ferosburn.project_management.component.overlays

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BoxScope.ErrorPopUp(
    visible: Boolean,
    message: String,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(),
        exit = slideOutVertically(),
    ) {
        Box(
            Modifier.padding(top = 20.dp).widthIn(max = 400.dp).shadow(10.dp, shape = RoundedCornerShape(12.dp))
                .background(Color.Red, shape = RoundedCornerShape(20.dp)).padding(12.dp),
        ) {
            AnimatedVisibility(
                visible = message.isNotBlank(),
                enter = expandHorizontally(),
                exit = shrinkHorizontally(),
            ) {
                Text(
                    message, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}