package dev.ventoy.android

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val VentoyColors = darkColorScheme(
    primary = Color(0xFF69A7FF),
    onPrimary = Color(0xFF071B35),
    secondary = Color(0xFF7DD3C7),
    background = Color(0xFF0B0F14),
    surface = Color(0xFF151B23),
    surfaceVariant = Color(0xFF202936),
    onBackground = Color(0xFFE8EDF5),
    onSurface = Color(0xFFE8EDF5),
    onSurfaceVariant = Color(0xFFAAB6C6)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = VentoyColors) { VentoyHomeScreen() } }
    }
}

@Composable
private fun VentoyHomeScreen() {
    val context = LocalContext.current
    val usbManager = remember { context.getSystemService(UsbManager::class.java) }
    var devices by remember { mutableStateOf(usbManager.deviceList.values.toList()) }
    var selectedIso by remember { mutableStateOf<Uri?>(null) }
    val isoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { selectedIso = it }
    )
    val nativeStatus = remember {
        runCatching { NativeBridge.getBridgeStatus() }
            .getOrElse { "Native bridge unavailable: ${it.javaClass.simpleName}" }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("VENTOY", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp,
                            fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                        Text("Android", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Outlined.Settings, contentDescription = "Configuración",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            item {
                Card(shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Usb, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary)
                            Text("  Dispositivos USB", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (devices.isEmpty()) "No se detectan dispositivos USB. Conecta una memoria mediante USB OTG."
                            else "Se detectaron ${devices.size} dispositivo(s) USB.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(14.dp))
                        Button(onClick = { devices = usbManager.deviceList.values.toList() },
                            modifier = Modifier.fillMaxWidth()) { Text("Buscar dispositivos") }
                    }
                }
            }
            items(devices, key = { it.deviceId }) { device -> UsbDeviceCard(device) }
            item {
                Card(shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.FolderOpen, contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary)
                            Text("  Imágenes ISO", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            selectedIso?.lastPathSegment?.substringAfterLast('/') ?: "Ninguna imagen seleccionada",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { isoPicker.launch(arrayOf("*/*")) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Seleccionar imagen")
                        }
                    }
                }
            }
            item {
                Card(shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Preparación USB todavía no habilitada", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Text("Esta versión inicial solo consulta dispositivos USB y permite seleccionar un archivo. No modifica, formatea ni escribe en ninguna unidad.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            nativeStatus,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun UsbDeviceCard(device: UsbDevice) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp)) {
            Text(device.productName?.takeIf { it.isNotBlank() } ?: "Dispositivo USB",
                fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text("Fabricante: ${device.manufacturerName ?: "Desconocido"}",
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("VID: %04X  •  PID: %04X".format(device.vendorId, device.productId),
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
