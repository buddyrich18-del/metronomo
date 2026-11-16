package com.drumclick

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Song(val name: String, val bpm: Int, val beats: Int = 4, val sub: Int = 1, val url: String = "")

data class SetList(val name: String, val songs: List<Song>)

data class Settings(val fontScale: Float = 1f, val volume: Float = 0.8f, val click: Int = 0, val lang: Int = -1)

object SetlistStore {
    private const val PREFS = "drumclick"
    private const val LEGACY = "setlist"
    private const val LISTS = "lists"
    private const val SEED = "seed_2026_11"

    private fun songsFrom(arr: JSONArray): List<Song> = (0 until arr.length()).map {
        val o = arr.getJSONObject(it)
        Song(o.getString("name"), o.getInt("bpm"), o.optInt("beats", 4), o.optInt("sub", 1), o.optString("url", ""))
    }

    private fun songsTo(songs: List<Song>): JSONArray {
        val arr = JSONArray()
        songs.forEach {
            arr.put(
                JSONObject().put("name", it.name).put("bpm", it.bpm)
                    .put("beats", it.beats).put("sub", it.sub).put("url", it.url)
            )
        }
        return arr
    }

    /** Carica tutte le liste; al primo avvio aggiunge le scalette importate (Seed) e migra la vecchia lista singola. */
    fun loadAll(ctx: Context): MutableList<SetList> {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val out = mutableListOf<SetList>()
        try {
            val raw = p.getString(LISTS, null)
            if (raw != null) {
                val arr = JSONArray(raw)
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    out.add(SetList(o.getString("name"), songsFrom(o.getJSONArray("songs"))))
                }
            } else {
                val legacy = p.getString(LEGACY, null)
                if (legacy != null) {
                    val songs = songsFrom(JSONArray(legacy))
                    if (songs.isNotEmpty()) out.add(SetList("Setlist", songs))
                }
            }
        } catch (e: Exception) {
        }
        if (!p.getBoolean(SEED, false)) {
            Seed.lists.forEach { s -> if (out.none { it.name == s.name }) out.add(s) }
            p.edit().putBoolean(SEED, true).apply()
            saveAll(ctx, out)
        }
        if (out.isEmpty()) out.add(SetList("Setlist", emptyList()))
        return out
    }

    fun saveAll(ctx: Context, lists: List<SetList>) {
        val arr = JSONArray()
        lists.forEach {
            arr.put(JSONObject().put("name", it.name).put("songs", songsTo(it.songs)))
        }
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(LISTS, arr.toString()).apply()
    }
}

object SettingsStore {
    private const val PREFS = "drumclick_settings"

    fun load(ctx: Context): Settings {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return Settings(
            fontScale = p.getFloat("font", 1f),
            volume = p.getFloat("volume", 0.8f),
            click = p.getInt("click", 0),
            lang = p.getInt("lang", -1)
        )
    }

    fun save(ctx: Context, s: Settings) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putFloat("font", s.fontScale).putFloat("volume", s.volume).putInt("click", s.click).putInt("lang", s.lang).apply()
    }
}
