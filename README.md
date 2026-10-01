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

## Release

La release viene firmata con la chiave indicata in `keystore.properties`, nella
radice del progetto. Il file è escluso da git e contiene `storeFile`,
`storePassword`, `keyAlias` e `keyPassword`. Senza di esso `assembleRelease`
produce un APK non firmato.

```sh
./gradlew :app:assembleRelease   # app/build/outputs/apk/release/app-release.apk
./gradlew :app:bundleRelease     # AAB per il Play Store
```

Prima di ogni nuova release aumenta `versionCode` in `app/build.gradle.kts`.
Conserva una copia della chiave e delle password: senza, non si possono più
pubblicare aggiornamenti dell'app.

## License

This project is licensed under the MIT License.
See the [LICENSE](LICENSE) file for details.
