package com.icescream.voltianix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomHeader(
    title: String,
    subtitle: String? = null,
    showProfileIcon: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 20.sp
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }

            if (showProfileIcon) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = Color(0xFF45BC75), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
