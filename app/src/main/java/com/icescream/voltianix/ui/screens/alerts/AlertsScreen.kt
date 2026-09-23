package com.icescream.voltianix.ui.screens.alerts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.icescream.voltianix.data.model.Alert
import com.icescream.voltianix.ui.theme.Green40
import com.icescream.voltianix.ui.theme.LightBlue40
import com.icescream.voltianix.ui.theme.Red40
import com.icescream.voltianix.ui.theme.VoltianixTheme
import com.icescream.voltianix.ui.theme.Yellow40

val HexagonShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    val centerX = width / 2f
    val centerY = height / 2f
    val radius = minOf(width, height) / 2f

    moveTo(centerX, centerY - radius)
    lineTo(centerX + radius * 0.866f, centerY - radius * 0.5f)
    lineTo(centerX + radius * 0.866f, centerY + radius * 0.5f)
    lineTo(centerX, centerY + radius)
    lineTo(centerX - radius * 0.866f, centerY + radius * 0.5f)
    lineTo(centerX - radius * 0.866f, centerY - radius * 0.5f)
    close()
}

@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier,
    viewModel: AlertsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is AlertsUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Green40)
            }
        }
        is AlertsUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error de conexión:\n${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }
        }
        is AlertsUiState.Success -> {
            AlertsScreenContent(
                modifier = modifier,
                alerts = state.alerts
            )
        }
    }
}

@Composable
fun AlertsScreenContent(
    modifier: Modifier = Modifier,
    alerts: List<Alert>
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay alertas registradas",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                HistoryTab()

                val sectionsOrder = listOf("Nuevo", "Esta Semana", "Este Mes", "Mes Pasado")

                sectionsOrder.forEach { sectionTitle ->
                    val sectionAlerts = alerts.filter { it.section.equals(sectionTitle, ignoreCase = true) }

                    if (sectionAlerts.isNotEmpty()) {
                        SectionDivider(
                            title = sectionTitle,
                            itItsNew = sectionTitle.equals("Nuevo", ignoreCase = true)
                        )

                        sectionAlerts.forEach { alert ->
                            AlertCard(
                                title = alert.title,
                                description = alert.description,
                                time = alert.time,
                                date = alert.date,
                                colorStatus = parseColorStatus(alert.colorStatusType)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlertCard(
    title: String,
    description: String,
    time: String,
    date: String,
    colorStatus: Color,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorStatus),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(HexagonShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PriorityHigh,
                        contentDescription = null,
                        tint = colorStatus,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = time,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                )
            }
        }
    }
}

@Composable
fun HistoryTab() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Historial",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Composable
fun SectionDivider(title: String, itItsNew: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 24.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (itItsNew) {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(12.dp)
                    .background(color = Color.Red, shape = CircleShape)
            )
        }

        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
        )
    }
}

private fun parseColorStatus(type: String): Color {
    return when (type.uppercase()) {
        "GREEN" -> Green40
        "YELLOW" -> Yellow40
        "BLUE" -> LightBlue40
        "RED" -> Red40
        else -> Green40
    }
}

@Preview(showBackground = true)
@Composable
fun AlertCardPreview() {
    VoltianixTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AlertCard(
                title = "Diagnóstico Correcto",
                description = "No se detectaron incidencias durante la última revisión.",
                time = "11:22 PM",
                date = "Agosto 04, 2026",
                colorStatus = Green40
            )
        }
    }
}