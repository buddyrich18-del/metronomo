package com.drumclick

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Song(val name: String, val bpm: Int, val beats: Int = 4, val sub: Int = 1)

object SetlistStore {
    private const val PREFS = "drumclick"
    private const val KEY = "setlist"

    fun load(ctx: Context): List<Song> {
        val raw = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, null)
            ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map {
                val o = arr.getJSONObject(it)
                Song(o.getString("name"), o.getInt("bpm"), o.optInt("beats", 4), o.optInt("sub", 1))
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(ctx: Context, songs: List<Song>) {
        val arr = JSONArray()
        songs.forEach {
            arr.put(
                JSONObject().put("name", it.name).put("bpm", it.bpm)
                    .put("beats", it.beats).put("sub", it.sub)
            )
        }
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, arr.toString()).apply()
    }
}
