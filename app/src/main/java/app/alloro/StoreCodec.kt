package app.alloro

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/** Formato JSON di libretto e impostazioni in [LocalStore]. */
object StoreCodec {
    fun encodeExams(exams: List<Exam>): String =
        JSONArray(exams.map(::encodeExam)).toString()

    fun decodeExams(json: String): List<Exam> {
        val array = JSONArray(json)
        return List(array.length()) { decodeExam(array.getJSONObject(it)) }
    }

    fun encodeSettings(settings: CareerSettings): String = JSONObject()
        .put("degreeName", settings.degreeName)
        .put("degreeLevel", settings.degreeLevel?.name ?: JSONObject.NULL)
        .put("totalCredits", settings.totalCredits)
        .put("honorsValue", settings.honorsValue)
        .toString()

    fun decodeSettings(json: String): CareerSettings {
        val obj = JSONObject(json)
        return CareerSettings(
            degreeName = obj.getString("degreeName"),
            degreeLevel = if (obj.isNull("degreeLevel")) null else DegreeLevel.valueOf(obj.getString("degreeLevel")),
            totalCredits = obj.getInt("totalCredits"),
            honorsValue = obj.getInt("honorsValue")
        )
    }

    private fun encodeExam(exam: Exam): JSONObject = JSONObject()
        .put("id", exam.id)
        .put("name", exam.name)
        .put("professor", exam.professor)
        .put("credits", exam.credits)
        .put("date", exam.date.toString())
        .put("result", when (val result = exam.result) {
            is ExamResult.Graded -> JSONObject()
                .put("type", "graded")
                .put("grade", result.grade)
                .put("honors", result.honors)
            ExamResult.Qualified -> JSONObject().put("type", "qualified")
        })

    private fun decodeExam(obj: JSONObject): Exam {
        val result = obj.getJSONObject("result")
        return Exam(
            id = obj.getString("id"),
            name = obj.getString("name"),
            professor = obj.getString("professor"),
            credits = obj.getInt("credits"),
            date = LocalDate.parse(obj.getString("date")),
            result = when (val type = result.getString("type")) {
                "graded" -> ExamResult.Graded(result.getInt("grade"), result.getBoolean("honors"))
                "qualified" -> ExamResult.Qualified
                else -> error("Esito sconosciuto: $type")
            }
        )
    }
}
