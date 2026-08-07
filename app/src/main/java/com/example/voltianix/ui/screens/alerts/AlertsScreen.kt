package com.icescream.voltianix.ui.screens.alerts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.icescream.voltianix.ui.theme.Blue40
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

    moveTo(centerX, centerY - radius) // Top point
    lineTo(centerX + radius * 0.866f, centerY - radius * 0.5f)
    lineTo(centerX + radius * 0.866f, centerY + radius * 0.5f)
    lineTo(centerX, centerY + radius)
    lineTo(centerX - radius * 0.866f, centerY + radius * 0.5f)
    lineTo(centerX - radius * 0.866f, centerY - radius * 0.5f)
    close()
}

@Composable
fun AlertsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Light background to contrast card
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HistoryTab()

            SectionDivider("Nuevo", true)

            AlertCard(
                title = "Diagnóstico Correcto",
                description = "No se detectaron incidencias durante la última revisión.",
                time = "11:22 PM",
                date = "Agosto 04, 2026",
                colorStatus = Green40
            )

            SectionDivider("Esta Semana")

            AlertCard(
                title = "Mantenimiento Próximo",
                description = "Faltan 450 km para el mantenimiento preventivo programado.",
                time = "10:33 PM",
                date = "Agosto 01, 2026",
                colorStatus = Yellow40
            )

            SectionDivider("Este Mes")

            AlertCard(
                title = "Carga Completada",
                description = "La batería alcanzó el 100% de carga.",
                time = "1:12 PM",
                date = "Julio 29, 2026",
                colorStatus = LightBlue40
            )

            AlertCard(
                title = "Baja Presión en Neumáticos",
                description = "Se detectó una presión inferior a la recomendada en uno o más neumáticos.",
                time = "5:39 PM",
                date = "Julio 25, 2026",
                colorStatus = Red40
            )


            AlertCard(
                title = "Ruta Actualizada",
                description = "Se ha asignado una nueva ruta para tu recorrido.",
                time = "12:07 PM",
                date = "Julio 21, 2026",
                colorStatus = LightBlue40
            )

            SectionDivider("Mes Pasado")

            AlertCard(
                title = "Próxima Recarga",
                description = "La batería ha descendido al 30%. Se recomienda planificar una " +
                        "recarga en los próximos kilómetros.La batería ha descendido al 30%. Se " +
                        "recomienda planificar una recarga en los próximos kilómetros.",
                time = "8:45 PM",
                date = "Junio 28, 2026",
                colorStatus = Yellow40
            )
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
            // Icon section
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

            // Text section
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
fun HistoryTab(){
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
