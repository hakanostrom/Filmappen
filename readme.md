# Filmappen

Sök bland filmer och tv-serier och spara just dina favoriter. Betygsätt enskilda titlar och spara
dessa i din mobil.

## Installera och kör lokalt i debug-läge

Ladda ned och installera [Android Studio](https://developer.android.com/studio).

Använd en vanlig Android-enhet genom att förbereda den för utveckling.
Alternativt en Android-emulator. Har man möjlighet att använda _[Windows Subsystem For Android](https://learn.microsoft.com/en-us/windows/android/wsa/)_ är detta sätt ännu snabbare.

Klona repot, och öppna sedan mappen i Android Studio.


> ***OBS! API-nyckeln till OMDb API är inte versionshanterad då den är att betrakta som känslig data***
Skapa en egen API-nyckel och lägg in i filen _app/res/values/hemlig.example.xml_. Ta bort kommentar på raden i filen och ta bort 'example' från filnamnet. Filen kommer då att ignoreras av .gitignore och riskerar inte att commitas av misstag. Däremot går det alldeles utmärkt att ha två string-resourcefiler i projektet då Android Studio kommer att läsa in båda (så länge namnen på egenskaperna är unika).

Välj din enhet under _Running devices_ och kör sedan projektet genom _Run -> Run 'app'_.
Detta skall räcka för att starta appen på din enhet eller emulator.

## Bibliotek och tekniker

**Retrofit** för REST-kommunikation

**ROOM database** för att wrappa och förenkla Androids stöd för SQLite 

**Views** för layouten. Detta sätt är jag mest van vid och valde därför det framför *Jetpack Compose*.

**Java** istället för Kotlin dels för tydlighetens skull och att detta är det språk jag är mest van vid.