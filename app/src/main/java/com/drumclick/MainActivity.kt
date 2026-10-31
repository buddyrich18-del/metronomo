package com.drumclick

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF0E1230)
private val Panel = Color(0xFF1A2050)
private val Orange = Color(0xFFFF8A3D)
private val Pink = Color(0xFFFF4D8D)
private val Teal = Color(0xFF22D3B8)
private val Yellow = Color(0xFFFFD23F)
private val Green = Color(0xFF22C55E)
private val Red = Color(0xFFEF4444)
private val Violet = Color(0xFF8B5CF6)
private val Blue = Color(0xFF3B82F6)
private val stripes = listOf(Pink, Orange, Yellow, Green, Teal, Blue, Violet)

private val Scheme = darkColorScheme(
    primary = Orange, onPrimary = Color.Black,
    secondary = Teal, onSecondary = Color.Black,
    tertiary = Pink,
    background = Bg, surface = Bg, onSurface = Color.White,
    surfaceVariant = Panel, onSurfaceVariant = Color(0xFFC9CEF2),
    primaryContainer = Color(0xFF4A2F7A), onPrimaryContainer = Color.White,
    secondaryContainer = Color(0xFF14504F), onSecondaryContainer = Color.White
)

class MainActivity : ComponentActivity() {
    private val beatState = mutableIntStateOf(-1)
    private val engine = Metronome { b, sub -> if (sub == 0) beatState.intValue = b }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent { DrumClickApp(engine, beatState) }
    }

    override fun onDestroy() {
        engine.stop()
        super.onDestroy()
    }
}

@Composable
fun DrumClickApp(engine: Metronome, beatState: MutableIntState) {
    val ctx = LocalContext.current
    var settings by remember { mutableStateOf(SettingsStore.load(ctx)) }
    val dens = LocalDensity.current
    MaterialTheme(colorScheme = Scheme) {
        CompositionLocalProvider(
            LocalDensity provides Density(dens.density, dens.fontScale * settings.fontScale)
        ) {
            Surface(Modifier.fillMaxSize(), color = Bg) {
                MainScreen(engine, beatState, settings) {
                    settings = it
                    SettingsStore.save(ctx, it)
                }
            }
        }
    }
}

@Composable
private fun Act(label: String, onClick: () -> Unit) {
    Text(
        label,
        fontSize = 18.sp,
        modifier = Modifier.clip(CircleShape).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 10.dp)
    )
}

