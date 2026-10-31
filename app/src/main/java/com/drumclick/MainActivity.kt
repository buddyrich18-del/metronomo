package com.drumclick

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private val beatState = mutableIntStateOf(-1)
    private val engine = Metronome { b, sub -> if (sub == 0) beatState.intValue = b }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(Modifier.fillMaxSize()) { DrumClickApp(engine, beatState) }
            }
        }
    }

    override fun onDestroy() {
        engine.stop()
        super.onDestroy()
    }
}

@Composable
fun DrumClickApp(engine: Metronome, beatState: MutableIntState) {
    val ctx = LocalContext.current
    var bpm by remember { mutableIntStateOf(120) }
    var beats by remember { mutableIntStateOf(4) }
    var sub by remember { mutableIntStateOf(1) }
    var playing by remember { mutableStateOf(false) }
    val beat by beatState
    var current by remember { mutableIntStateOf(-1) }
    var showAdd by remember { mutableStateOf(false) }
    val songs = remember { mutableStateListOf<Song>().apply { addAll(SetlistStore.load(ctx)) } }
    val taps = remember { mutableListOf<Long>() }

    SideEffect {
        engine.bpm = bpm
        engine.beatsPerBar = beats
        engine.subdivision = sub
    }

    fun persist() = SetlistStore.save(ctx, songs)
    fun setBpm(v: Int) { bpm = v.coerceIn(30, 300) }
    fun select(i: Int) {
        current = i
        val s = songs[i]
        bpm = s.bpm; beats = s.beats; sub = s.sub
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            songs.getOrNull(current)?.name ?: "—",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Row(Modifier.align(Alignment.CenterHorizontally), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(beats) { i ->
                Box(
                    Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            if (i == beat) (if (i == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
        Text(
            "$bpm",
            fontSize = 84.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(-5, -1, 1, 5).forEach { d ->
                FilledTonalButton(onClick = { setBpm(bpm + d) }) { Text(if (d > 0) "+$d" else "$d") }
            }
        }
        Slider(
            value = bpm.toFloat(),
            onValueChange = { setBpm(it.toInt()) },
            valueRange = 30f..300f
        )
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Battiti")
            OutlinedButton(onClick = { if (beats > 1) beats-- }) { Text("−") }
            Text("$beats", fontWeight = FontWeight.Bold)
            OutlinedButton(onClick = { if (beats < 12) beats++ }) { Text("+") }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Suddiv.")
            listOf(1, 2, 3, 4).forEach { s ->
                FilterChip(selected = sub == s, onClick = { sub = s }, label = { Text("$s") })
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = {
                if (songs.isNotEmpty()) select(if (current <= 0) songs.lastIndex else current - 1)
            }) { Text("◀") }
            Button(
                onClick = {
                    if (playing) { engine.stop(); beatState.intValue = -1 } else engine.start()
                    playing = !playing
                },
                modifier = Modifier.weight(1f).height(56.dp)
            ) { Text(if (playing) "STOP" else "START", fontSize = 20.sp) }
            OutlinedButton(onClick = {
                if (songs.isNotEmpty()) select((current + 1) % songs.size)
            }) { Text("▶") }
            OutlinedButton(onClick = {
                val now = System.currentTimeMillis()
                if (taps.isNotEmpty() && now - taps.last() > 2000) taps.clear()
                taps.add(now)
                if (taps.size > 6) taps.removeAt(0)
                if (taps.size >= 2) {
                    val avg = (taps.last() - taps.first()).toDouble() / (taps.size - 1)
                    setBpm((60000 / avg).toInt())
                }
            }) { Text("TAP") }
        }

        HorizontalDivider()
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Setlist", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            if (current in songs.indices) {
                TextButton(onClick = {
                    songs[current] = songs[current].copy(bpm = bpm, beats = beats, sub = sub)
                    persist()
                }) { Text("Aggiorna") }
            }
            TextButton(onClick = { showAdd = true }) { Text("+ Brano") }
        }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            itemsIndexed(songs) { i, s ->
                Card(
                    Modifier.fillMaxWidth().clickable { select(i) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (i == current) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(Modifier.padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${i + 1}. ${s.name}", fontWeight = FontWeight.SemiBold)
                            Text("${s.bpm} BPM · ${s.beats}/4 · ÷${s.sub}", style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = {
                            if (i > 0) {
                                songs.add(i - 1, songs.removeAt(i))
                                if (current == i) current = i - 1 else if (current == i - 1) current = i
                                persist()
                            }
                        }) { Text("▲") }
                        TextButton(onClick = {
                            if (i < songs.lastIndex) {
                                songs.add(i + 1, songs.removeAt(i))
                                if (current == i) current = i + 1 else if (current == i + 1) current = i
                                persist()
                            }
                        }) { Text("▼") }
                        IconButton(onClick = {
                            songs.removeAt(i)
                            current = if (songs.isEmpty()) -1 else current.coerceAtMost(songs.lastIndex).let { if (current > i) current - 1 else it }
                            persist()
                        }) { Icon(Icons.Default.Delete, contentDescription = "Elimina") }
                    }
                }
            }
        }
    }

    if (showAdd) {
        var name by remember { mutableStateOf("") }
        var bpmText by remember { mutableStateOf(bpm.toString()) }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Nuovo brano") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(name, { name = it }, label = { Text("Titolo") }, singleLine = true)
                    OutlinedTextField(
                        bpmText, { bpmText = it.filter(Char::isDigit).take(3) },
                        label = { Text("BPM") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text("Battiti e suddivisione: quelli attuali ($beats/4, ÷$sub)", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val v = bpmText.toIntOrNull()?.coerceIn(30, 300) ?: bpm
                    songs.add(Song(name.ifBlank { "Brano ${songs.size + 1}" }, v, beats, sub))
                    persist()
                    showAdd = false
                }) { Text("Aggiungi") }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("Annulla") } }
        )
    }
}
