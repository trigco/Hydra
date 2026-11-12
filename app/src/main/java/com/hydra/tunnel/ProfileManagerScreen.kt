package com.hydra.tunnel

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

private val ProfileMuted = androidx.compose.ui.graphics.Color(0xFF91A2B8)
private val ProfileSurface = androidx.compose.ui.graphics.Color(0xFF111A27)

@Composable
fun ProfileManagerScreen(store: ProfileStore) {
    val context = LocalContext.current
    val profiles = remember { mutableStateListOf<HydraProfile>().apply { addAll(store.load()) } }
    var selectedId by remember { mutableStateOf(store.selectedProfileId()) }
    var editing by remember { mutableStateOf<HydraProfile?>(null) }
    var creating by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            runCatching {
                val json = store.encodeExport(profiles.toList())
                context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray(Charsets.UTF_8)) }
                    ?: error("Could not open the export destination.")
            }.onSuccess { status = "Exported ${profiles.size} profile(s)." }
                .onFailure { status = it.message ?: "Export failed." }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
                    val output = java.io.ByteArrayOutputStream()
                    val buffer = ByteArray(8192)
                    var total = 0
                    while (true) {
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        require(total <= ProfileStore.MAX_IMPORT_BYTES) { "Import file is too large." }
                        output.write(buffer, 0, count)
                    }
                    output.toByteArray()
                } ?: error("Could not read the selected file.")
                val imported = store.decodeImport(String(bytes, Charsets.UTF_8))
                val existingIds = profiles.map { it.id }.toMutableSet()
                val normalized = imported.map { profile ->
                    if (profile.id in existingIds) profile.copy(id = UUID.randomUUID().toString()) else profile
                }
                require(profiles.size + normalized.size <= ProfileStore.MAX_PROFILES) {
                    "Import would exceed the ${ProfileStore.MAX_PROFILES}-profile limit."
                }
                val merged = profiles.toList() + normalized
                store.save(merged)
                profiles.clear()
                profiles.addAll(merged)
                status = "Imported ${normalized.size} profile(s)."
            }.onFailure { status = it.message ?: "Import failed." }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("YOUR PROFILES", color = ProfileMuted, fontSize = 11.sp, letterSpacing = 1.2.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                editing = HydraProfile()
                creating = true
            }, modifier = Modifier.weight(1f)) { Text("New profile") }
            OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/*")) }) {
                Text("Import")
            }
            OutlinedButton(onClick = {
                if (profiles.isEmpty()) status = "There are no profiles to export."
                else exportLauncher.launch("hydra-profiles.json")
            }) { Text("Export") }
        }
        if (status != null) {
            Text(status ?: "", color = ProfileMuted, fontSize = 12.sp)
        }
        if (editing != null) {
            ProfileEditor(
                initial = editing!!,
                onCancel = { editing = null; creating = false },
                onSave = { updated ->
                    runCatching {
                        val next = if (creating) profiles.toList() + updated
                        else profiles.map { if (it.id == updated.id) updated else it }
                        store.save(next)
                        profiles.clear()
                        profiles.addAll(next)
                        if (selectedId == null) {
                            selectedId = updated.id
                            store.selectProfile(selectedId)
                        }
                        status = if (creating) "Profile created." else "Profile updated."
                        editing = null
                        creating = false
                    }.onFailure { status = it.message ?: "Could not save profile." }
                }
            )
        } else if (profiles.isEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = ProfileSurface), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("No saved profiles", fontWeight = FontWeight.SemiBold)
                    Text("Create a profile or import a Hydra JSON profile file.", color = ProfileMuted, fontSize = 12.sp)
                }
            }
        } else {
            profiles.forEach { profile ->
                Card(colors = CardDefaults.cardColors(containerColor = ProfileSurface), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(profile.name, fontWeight = FontWeight.SemiBold)
                                Text(profile.domain, color = ProfileMuted, fontSize = 12.sp)
                            }
                            if (selectedId == profile.id) Text("SELECTED", color = androidx.compose.ui.graphics.Color(0xFF26C89B), fontSize = 10.sp)
                        }
                        Text("${profile.resolvers.joinToString()}  ·  Port ${profile.listenPort}  ·  MTU ${profile.mtu}",
                            color = ProfileMuted, fontSize = 11.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = {
                                selectedId = profile.id
                                store.selectProfile(profile.id)
                                status = "Selected ${profile.name}."
                            }) { Text("Select") }
                            TextButton(onClick = {
                                editing = profile
                                creating = false
                            }) { Text("Edit") }
                            TextButton(onClick = {
                                runCatching {
                                    val next = profiles.filterNot { it.id == profile.id }
                                    store.save(next)
                                    profiles.remove(profile)
                                    if (selectedId == profile.id) {
                                        selectedId = next.firstOrNull()?.id
                                        store.selectProfile(selectedId)
                                    }
                                    status = "Profile deleted."
                                }.onFailure { status = it.message ?: "Could not delete profile." }
                            }) { Text("Delete") }
                        }
                    }
                }
            }
        }
        Text("Profiles are stored locally on this device. Exported JSON files may contain server and resolver details; share them carefully.",
            color = ProfileMuted, fontSize = 11.sp)
    }
}

@Composable
private fun ProfileEditor(
    initial: HydraProfile,
    onCancel: () -> Unit,
    onSave: (HydraProfile) -> Unit
) {
    var name by remember(initial.id) { mutableStateOf(initial.name) }
    var domain by remember(initial.id) { mutableStateOf(initial.domain) }
    var port by remember(initial.id) { mutableStateOf(initial.listenPort.toString()) }
    var resolverText by remember(initial.id) { mutableStateOf(initial.resolvers.joinToString(", ")) }
    var aggressive by remember(initial.id) { mutableStateOf(initial.aggressiveDns) }
    var workers by remember(initial.id) { mutableStateOf(initial.dnsWorkers.toFloat()) }
    var mtu by remember(initial.id) { mutableStateOf(initial.mtu.toFloat()) }
    var error by remember(initial.id) { mutableStateOf<String?>(null) }

    Card(colors = CardDefaults.cardColors(containerColor = ProfileSurface), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(if (initial.domain.isBlank()) "CREATE PROFILE" else "EDIT PROFILE",
                fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            OutlinedTextField(name, { name = it }, label = { Text("Profile name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(domain, { domain = it }, label = { Text("Tunnel domain") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(port, { port = it.filter(Char::isDigit).take(5) }, label = { Text("Local listen port") },
                modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(resolverText, { resolverText = it }, label = { Text("DNS resolvers (comma separated)") },
                modifier = Modifier.fillMaxWidth(), minLines = 2)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Aggressive DNS mode")
                    Text("Preference only; DNS engine is not connected yet.", color = ProfileMuted, fontSize = 10.sp)
                }
                Switch(checked = aggressive, onCheckedChange = { aggressive = it })
            }
            Text("Worker limit: ${workers.toInt()}")
            Slider(value = workers, onValueChange = { workers = it }, valueRange = 1f..8f, steps = 6)
            Text("MTU: ${mtu.toInt()}")
            Slider(value = mtu, onValueChange = { mtu = it }, valueRange = 1280f..1500f)
            if (error != null) Text(error ?: "", color = androidx.compose.material3.MaterialTheme.colorScheme.error, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancel") }
                Button(onClick = {
                    val profile = initial.copy(
                        name = name.trim(),
                        domain = domain.trim(),
                        listenPort = port.toIntOrNull() ?: -1,
                        resolvers = resolverText.split(',').map { it.trim() }.filter { it.isNotEmpty() },
                        aggressiveDns = aggressive,
                        dnsWorkers = workers.toInt(),
                        mtu = mtu.toInt()
                    )
                    error = profile.validate()
                    if (error == null) onSave(profile)
                }, modifier = Modifier.weight(1f)) { Text("Save profile") }
            }
        }
    }
}
