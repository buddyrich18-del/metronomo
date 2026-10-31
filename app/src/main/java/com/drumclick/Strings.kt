package com.drumclick

import androidx.compose.runtime.compositionLocalOf
import java.util.Locale

data class Str(
    val code: String, val label: String,
    val textSize: String, val volume: String, val clickSound: String, val clicks: List<String>,
    val beats: String, val subdiv: String, val setlist: String, val update: String,
    val addSong: String, val newSong: String, val editSong: String,
    val add: String, val save: String, val cancel: String,
    val titleField: String, val bpmField: String, val songDefault: String,
    val delete: String, val language: String
)

val LANGS = listOf(
    Str("it", "Italiano", "Dimensione testo", "Volume", "Suono del click", listOf("Classico", "Legno", "Hi-hat", "Campana"),
        "Battiti", "Suddiv.", "Setlist", "Aggiorna", "+ Brano", "Nuovo brano", "Modifica brano",
        "Aggiungi", "Salva", "Annulla", "Titolo", "BPM (30–300)", "Brano", "Elimina", "Lingua"),
    Str("en", "English", "Text size", "Volume", "Click sound", listOf("Classic", "Wood", "Hi-hat", "Cowbell"),
        "Beats", "Subdiv.", "Setlist", "Update", "+ Song", "New song", "Edit song",
        "Add", "Save", "Cancel", "Title", "BPM (30–300)", "Song", "Delete", "Language"),
    Str("es", "Español", "Tamaño del texto", "Volumen", "Sonido del clic", listOf("Clásico", "Madera", "Hi-hat", "Cencerro"),
        "Tiempos", "Subdiv.", "Repertorio", "Actualizar", "+ Canción", "Nueva canción", "Editar canción",
        "Añadir", "Guardar", "Cancelar", "Título", "BPM (30–300)", "Canción", "Eliminar", "Idioma"),
    Str("fr", "Français", "Taille du texte", "Volume", "Son du clic", listOf("Classique", "Bois", "Hi-hat", "Cloche"),
        "Temps", "Subdiv.", "Setlist", "Mettre à jour", "+ Morceau", "Nouveau morceau", "Modifier le morceau",
        "Ajouter", "Enregistrer", "Annuler", "Titre", "BPM (30–300)", "Morceau", "Supprimer", "Langue"),
    Str("de", "Deutsch", "Schriftgröße", "Lautstärke", "Klickklang", listOf("Klassisch", "Holz", "Hi-Hat", "Kuhglocke"),
        "Schläge", "Unterteil.", "Setlist", "Aktualisieren", "+ Song", "Neuer Song", "Song bearbeiten",
        "Hinzufügen", "Speichern", "Abbrechen", "Titel", "BPM (30–300)", "Song", "Löschen", "Sprache")
)

/** lang = -1: segue la lingua del telefono (inglese se non supportata). */
fun resolveStr(lang: Int): Str =
    LANGS.getOrNull(lang) ?: LANGS.firstOrNull { it.code == Locale.getDefault().language } ?: LANGS[1]

val LocalStr = compositionLocalOf { LANGS[0] }
