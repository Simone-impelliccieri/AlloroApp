# Struttura dell'interfaccia

Questo documento descrive la struttura funzionale dell'app e gli invarianti da
preservare mentre lo scheletro viene riempito. L'obiettivo è che ogni dato abbia
una sola destinazione proprietaria e che componenti equivalenti si comportino
allo stesso modo in tutte le schermate.

## Primo avvio

Prima di ogni altra schermata l'app chiede **"Cosa studi?"**. L'utente sceglie
uno dei 35 corsi di `DegreeCatalog.kt` e poi il livello (triennale 180 CFU,
magistrale 120 CFU, ciclo unico 300 o 360 CFU); il passo del livello si salta
se il corso ne ha uno solo. Con **Altro corso** scrive a mano nome e CFU
totali. Finché la scelta non è salvata, `LocalStore.settings` vale `null` e
l'app mostra soltanto questa schermata.

## Navigazione principale

La barra inferiore contiene tre destinazioni stabili, nello stesso ordine in cui
compaiono nell'app:

1. **Riepilogo** mostra a colpo d'occhio corso di laurea, CFU acquisiti, media
   ponderata, base di laurea e ultimi esami;
2. **Libretto** possiede l'elenco degli esami sostenuti, senza distinzione per
   anno, e ne gestisce inserimento, modifica e rimozione;
3. **Statistiche** approfondisce medie, andamento dei voti e proiezione del voto
   di laurea.

Le **Preferenze** (corso di laurea e quindi CFU totali, valore della lode) si
aprono dall'azione nell'intestazione del Riepilogo e non occupano un tab.
L'**editor dell'esame** si apre dal Libretto o toccando un esame nel Riepilogo.
Entrambi nascondono la barra inferiore; il tasto indietro li chiude e riporta
alla destinazione di provenienza.

Riepilogo e Statistiche non mantengono elenchi propri: leggono il libretto e
ricavano i valori da `careerSummary`, `projectGraduation` e
`weightedMeanHistory` in `CareerStats.kt`.

## Esami

Il libretto contiene solo esami già sostenuti. Ogni esame ha nome (testo
libero), professore (facoltativo), data (non futura), CFU (1–30) ed esito:

- un **voto** da 18 a 30, con lode solo sul 30;
- oppure **idoneo**: l'esame conta nei CFU acquisiti ma non entra nelle medie.

La validazione del modulo sta in `ExamDraft.kt`, fuori dai composable.

## Proiezione del voto di laurea

La base è la media ponderata × 110 / 30, con la lode che vale quanto indicato
nelle Preferenze. Il simulatore tratta i CFU mancanti come esami con la media
prevista scelta dall'utente, aggiunge i punti tesi (0–11) e arrotonda per
eccesso da ,5, fermandosi a 110. Prova finale e idoneità rientrano nei CFU
mancanti: è un'approssimazione dichiarata anche nell'interfaccia.

## File principali

| File | Contenuto |
| --- | --- |
| `MainActivity.kt` | punto di ingresso, applica tema e contenuto |
| `AlloroApp.kt` | stato di libretto e impostazioni, primo avvio, navigazione e barra inferiore |
| `AlloroTheme.kt` | schema colori chiaro e scuro, colori del grafico |
| `Models.kt` | esami ed esiti, impostazioni della carriera, valori calcolati |
| `DegreeCatalog.kt` | i 35 corsi di laurea con i CFU per livello |
| `CareerStats.kt` | calcoli derivati dal libretto: medie, base, proiezione, andamento |
| `ExamDraft.kt` | modulo dell'esame e sua validazione |
| `LocalStore.kt` | persistenza locale di libretto e preferenze |
| `StoreCodec.kt` | formato JSON salvato da `LocalStore` |
| `OnboardingScreen.kt` | "Cosa studi?" e `DegreePicker`, riusato dalle Preferenze |
| `Screens.kt` | Riepilogo, Statistiche e Preferenze |
| `ExamScreens.kt` | Libretto ed editor dell'esame |
| `GradeTrendChart.kt` | grafico dell'andamento dei voti |
| `UiComponents.kt` | componenti condivisi e formattazione |

## Componenti condivisi

`UiComponents.kt` contiene gli elementi che devono rimanere coerenti tra le
schermate:

- `AppScreenHeader` per titolo, descrizione e azione contestuale;
- `AppSurfaceIconButton` per i pulsanti icona delle intestazioni;
- `AppEmptyState` per stati vuoti e relativi inviti all'azione;
- `AppSectionTitle` per i titoli delle sezioni;
- `StatTile` per un valore calcolato con la sua etichetta;
- `ExamRow` per un esame del libretto, ovunque compaia.

## Dati

Libretto e preferenze restano soltanto sul dispositivo, nelle
`SharedPreferences` con nome `alloro`:

- `schema_version`: versione del formato, oggi `1`;
- `settings`: impostazioni della carriera in JSON (assente prima della scelta
  del corso);
- `exams`: libretto in JSON; l'esito è `{"type":"graded","grade":…,"honors":…}`
  oppure `{"type":"qualified"}`.

Una volta pubblicate, le chiavi di
`LocalStore` non vanno rinominate: ogni modifica alla persistenza richiede una
migrazione esplicita e un test con dati prodotti dalla versione precedente.

## Regole per i prossimi interventi

- aggiungere una funzione alla destinazione che possiede quel dato, evitando un
  secondo elenco locale;
- riutilizzare i componenti condivisi prima di introdurre una variante;
- tenere i calcoli fuori dai composable, in funzioni pure coperte da test;
- verificare sempre la navigazione indietro da ogni schermata secondaria.
