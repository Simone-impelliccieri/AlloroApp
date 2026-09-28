# Struttura dell'interfaccia

Questo documento descrive la struttura funzionale dell'app e gli invarianti da
preservare mentre lo scheletro viene riempito. L'obiettivo è che ogni dato abbia
una sola destinazione proprietaria e che componenti equivalenti si comportino
allo stesso modo in tutte le schermate.

## Navigazione principale

La barra inferiore contiene tre destinazioni stabili, nello stesso ordine in cui
compaiono nell'app:

1. **Riepilogo** mostra a colpo d'occhio CFU acquisiti, medie e prossimi esami;
2. **Libretto** possiede l'elenco degli esami, superati e da sostenere, e ne
   gestisce inserimento, modifica e rimozione;
3. **Statistiche** approfondisce medie, andamento dei voti e base di laurea.

Le **Preferenze** (CFU totali del corso, valore della lode) si aprono
dall'azione nell'intestazione del Riepilogo e non occupano un tab. Il tasto
indietro chiude le Preferenze e riporta alla destinazione di provenienza.

Riepilogo e Statistiche non mantengono elenchi propri: leggono il libretto e
ricavano i valori da `careerSummary` in `CareerStats.kt`.

## File principali

| File | Contenuto |
| --- | --- |
| `MainActivity.kt` | punto di ingresso, applica tema e contenuto |
| `AlloroApp.kt` | stato di navigazione, barra inferiore e instradamento delle schermate |
| `AlloroTheme.kt` | schema colori chiaro e scuro |
| `Models.kt` | esami, impostazioni della carriera e riepilogo calcolato |
| `CareerStats.kt` | calcoli derivati dal libretto |
| `LocalStore.kt` | persistenza locale di libretto e preferenze |
| `Screens.kt` | Riepilogo, Statistiche e Preferenze |
| `ExamScreens.kt` | Libretto e, in futuro, inserimento e dettaglio degli esami |
| `UiComponents.kt` | componenti condivisi |

## Componenti condivisi

`UiComponents.kt` contiene gli elementi che devono rimanere coerenti tra le
schermate:

- `AppScreenHeader` per titolo, descrizione e azione contestuale;
- `AppSurfaceIconButton` per i pulsanti icona delle intestazioni;
- `AppEmptyState` per stati vuoti e relativi inviti all'azione.

## Dati

Libretto e preferenze restano soltanto sul dispositivo, nelle
`SharedPreferences` con nome `alloro`. Una volta pubblicate, le chiavi di
`LocalStore` non vanno rinominate: ogni modifica alla persistenza richiede una
migrazione esplicita e un test con dati prodotti dalla versione precedente.

## Regole per i prossimi interventi

- aggiungere una funzione alla destinazione che possiede quel dato, evitando un
  secondo elenco locale;
- riutilizzare i componenti condivisi prima di introdurre una variante;
- tenere i calcoli fuori dai composable, in funzioni pure coperte da test;
- verificare sempre la navigazione indietro da ogni schermata secondaria.
