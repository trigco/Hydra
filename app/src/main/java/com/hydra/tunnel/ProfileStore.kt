package com.hydra.tunnel

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class ProfileStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("hydra_profiles", Context.MODE_PRIVATE)

    fun load(): List<HydraProfile> {
        val raw = preferences.getString(KEY_PROFILES, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val profile = HydraProfile.fromJson(array.getJSONObject(index))
                    if (profile.validate() == null) add(profile)
                }
            }
        }.getOrDefault(emptyList())
    }

    fun save(profiles: List<HydraProfile>) {
        require(profiles.size <= MAX_PROFILES) { "You can store up to ${MAX_PROFILES} profiles." }
        val ids = profiles.map { it.id }
        require(ids.distinct().size == ids.size) { "Profile IDs must be unique." }
        profiles.forEach { profile ->
            require(profile.validate() == null) { profile.validate() ?: "Invalid profile." }
        }
        val array = JSONArray()
        profiles.forEach { array.put(it.toJson()) }
        check(preferences.edit().putString(KEY_PROFILES, array.toString()).commit()) {
            "Could not save profiles to local storage."
        }
    }

    fun encodeExport(profiles: List<HydraProfile>): String {
        val root = JSONObject()
            .put("format", FORMAT_NAME)
            .put("version", FORMAT_VERSION)
            .put("profiles", JSONArray().apply { profiles.forEach { put(it.toJson()) } })
        return root.toString(2)
    }

    fun decodeImport(raw: String): List<HydraProfile> {
        require(raw.toByteArray(Charsets.UTF_8).size <= MAX_IMPORT_BYTES) { "Import file is too large." }
        val root = JSONObject(raw)
        require(root.optString("format") == FORMAT_NAME) { "This is not a Hydra profile file." }
        require(root.optInt("version") == FORMAT_VERSION) { "Unsupported profile file version." }
        val array = root.optJSONArray("profiles") ?: error("Profiles list is missing.")
        require(array.length() in 1..MAX_PROFILES) { "Import must contain 1–${MAX_PROFILES} profiles." }
        val imported = buildList {
            for (index in 0 until array.length()) {
                val profile = HydraProfile.fromJson(array.getJSONObject(index))
                require(profile.validate() == null) {
                    "Profile ${index + 1}: ${profile.validate() ?: "Invalid profile."}"
                }
                add(profile)
            }
        }
        require(imported.map { it.id }.distinct().size == imported.size) { "Import contains duplicate profile IDs." }
        return imported
    }

    companion object {
        const val MAX_PROFILES = 50
        const val MAX_IMPORT_BYTES = 256 * 1024
        private const val KEY_PROFILES = "profiles_json"
        private const val FORMAT_NAME = "hydra-profile"
        private const val FORMAT_VERSION = 1
    }
}
