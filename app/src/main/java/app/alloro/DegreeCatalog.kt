package app.alloro

/** Un corso di laurea con i CFU totali per ciascun livello in cui è offerto. */
data class DegreeCourse(val name: String, val credits: Map<DegreeLevel, Int>)

private val TRIENNALE_MAGISTRALE = mapOf(DegreeLevel.TRIENNALE to 180, DegreeLevel.MAGISTRALE to 120)

private fun twoLevels(name: String) = DegreeCourse(name, TRIENNALE_MAGISTRALE)

private fun singleCycle(name: String, credits: Int) = DegreeCourse(name, mapOf(DegreeLevel.CICLO_UNICO to credits))

/**
 * I 35 corsi di laurea più diffusi in Italia, approssimati dalle classi di laurea con più
 * iscritti. L'interfaccia li mostra in ordine alfabetico.
 */
val DEGREE_COURSES: List<DegreeCourse> = listOf(
    singleCycle("Medicina e chirurgia", 360),
    singleCycle("Odontoiatria e protesi dentaria", 360),
    singleCycle("Giurisprudenza", 300),
    singleCycle("Farmacia", 300),
    singleCycle("Medicina veterinaria", 300),
    singleCycle("Scienze della formazione primaria", 300),
    DegreeCourse("Architettura", TRIENNALE_MAGISTRALE + (DegreeLevel.CICLO_UNICO to 300)),
    twoLevels("Economia aziendale"),
    twoLevels("Economia"),
    twoLevels("Psicologia"),
    twoLevels("Infermieristica"),
    twoLevels("Fisioterapia"),
    twoLevels("Ingegneria informatica"),
    twoLevels("Ingegneria gestionale"),
    twoLevels("Ingegneria meccanica"),
    twoLevels("Ingegneria civile"),
    twoLevels("Ingegneria elettronica"),
    twoLevels("Ingegneria biomedica"),
    twoLevels("Informatica"),
    twoLevels("Matematica"),
    twoLevels("Fisica"),
    twoLevels("Chimica"),
    twoLevels("Scienze biologiche"),
    twoLevels("Biotecnologie"),
    twoLevels("Scienze dell'educazione e della formazione"),
    twoLevels("Scienze politiche e relazioni internazionali"),
    twoLevels("Lingue e letterature straniere"),
    twoLevels("Mediazione linguistica"),
    twoLevels("Scienze della comunicazione"),
    twoLevels("Lettere"),
    twoLevels("Filosofia"),
    twoLevels("Scienze motorie"),
    twoLevels("Servizio sociale"),
    twoLevels("Design"),
    twoLevels("Scienze e tecnologie agrarie")
).sortedBy { it.name }
