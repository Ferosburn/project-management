package com.ferosburn.project_management.component.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ferosburn.project_management.models.report.ReportRes
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
fun ReportItem(
    userId: String?,
    report: ReportRes,
    onOpenEditDialog: (ReportRes) -> Unit,
    onOpenDeleteDialog: (String) -> Unit,
) {
    Column(
        Modifier.shadow(10.dp, shape = RoundedCornerShape(12.dp))
            .background(Color.White, shape = RoundedCornerShape(12.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        var expanded by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                report.user.name,
                fontWeight = FontWeight.Bold,
                color = if (!report.user.isActive) Color.Gray else Color.Black
            )
            if (!report.user.isActive) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "(Deactivated)", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Spacer(Modifier.weight(1f))
            Text(
                Instant.fromEpochMilliseconds(report.createdAt).toLocalDateTime(TimeZone.currentSystemDefault())
                    .format(LocalDateTime.Format {
                        hour(); char(':'); minute(); char(' ')
                        dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
                        chars(", "); day(); chars(" ")
                        monthName(names = MonthNames.ENGLISH_ABBREVIATED)
                        chars(" "); year()
                    }),
                fontSize = 10.sp, color = Color.Gray,
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (userId == report.user.id) {
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert, contentDescription = "More options"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("Edit") }, onClick = {
                            expanded = false
                            onOpenEditDialog(report)
                        })
                        DropdownMenuItem(text = { Text("Delete") }, onClick = {
                            expanded = false
                            onOpenDeleteDialog(report.id)
                        })
                    }
                }
            }
        }
        Text(
            report.user.division?.name ?: report.user.role.name,
            fontSize = 10.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Text(report.project.name, fontSize = 14.sp)
        Text(report.detail)
    }
}