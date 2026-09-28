package app.alloro

import android.content.Context

/** Libretto e preferenze salvati solo sul dispositivo; non serve alcun account. */
class LocalStore(context: Context) {
    private val preferences = context.getSharedPreferences("alloro", Context.MODE_PRIVATE)

    var settings: CareerSettings
        get() = TODO("Leggere le impostazioni da preferences")
        set(value) = TODO("Salvare le impostazioni in preferences")

    fun exams(): List<Exam> = TODO("Leggere il libretto da preferences")

    fun add(exam: Exam): Boolean = TODO()

    fun update(exam: Exam): Unit = TODO()

    fun remove(exam: Exam): Unit = TODO()
}
