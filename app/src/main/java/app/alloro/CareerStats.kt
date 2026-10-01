package app.alloro

import kotlin.math.roundToInt

private const val MAX_GRADUATION_GRADE = 110

/** Voto usato nelle medie: la lode vale [CareerSettings.honorsValue]. */
private fun ExamResult.Graded.value(settings: CareerSettings): Int =
    if (honors) settings.honorsValue else grade

private fun meanToBase(mean: Double): Double = mean * MAX_GRADUATION_GRADE / 30

/** Riepilogo della carriera calcolato a partire dal libretto. */
fun careerSummary(exams: List<Exam>, settings: CareerSettings): CareerSummary {
    val graded = exams.mapNotNull { exam -> (exam.result as? ExamResult.Graded)?.let { exam to it } }
    val earnedCredits = exams.sumOf { it.credits }
    val gradedCredits = graded.sumOf { (exam, _) -> exam.credits }
    val arithmeticMean = graded.takeIf { it.isNotEmpty() }
        ?.map { (_, result) -> result.value(settings) }?.average()
    val weightedMean = if (gradedCredits == 0) null
        else graded.sumOf { (exam, result) -> result.value(settings) * exam.credits }.toDouble() / gradedCredits
    return CareerSummary(
        earnedCredits = earnedCredits,
        gradedCredits = gradedCredits,
        totalCredits = settings.totalCredits,
        remainingCredits = (settings.totalCredits - earnedCredits).coerceAtLeast(0),
        arithmeticMean = arithmeticMean,
        weightedMean = weightedMean,
        graduationBase = weightedMean?.let(::meanToBase),
        honorsCount = graded.count { (_, result) -> result.honors },
        qualifiedCount = exams.count { it.result == ExamResult.Qualified }
    )
}

/**
 * Stima del voto di laurea: i CFU mancanti sono trattati come esami con media [expectedMean],
 * poi alla base si aggiungono [thesisPoints]. Restituisce `null` se non c'è nulla da pesare.
 */
fun projectGraduation(
    exams: List<Exam>,
    settings: CareerSettings,
    expectedMean: Double,
    thesisPoints: Int
): GraduationProjection? {
    val summary = careerSummary(exams, settings)
    val weightedSum = (summary.weightedMean ?: 0.0) * summary.gradedCredits
    val weight = summary.gradedCredits + summary.remainingCredits
    if (weight == 0) return null
    val projectedMean = (weightedSum + expectedMean * summary.remainingCredits) / weight
    val projectedBase = meanToBase(projectedMean)
    val raw = projectedBase + thesisPoints
    return GraduationProjection(
        projectedMean = projectedMean,
        projectedBase = projectedBase,
        finalGrade = raw.roundToInt().coerceAtMost(MAX_GRADUATION_GRADE),
        reaches110 = raw >= MAX_GRADUATION_GRADE
    )
}

/** Voto di ogni esame con la media ponderata raggiunta fino a quella data, in ordine cronologico. */
fun weightedMeanHistory(exams: List<Exam>, settings: CareerSettings): List<MeanPoint> {
    var weightedSum = 0
    var credits = 0
    return exams.sortedBy { it.date }.mapNotNull { exam ->
        val result = exam.result as? ExamResult.Graded ?: return@mapNotNull null
        weightedSum += result.value(settings) * exam.credits
        credits += exam.credits
        MeanPoint(exam.date, result, result.value(settings), weightedSum.toDouble() / credits)
    }
}