@Composable
private fun MainScreen(
    engine: Metronome,
    beatState: MutableIntState,
    settings: Settings,
    onSettings: (Settings) -> Unit
) {
    val ctx = LocalContext.current
    var bpm by remember { mutableIntStateOf(120) }
    var beats by remember { mutableIntStateOf(4) }
    var sub by remember { mutableIntStateOf(1) }
    var playing by remember { mutableStateOf(false) }
    val beat by beatState
    var current by remember { mutableIntStateOf(-1) }
    var editIndex by remember { mutableStateOf<Int?>(null) } // -1 = nuovo brano
    var showSettings by remember { mutableStateOf(false) }
    val songs = remember { mutableStateListOf<Song>().apply { addAll(SetlistStore.load(ctx)) } }
    val taps = remember { mutableListOf<Long>() }

    SideEffect {
        engine.bpm = bpm
        engine.beatsPerBar = beats
        engine.subdivision = sub
        engine.volume = settings.volume
        engine.clickType = settings.click
    }

    fun persist() = SetlistStore.save(ctx, songs)
    fun setBpm(v: Int) { bpm = v.coerceIn(30, 300) }
    fun select(i: Int) {
        current = i
        val s = songs[i]
        bpm = s.bpm; beats = s.beats; sub = s.sub
    }

    Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        songs.getOrNull(current)?.name ?: "DrumClick",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Yellow,
                        modifier = Modifier.weight(1f)
                    )
                    Act(if (showSettings) "✕" else "⚙") { showSettings = !showSettings }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)) {
                    repeat(beats) { i ->
                        Box(
                            Modifier.size(if (i == beat) 26.dp else 20.dp).clip(CircleShape).background(
                                when {
                                    i == beat && i == 0 -> Pink
                                    i == beat -> Teal
                                    else -> Color(0xFF3A4180)
                                }
                            )
                        )
                    }
                }
            }
            item {
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                        .background(Brush.horizontalGradient(listOf(Violet, Pink, Orange)))
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$bpm", fontSize = 88.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        Text("BPM", color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf(-5, -1, 1, 5).forEach { d ->
                        Button(
                            onClick = { setBpm(bpm + d) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (d < 0) Pink else Teal, contentColor = Color.Black
                            )
                        ) { Text(if (d > 0) "+$d" else "$d", fontWeight = FontWeight.Bold) }
                    }
                }
            }
            item {
                Slider(
                    value = bpm.toFloat(),
                    onValueChange = { setBpm(it.toInt()) },
                    valueRange = 30f..300f,
                    colors = SliderDefaults.colors(thumbColor = Yellow, activeTrackColor = Orange)
                )
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Panel)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Battiti", color = Teal, fontWeight = FontWeight.SemiBold)
                            OutlinedButton(onClick = { if (beats > 1) beats-- }) { Text("−") }
                            Text("$beats", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            OutlinedButton(onClick = { if (beats < 12) beats++ }) { Text("+") }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Suddiv.", color = Orange, fontWeight = FontWeight.SemiBold)
                            listOf(1, 2, 3, 4).forEach { s ->
                                FilterChip(selected = sub == s, onClick = { sub = s }, label = { Text("÷$s") })
                            }
                        }
                    }
                }
            }
            if (showSettings) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2060))) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Dimensione testo  ${(settings.fontScale * 100).toInt()}%", color = Yellow, fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = settings.fontScale,
                                onValueChange = { onSettings(settings.copy(fontScale = it)) },
                                valueRange = 0.8f..1.6f
                            )
                            Text("Volume  ${(settings.volume * 100).toInt()}%", color = Yellow, fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = settings.volume,
                                onValueChange = { onSettings(settings.copy(volume = it)) },
                                valueRange = 0f..1f
                            )
                            Text("Suono del click", color = Yellow, fontWeight = FontWeight.SemiBold)
                            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Metronome.CLICK_NAMES.forEachIndexed { i, n ->
                                    FilterChip(
                                        selected = settings.click == i,
                                        onClick = { onSettings(settings.copy(click = i)) },
                                        label = { Text(n) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Setlist", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Teal, modifier = Modifier.weight(1f))
                    if (current in songs.indices) {
                        TextButton(onClick = {
                            songs[current] = songs[current].copy(bpm = bpm, beats = beats, sub = sub)
                            persist()
                        }) { Text("Aggiorna") }
                    }
                    TextButton(onClick = { editIndex = -1 }) { Text("+ Brano") }
                }
            }
            itemsIndexed(songs) { i, s ->
                Card(
                    Modifier.fillMaxWidth().clickable { select(i) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (i == current) MaterialTheme.colorScheme.primaryContainer else Panel
                    )
                ) {
                    Row(Modifier.height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.width(8.dp).fillMaxHeight().background(stripes[i % stripes.size]))
                        Column(Modifier.weight(1f).padding(start = 12.dp, top = 8.dp, bottom = 8.dp)) {
                            Text("${i + 1}. ${s.name}", fontWeight = FontWeight.SemiBold)
                            Text(
                                "${s.bpm} BPM · ${s.beats}/4 · ÷${s.sub}",
                                style = MaterialTheme.typography.bodySmall,
                                color = stripes[i % stripes.size]
                            )
                        }
                        Act("✎") { editIndex = i }
                        Act("▲") {
                            if (i > 0) {
                                songs.add(i - 1, songs.removeAt(i))
                                if (current == i) current = i - 1 else if (current == i - 1) current = i
                                persist()
                            }
                        }
                        Act("▼") {
                            if (i < songs.lastIndex) {
                                songs.add(i + 1, songs.removeAt(i))
                                if (current == i) current = i + 1 else if (current == i + 1) current = i
                                persist()
                            }
                        }
                        IconButton(onClick = {
                            songs.removeAt(i)
                            current = when {
                                songs.isEmpty() || i == current -> -1
                                i < current -> current - 1
                                else -> current
                            }
                            persist()
                        }) { Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = Red) }
                    }
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().background(Panel).padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (songs.isNotEmpty()) select(if (current <= 0) songs.lastIndex else current - 1) },
                colors = ButtonDefaults.buttonColors(containerColor = Violet, contentColor = Color.White)
            ) { Text("◀") }
            Button(
                onClick = {
                    if (playing) { engine.stop(); beatState.intValue = -1 } else engine.start()
                    playing = !playing
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (playing) Red else Green, contentColor = Color.White
                ),
                modifier = Modifier.weight(1f).height(60.dp)
            ) { Text(if (playing) "STOP" else "START", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold) }
            Button(
                onClick = { if (songs.isNotEmpty()) select((current + 1) % songs.size) },
                colors = ButtonDefaults.buttonColors(containerColor = Violet, contentColor = Color.White)
            ) { Text("▶") }
            Button(
                onClick = {
                    val now = System.currentTimeMillis()
                    if (taps.isNotEmpty() && now - taps.last() > 2000) taps.clear()
                    taps.add(now)
                    if (taps.size > 6) taps.removeAt(0)
                    if (taps.size >= 2) {
                        val avg = (taps.last() - taps.first()).toDouble() / (taps.size - 1)
                        setBpm((60000 / avg).toInt())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color.Black)
            ) { Text("TAP", fontWeight = FontWeight.Bold) }
        }
    }

    editIndex?.let { idx ->
        val isNew = idx < 0
        SongDialog(
            title = if (isNew) "Nuovo brano" else "Modifica brano",
            initial = if (isNew) Song("", bpm, beats, sub) else songs[idx],
            confirm = if (isNew) "Aggiungi" else "Salva",
            onConfirm = { s ->
                if (isNew) {
                    songs.add(s.copy(name = s.name.ifBlank { "Brano ${songs.size + 1}" }))
                } else {
                    songs[idx] = s.copy(name = s.name.ifBlank { songs[idx].name })
                    if (idx == current) { bpm = s.bpm; beats = s.beats; sub = s.sub }
                }
                persist()
                editIndex = null
            },
            onDismiss = { editIndex = null }
        )
    }
}

@Composable
private fun SongDialog(
    title: String,
    initial: Song,
    confirm: String,
    onConfirm: (Song) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial.name) }
    var bpmText by remember { mutableStateOf(initial.bpm.toString()) }
    var beats by remember { mutableIntStateOf(initial.beats) }
    var sub by remember { mutableIntStateOf(initial.sub) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Titolo") }, singleLine = true)
                OutlinedTextField(
                    bpmText, { bpmText = it.filter(Char::isDigit).take(3) },
                    label = { Text("BPM (30–300)") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Battiti")
                    OutlinedButton(onClick = { if (beats > 1) beats-- }) { Text("−") }
                    Text("$beats", fontWeight = FontWeight.Bold)
                    OutlinedButton(onClick = { if (beats < 12) beats++ }) { Text("+") }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Suddiv.")
                    listOf(1, 2, 3, 4).forEach { s ->
                        FilterChip(selected = sub == s, onClick = { sub = s }, label = { Text("÷$s") })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val v = bpmText.toIntOrNull()?.coerceIn(30, 300) ?: initial.bpm
                onConfirm(Song(name, v, beats, sub))
            }) { Text(confirm) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annulla") } }
    )
}
