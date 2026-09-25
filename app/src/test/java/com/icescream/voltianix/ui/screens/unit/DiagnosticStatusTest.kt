package com.icescream.voltianix.ui.screens.unit

import com.icescream.voltianix.ui.theme.Green40
import com.icescream.voltianix.ui.theme.Grey40
import com.icescream.voltianix.ui.theme.Red40
import com.icescream.voltianix.ui.theme.Yellow40
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * El punto del diagnóstico se comparaba con `contains`, así que "Normal" contenía "mal" y una
 * unidad sana se pintaba de rojo. Estas pruebas cuidan justo eso.
 */
class DiagnosticStatusTest {

    @Test
    fun `un estado sano se pinta de verde`() {
        assertEquals(Green40, diagnosticColor("Normal"))
        assertEquals(Green40, diagnosticColor("Correcto"))
        assertEquals(Green40, diagnosticColor("Conectado"))
    }

    @Test
    fun `una negacion cancela la palabra de falla`() {
        assertEquals(Green40, diagnosticColor("Sin fallas"))
        assertEquals(Green40, diagnosticColor("No hay errores"))
    }

    @Test
    fun `una falla real se pinta de rojo`() {
        assertEquals(Red40, diagnosticColor("Falla en frenos"))
        assertEquals(Red40, diagnosticColor("Desconectado"))
        assertEquals(Red40, diagnosticColor("Crítico"))
    }

    @Test
    fun `sin conexion es una falla aunque empiece con una negacion`() {
        assertEquals(Red40, diagnosticColor("Sin conexión"))
        assertEquals(Red40, diagnosticColor("Fuera de servicio"))
    }

    @Test
    fun `lo que hay que revisar se pinta de amarillo`() {
        assertEquals(Yellow40, diagnosticColor("Revisar"))
        assertEquals(Yellow40, diagnosticColor("Nivel bajo"))
    }

    @Test
    fun `sin dato el punto queda gris`() {
        assertEquals(Grey40, diagnosticColor(""))
        assertEquals(Grey40, diagnosticColor("   "))
    }

    @Test
    fun `la temperatura se colorea por umbral`() {
        assertEquals(Green40, temperatureColor("31°C"))
        assertEquals(Green40, temperatureColor("31,5 °C"))
        assertEquals(Yellow40, temperatureColor("45°C"))
        assertEquals(Red40, temperatureColor("52°C"))
    }

    @Test
    fun `una temperatura sin numero se lee como texto`() {
        assertEquals(Grey40, temperatureColor(""))
        assertEquals(Red40, temperatureColor("Sensor desconectado"))
    }
}
