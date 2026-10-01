# AlloroApp

Applicazione Android per tracciare i progressi universitari. Al primo avvio
chiede il corso di laurea per fissare i CFU totali; poi registra gli esami
sostenuti (con voto o idoneità) e calcola CFU acquisiti, medie, base di laurea
e una proiezione del voto finale.

App nativa realizzata con Kotlin, Jetpack Compose e Material 3. Libretto e
preferenze sono memorizzati solo nell'app sul dispositivo. Non servono account
o connessione Internet.


## Struttura

```text
app/src/main/java/app/alloro/   sorgenti Kotlin (package unico)
app/src/main/res/               risorse Android: tema, stringhe, icona
app/src/test/java/app/alloro/   test unitari JUnit
docs/                           documentazione di progetto
```

## License

This project is licensed under the MIT License.
See the [LICENSE](LICENSE) file for details.
