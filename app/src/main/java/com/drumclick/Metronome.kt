package com.drumclick

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.Random
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Metronomo a bassa latenza: genera i click come PCM e li scrive in un AudioTrack.
 * Il tempo è calcolato sui campioni, quindi non deriva (nessun Handler/timer).
 */
class Metronome(private val onBeat: (beat: Int, sub: Int) -> Unit) {

    @Volatile var bpm = 120
    @Volatile var beatsPerBar = 4
    @Volatile var subdivision = 1
    @Volatile var volume = 0.8f
    @Volatile var clickType = 0

    @Volatile private var generation = 0
    @Volatile var running = false
        private set

    private val sr = 44100

    /** accento, battito normale, suddivisione */
    private class ClickSet(val accent: ShortArray, val normal: ShortArray, val soft: ShortArray)

    private val sets = listOf(
        // Classico: seno con decadimento
        ClickSet(tone(doubleArrayOf(1600.0), 40, 60.0, 1.0), tone(doubleArrayOf(1000.0), 40, 60.0, 0.8), tone(doubleArrayOf(700.0), 40, 60.0, 0.4)),
        // Legno: breve e secco
        ClickSet(tone(doubleArrayOf(2400.0, 1200.0), 25, 150.0, 1.0), tone(doubleArrayOf(1800.0, 900.0), 25, 150.0, 0.8), tone(doubleArrayOf(1400.0, 700.0), 25, 150.0, 0.45)),
        // Hi-hat: rumore filtrato
        ClickSet(noise(70, 45.0, 1.0), noise(40, 70.0, 0.8), noise(25, 90.0, 0.4)),
        // Campana: due toni inarmonici
        ClickSet(tone(doubleArrayOf(587.0, 845.0), 150, 22.0, 1.0), tone(doubleArrayOf(540.0, 800.0), 120, 26.0, 0.8), tone(doubleArrayOf(540.0, 800.0), 60, 40.0, 0.4))
    )

    private fun tone(freqs: DoubleArray, ms: Int, decay: Double, amp: Double): ShortArray {
        val n = sr * ms / 1000
        return ShortArray(n) { i ->
            val t = i.toDouble() / sr
            var v = 0.0
            for (f in freqs) v += sin(2 * PI * f * t)
            (v / freqs.size * exp(-t * decay) * amp * Short.MAX_VALUE).toInt().toShort()
        }
    }

    private fun noise(ms: Int, decay: Double, amp: Double): ShortArray {
        val n = sr * ms / 1000
        val rnd = Random(7)
        var prev = 0.0
        return ShortArray(n) { i ->
            val t = i.toDouble() / sr
            val x = rnd.nextDouble() * 2 - 1
            val hp = (x - prev) * 0.5 // passa-alto grezzo
            prev = x
            (hp * exp(-t * decay) * amp * Short.MAX_VALUE).toInt().toShort()
        }
    }

    fun start() {
        if (running) return
        running = true
        val gen = ++generation
        Thread {
            val minBuf = AudioTrack.getMinBufferSize(
                sr, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT
            )
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sr)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuf)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                .build()
            track.play()

            var beat = 0
            var sub = 0
            var carry = 0.0
            while (running && gen == generation) {
                val subs = subdivision
                val exact = sr * 60.0 / bpm / subs + carry
                val len = exact.toInt()
                carry = exact - len

                val set = sets[clickType.coerceIn(0, sets.lastIndex)]
                val c = when {
                    sub != 0 -> set.soft
                    beat == 0 -> set.accent
                    else -> set.normal
                }
                track.setVolume(volume.coerceIn(0f, 1f))
                val buf = ShortArray(len)
                System.arraycopy(c, 0, buf, 0, minOf(len, c.size))
                onBeat(beat, sub)

                // scrittura a blocchi: lo stop è reattivo anche a BPM bassi
                var off = 0
                while (off < len && running && gen == generation) {
                    val chunk = minOf(1024, len - off)
                    track.write(buf, off, chunk)
                    off += chunk
                }

                sub++
                if (sub >= subs) {
                    sub = 0
                    beat = (beat + 1) % beatsPerBar
                }
            }
            track.stop()
            track.release()
        }.also {
            it.priority = Thread.MAX_PRIORITY
            it.start()
        }
    }

    fun stop() {
        running = false
    }

    companion object {
        val CLICK_NAMES = listOf("Classico", "Legno", "Hi-hat", "Campana")
    }
}
