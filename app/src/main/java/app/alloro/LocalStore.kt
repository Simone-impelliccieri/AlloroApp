package app.alloro

import android.content.Context

/** Libretto e preferenze salvati solo sul dispositivo; non serve alcun account. */
class LocalStore(context: Context) {
    private val preferences = context.getSharedPreferences("alloro", Context.MODE_PRIVATE)

    init {
        // Le versioni successive leggeranno questa chiave per migrare i dati salvati.
        if (!preferences.contains(KEY_SCHEMA_VERSION)) {
            preferences.edit().putInt(KEY_SCHEMA_VERSION, SCHEMA_VERSION).apply()
        }
    }

    /** `null` finché l'utente non ha scelto il proprio corso di laurea. */
    var settings: CareerSettings?
        get() = preferences.getString(KEY_SETTINGS, null)?.let(StoreCodec::decodeSettings)
        set(value) {
            val editor = preferences.edit()
            if (value == null) editor.remove(KEY_SETTINGS) else editor.putString(KEY_SETTINGS, StoreCodec.encodeSettings(value))
            editor.apply()
        }

    fun exams(): List<Exam> =
        preferences.getString(KEY_EXAMS, null)?.let(StoreCodec::decodeExams).orEmpty()

    /** Restituisce `false` se nel libretto c'è già un esame con lo stesso id. */
    fun add(exam: Exam): Boolean {
        val exams = exams()
        if (exams.any { it.id == exam.id }) return false
        save(exams + exam)
        return true
    }

    fun update(exam: Exam) = save(exams().map { if (it.id == exam.id) exam else it })

    fun remove(exam: Exam) = save(exams().filterNot { it.id == exam.id })

    private fun save(exams: List<Exam>) =
        preferences.edit().putString(KEY_EXAMS, StoreCodec.encodeExams(exams)).apply()

    private companion object {
        const val SCHEMA_VERSION = 1
        const val KEY_SCHEMA_VERSION = "schema_version"
        const val KEY_SETTINGS = "settings"
        const val KEY_EXAMS = "exams"
    }
}
