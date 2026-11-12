package com.hydra.tunnel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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

private val HydraBackground = Color(0xFF080B12)
private val HydraSurface = Color(0xFF111A27)
private val HydraBlue = Color(0xFF58B8FF)
private val HydraMuted = Color(0xFF91A2B8)
private val HydraGreen = Color(0xFF26C89B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HydraApp() }
    }
}

private enum class AppTab(val label: String) {
    HOME("Home"), CONFIGS("Configs"), SETTINGS("Settings")
}

@Composable
private fun HydraApp() {
    val context = LocalContext.current
    val profileStore = remember(context) { ProfileStore(context) }
    var selectedTab by remember { mutableStateOf(AppTab.HOME) }
    var activeProfile by remember {
        mutableStateOf(profileStore.load().firstOrNull { it.id == profileStore.selectedProfileId() })
    }
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = HydraBlue,
            secondary = HydraGreen,
            background = HydraBackground,
            surface = HydraSurface,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Scaffold(
            containerColor = HydraBackground,
            bottomBar = {
                NavigationBar(containerColor = HydraSurface) {
                    AppTab.entries.forEach { tab ->
                        val icon = when (tab) {
                            AppTab.HOME -> Icons.Default.Home
                            AppTab.CONFIGS -> Icons.Default.Tune
                            AppTab.SETTINGS -> Icons.Default.Settings
                        }
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            icon = { Icon(icon, contentDescription = tab.label) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().background(HydraBackground)
                    .padding(padding).verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Header()
                Spacer(Modifier.height(24.dp))
                when (selectedTab) {
                    AppTab.HOME -> HomeScreen(
                        activeProfile = activeProfile,
                        onOpenConfigs = { selectedTab = AppTab.CONFIGS }
                    )
                    AppTab.CONFIGS -> ProfileManagerScreen(profileStore) { selected ->
                        activeProfile = selected
                    }
                    AppTab.SETTINGS -> SettingsScreen()
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFF172B43)) {
            Text("H", modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                color = HydraBlue, fontSize = 25.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text("HYDRA TUNNEL", fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
            Text("PRIVATE CONFIGURATION CLIENT", color = HydraMuted, fontSize = 10.sp)
        }
        Spacer(Modifier.weight(1f))
        Surface(shape = CircleShape, color = Color(0xFF392B18)) {
            Text("DEV", modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                color = Color(0xFFFFC46B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HomeScreen(activeProfile: HydraProfile?, onOpenConfigs: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("YOUR CONNECTION", color = HydraMuted, fontSize = 11.sp, letterSpacing = 1.4.sp)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = HydraSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(shape = CircleShape, color = Color(0xFF172B43), modifier = Modifier.size(112.dp)) {
                    BoxCenter { Text("◇", color = HydraBlue, fontSize = 54.sp) }
                }
                Spacer(Modifier.height(16.dp))
                Text("Engine not connected", fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                Text("Slipstream integration is under development", color = HydraMuted, fontSize = 12.sp)
                Spacer(Modifier.height(18.dp))
                Button(onClick = onOpenConfigs, modifier = Modifier.fillMaxWidth()) {
                    Text("Open configurations")
                }
                Text("Connection is disabled until the VPN engine and TUN bridge are implemented.",
                    color = HydraMuted, fontSize = 10.sp)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("DOWNLOAD", "—", Modifier.weight(1f))
            MetricCard("UPLOAD", "—", Modifier.weight(1f))
        }
        Card(colors = CardDefaults.cardColors(containerColor = HydraSurface)) {
            Column(Modifier.padding(16.dp)) {
                Text("ACTIVE CONFIGURATION", color = HydraMuted, fontSize = 10.sp)
                Spacer(Modifier.height(6.dp))
                Text(activeProfile?.name ?: "No configuration selected", fontWeight = FontWeight.Medium)
                if (activeProfile != null) {
                    Text(activeProfile.domain, color = HydraMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun BoxCenter(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = { content() }
    )
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = HydraSurface)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, color = HydraMuted, fontSize = 10.sp)
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SettingsScreen() {
    var autoConnect by remember { mutableStateOf(false) }
    var diagnostics by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("APP PREFERENCES", color = HydraMuted, fontSize = 11.sp, letterSpacing = 1.2.sp)
        PreferenceRow("Auto-connect", "Connect when the selected profile is available", autoConnect) { autoConnect = it }
        PreferenceRow("Verbose diagnostics", "Enable additional troubleshooting logs", diagnostics) { diagnostics = it }
        Card(colors = CardDefaults.cardColors(containerColor = HydraSurface)) {
            Column(Modifier.padding(16.dp)) {
                Text("Hydra Tunnel", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Version 0.1.0 · Early development", color = HydraMuted, fontSize = 12.sp)
                Text("Slipstream Rust adapter: not integrated", color = HydraMuted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PreferenceRow(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(subtitle, color = HydraMuted, fontSize = 11.sp)
        }
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}
