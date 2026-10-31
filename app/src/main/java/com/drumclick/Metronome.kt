package com.drumclick

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.sin

/**
 * Metronomo a bassa latenza: genera i click come PCM e li scrive in un AudioTrack.
 * Il tempo è calcolato sui campioni, quindi non deriva (nessun Handler/timer).
 */
class Metronome(private val onBeat: (beat: Int, sub: Int) -> Unit) {

    @Volatile var bpm = 120
    @Volatile var beatsPerBar = 4
    @Volatile var subdivision = 1

    @Volatile private var generation = 0
    @Volatile var running = false
        private set

    private val sr = 44100
    private val accent = click(1600.0, 1.0)
    private val normal = click(1000.0, 0.8)
    private val soft = click(700.0, 0.4)

    private fun click(freq: Double, amp: Double): ShortArray {
        val n = sr * 40 / 1000
        return ShortArray(n) { i ->
            val env = 1.0 - i.toDouble() / n
            (sin(2 * PI * freq * i / sr) * env * env * amp * Short.MAX_VALUE).toInt().toShort()
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

                val c = when {
                    sub != 0 -> soft
                    beat == 0 -> accent
                    else -> normal
                }
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
}
