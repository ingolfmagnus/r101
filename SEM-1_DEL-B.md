# [Semesteroppgave 1: “Rogue One oh one”](https://retting.ii.uib.no/inf101.v19.sem1/blob/master/SEM-1_DEL-B.md) Del B: Fullfør basisimplementasjonen

* [README](README.md)
* [Oversikt](SEM-1.md) – [Praktisk informasjon 5%](SEM-1.md#praktisk-informasjon)
* [Del A: Bakgrunn, modellering og utforskning 15%](SEM-1_DEL-A.md)
* **Del B: Fullfør basisimplementasjonen 40%**
* [Del C: Videreutvikling 40%](SEM-1_DEL-C.md)

I denne delen av semesteroppgaven skal du implementere en del konkrete metoder.

### Konsepter
* *Factory pattern* – *fabrikk-metoder* brukes til å lage nye objekter; ofte ved at du velger hva du vil ha med parameterne, uten at du trenger å kjenne til klassen. Passer veldig bra sammen med `interface` hvor du vet hvordan du skal bruke objektene men ikke hvordan du kan lage dem.
* (*Design pattern* er en vanlig måte å løse noe på uten at det finnes noen egen mekanisme for det i programmerinsspråket. Å bruke fabrikk-metoder og fabrikk-objekter til å lage objekter av en interface type er en vanlig design pattern.)
* *Event-drevet kjøring* – du har ingen løkke i en `main`-metode som leser inn input fra brukeren; i stedet blir programmet vårt kalt opp når det skjer noe. Vanligvis vil datamaskinen da “sove” eller gjøre noe annet i mellomtiden. Dette kan også gjøre det litt lettere å teste – spiller-objektet vårt vil ikke merke forskjell på om det er en faktisk bruker som har trykket en tast, eller vi bare simulerer et tastetrykk.

## *(3%)* Deloppgave B1: Objekt-fabrikk
* **a)** Se på konstruktøren i Game-klassen, og se hvordan kartet blir fylt inn. Finn metoden som lager nye objekter basert på strenger (`"#"`, `"."`, `"R"`, osv) dette er en såkalt [factory/fabrikk-metode](https://en.wikipedia.org/wiki/Factory_method_pattern). Det er vanlig å bruke når vi har mange klasser som implementerer det samme grensesnittet, og vi vil gjøre det lett for andre deler av programmet å opprette objekter uten å kjenne til klassene. **Beskriv prosessen kort i [README.md](README.md).**
* **b)** **Lag deg en ny klasse som implementerer [IItem](src/inf101/v19/rogue101/objects/IItem.java).** Du kan ta utgangspunkt i en av klassene du har fra før (f.eks. `Carrot`) og du kan velge navn selv (f.eks. `CarrotCake`). Funksjonaliteten kan (foreløpig) være helt lik.
* **c)** **Legg klassen du laget til i fabrikk-metoden.** Du må velge et tegn som skal representere objekter av klassen (f.eks. `"c"`). Bruk dette tegnet både i fabrikk-metoden i `Game` og i `getSymbol()`-metoden i den nye klassen din (det er ingen direkte sammenheng mellom disse, men greit om de stemmer overens).
* **d)** **Legg til det nye tegnet ditt i standard-kartet** (ligger i [src/inf101/v19/rogue101/map/maps/level1.txt](src/inf101/v19/rogue101/map/maps/level1.txt) – eventuelt ligger det et innebygget kart i `Main`-klassen hvis vi av en eller annen grunn ikke finner kartfilen; vi bruker forøvrig samme kartformat som [semesteroppgave 1 i fjor](https://retting.ii.uib.no/inf101.v17.oppgaver/inf101.v17.sem1/)), og se at gulrotkaken (eller det du har laget) dukker opp på skjermen.

Symbolene på skjermen blir hentet fra `getSymbol()` (evt. `getPrintSymbol()` om du har definert den). I tillegg til å kunne tegne tekst-tegn kaller programmet `draw()`-metoden hvor du kan legge inn egen grafikk; hvis denne returnerer `true` blir tegnet fra `getSymbol()` ikke tegnet på skjermen. Så hvis du bare ser ekstra `X`-er, så har du kopiert `ExampleItem` uten å endre `getSymbol()`, og hvis du bare ser ekstra gulrøtter, så har du kopiert `Carrot` uten å oppdatere grafikken (du kan bare slette draw-metoden og returnere false).

*(Avansert mulighet (ikke del av oppgaven): i stedet for å lage en stor fabrikk med `switch` går det an å bygge en oversikt over fabrikkene mens programmet kjører. Du kan registrere symbolet for den nye klassen + en kodesnutt som lager objekt i `itemFactories` i `Game`: f.eks. `itemFactories.put("R", () -> new Rabbit())`. `default`-tilfellet i `switch`en vil prøve å slå opp i `itemFactories` og kalle kodesnutten hvis den finner noe. Registreringen kan gjøres i konstruktøren til `Game`, eller du kan lage en ekstra `addFactory`-metode.)*

## *(7%)* Deloppgave B2: Minimal Player
Du er sikkert lei av at det bare er kaninene som får lov til å ha det gøy.
* **a)** **Lag en klasse `Player implements` [`IPlayer`](src/inf101/v19/rogue101/objects/IPlayer.java)** (putt den i `objects` eller en egen ny `player` pakke). Du kan følge mønsteret fra de andre IItem-klassene, men du trenger litt andre metoder. Der hvor `Rabbit` har `doTurn()` skal `Player` ha `keyPressed()`. Du kan la denne være tom til å begynne med.
* **b)** **Oppdatere fabrikken i `Game` så den også lager `Player`-objekter;** de har tradisjonelt symbolet `@` (i utlevert kode gir det deg en `ExampleItem`).
* **c)** Metoden `keyPressed()` i `IPlayer` tar et `KeyCode`-object, og kalles hver gang spilleren
trykker på en tast. Knappen indikeres med KeyCode-en (en for hver tast på tastaturet + at man kan sjekke f.eks. om *Ctrl*-tasten er trykket inn). F.eks. vil følgende kode sjekke om
venstre-tasten ble trykket, og kalle `tryToMove()` (denne metoden er ikke implementert ennå):

```
    public void keyPressed(IGame game, KeyCode key) {
		if (key == KeyCode.LEFT) {
			tryToMove(game, GridDirection.WEST);
		}
	}
```

**Gjør ferdig denne metoden så den kaller `tryToMove` med `GridDirection.EAST`, `GridDirection.NORTH`, eller `GridDirection.SOUTH` når spilleren trykker `KeyCode.RIGHT`, `KeyCode.UP`, eller `KeyCode.DOWN`.**

(Dette er typisk enkel *event-drevet programmering* hvor metoder i programmet vårt blir kalt når
ting skjer utenfor programmet. Dette er vanlig for spill, nettverkstjenere og programmer med grafiske brukergrensesnitt – man har en metode som blir kalt for hvert tidssteg, og metoder som blir kalt når tastatur eller mus brukes eller det kommer inn en ny forespørsel over nettet. Legg merke til at det ikke er slik at Player gjør noe for å sjekke om brukeren har trykket på en tast – derimot blir metoden `keyPressed` kalt når tasten trykkes, og Player vet ingenting om hvordan det skjer eller hvor tastetrykkene kommer fra.)

* **d)** **Lage metoden `tryToMove()` som kalles av `keyPressed()`** (du kan kalle den hva du vil, men det er greit å ha den som en egen metode, siden du gjør nesten det samme for alle retningene).
    * Den må spørre `game` om det er lov å gå i den aktuelle retningen, og i såfall kalle `game.move()` for å flytte i den gitte retningen. Du kan se hvordan `Rabbit` løser dette – hos Player slipper du heldigvis å tenke på hvor det er *lurt* å flytte, siden brukeren alt har bestemt det.
    * Hvis det ikke er lov å flytte (det er en vegg der – eller en kanin), ska du gi en tilbakemelding til brukeren f.eks. med `game.displayMessage("Ouch!")`. Hvis du er ekstra streng, kan du også la spilleren tape litt helse på å stange hodet i veggen.

* **e)** Brukeren trenger også litt statusinformasjon, f.eks. om antall helsepoeng. **Lag en metode `showStatus(game)` i `Player`-klassen din.** Den kan f.eks. bruke `game.displayStatus("...")` eller `game.formatStatus("... %d ...", verdi)` (hvis du er komfortabel med `printf`-liknende strengformattering). Begge disse printer en linje med tekst på skjermen (Main-klassen holder rede på de passende linjene, status havner på linjen `Main.LINE_STATUS` som er 21 (rett under kartet hvis kartet er 20 linjer høyt)). **Kall `showStatus()` på slutten av `keyPressed()`** (hvis du senere lager andre metoder som kan endre tilstanden til spilleren, bør du kalle `showStatus()` på slutten av disse også).

* **f)** **Lag noen tester for Player-klassen din, som sjekker bevegelsene.** Du kan ta utgangspunkt i den lille testen som ligger i [PlayerTest.java](src/inf101/v19/rogue101/tests/PlayerTest.java); test at det funker å flytte seg i forskjellige retninger, og at det *ikke* funker å flytte seg inn i veggen.

## *(5%)* Deloppgave B3: Sortering av items

Vi er ikke så veldig lure med hvordan vi samler en mengde IItems i en List i kart-cellene. F.eks. blir grafikken antakelig veldig rotete om vi prøver å tegne opp mer én ting i hver rute – så vi trenger gjerne en bedre måte å bestemme rekkefølgen de ligger i listen på (eller evt. hvordan vi velger hvilken ting vi skal tegne). I den utleverte koden tegner vi alltid bare den første tingen i hver celle, uavhengig av om den er interessant eller ikke. Du har kanskje lagt merke til at kaninene “forsvinner” når de står på et felt med en gulrot (hvis ikke, kan du prøve å flytte spilleren til en gulrot og se hva som skjer).

En grei løsning er å si at vi vil se den *største* tingen – alle IItems har en `getSize()` metode som forteller hvor “stor” den er (uten at vi har brukt dette til noe hittil). IItems er også `Comparable` – det følger med en `default` `compareTo()`-metode i `IItem` som sammenlikner størrelse (`getSize()`).

For å fikse dette vil vi at kartet skal ha tingene liggende i sortert rekkefølge i listene for hver celle. Vi kunne sortert listen hver gang vi hentet den (f.eks. hver gang kaninen kaller `game.getLocalItems()`, slik at den største gulroten kommer først), men det er både raskere og ca. like lettvint å passe på at vi legger ting inn i sortert rekkefølge: Hvis vi skal legge et element *e* inn i listen *l*, så vil vi ha det på den første posisjonen *i* slik at *e.compareTo(l.get(i)) >= 0* (altså, sett inn *e* foran det første elementet *e* er større enn).

* **a)** **Legg til alternativet for `"."` i `Game.createItem()` – den skal produsere et [`Dust`-objekt](src/inf101/v19/rogue101/objects/Dust.java).**
    * Hvis du kjører programmet nå, vil du antakelig se at mange av kaninenen “gjemmer seg” – de ligger altså under støvet (Dust-objektene; sees som litt mørkere felter).
* **b)** **Endre `add`-metoden i `GameMap` slik at nye items blir lagt til i sortert rekkefølge (største først).**
    * Husk at `a.compareTo(b)` er `< 0` hvis `a` er mindre `b`, `== 0` hvis `a` er lik `b` og `> 0` hvis `a` er større `b`.
    * Du kan legge element foran det nåværende elementet på posisjon `i` med `list.add(i, e)` (det er også OK om `i == list.size()`, da legger du til et nytt element på slutten av listen)
    * Det holder å kalle `add` på listen, siden `list` refererer til samme objektet som ligger inne i kart-cellen.
    * `Dust`-objektene er ganske små (størrelse 1), så hvis du har gjort det riktig, vil kaninene (og spilleren) bli tegnet i stedet for støvet.
* **c)** **Finn testklassen [GameMapTest](src/inf101/v19/rogue101/tests/GameMapTest.java) og testmetoden vi har begynt på (`testSortedAdd`). Skriv klart testet av `add`-metoden.**
   * Du må sette opp et test-scenario:
      * du trenger et nytt GameMap (det trenger ikke være fylt med noe)
      * du trenger også en ILocation, siden de fleste kart-metodene bruker det (kall `getLocation(x,y)` med en kjent, lovlig posisjon)
      * så må du opprette noen IItems og legge dem til på kartposisjonen ved å kalle `add()` på kartet
   * Til slutt kan du teste at tingene ligger i den rekkefølgen du forventer (`getAll()`, og sjekk listeelementene med `get()`)
   * *(Litt mer avansert:)* Hvis du vil teste med litt større mengder data er det gjerne upraktisk å sjekke nøyaktig hvordan elementene er plassert – da kan du i stedet gå gjennom listen og sjekke at `list.get(i).compareTo(list.get(i+1)) >= 0` for `0 <= i < list.size()-1`. Et godt utvalg data kan du få ved å lage deg en metode som lager tilfeldige IItems – den kan uansett være nyttig for å lage tilfeldige kart og slikt. (Du skal lære dette mer grundig i neste lab-oppgave.)

*(Avansert mulighet (ikke del av oppgaven): En mer generell løsning er å lage en sortert liste (kanskje helst `IList/MyList` fra Lab 2/3/4, men evt `List/ArrayList`) og så bruke den i stedet for vanlig liste i `IMultiGrid` og `MultiGrid`. Du trenger litt mer avansert Java-kunnskap for å få det til å funke, du må bla. si at elementtypen er sammenliknbar (f.eks. `ISortedList<T extends Comparable<T>>`) – vi ser mer på dette senere i semesteret. )*

## *(5%)* Deloppgave B4: Plukk opp / dropp ting

Vi vil gjerne la brukeren kunne plukke opp og legge fra seg ting på
kartet – halvspiste gulrøtter, for eksempel. For å få til dette må du
legge til flere tastetrykk til `keyPressed`-metoden i `Player` – du
kan f.eks. bruke `KeyCode.P` og `KeyCode.D`.

* **a)** **Lag metoder `pickUp(IGame game)` og `drop(IGame game)` for å plukke opp/droppe ting i `Player`.**
   * *Legg merke til:* andre metoder i `Player` er laget slik at de tar et `IGame`-argument. Det trenger du nok her også fordi du må snakke med spill-objektet for å interagere med kartet og andre ting. Du *kan* lagre `game` som en feltvariabel, så du slipper å sende den rundt som argument. Vi har latt være å gjøre det fordi: a) det er lettere å opprette og teste Player (og andre IItems/IActors) hvis man ikke trenger å lage gi dem et game-objekt (f.eks. kunne du teste GameMap/sortert add uten å bruke `Game`/`IGame` i det hele tatt); og b) det kan være litt forvirrende med sykliske avhengigheter mellom objekter (ikke minst når forholdet endrer seg – f.eks. når ting fjernes fra spillet).
* **b)** `Game` har tilsvarende `pickUp()` og `drop()` metoder du kan bruke for å plukke et objekt fra kartet og for å etterlate et objekt på kartet. Metoden `pickUp()` returnerer `null` om du av en eller annen grunn ikke kunne plukke opp tingen. **Implementer “plukke opp”-funksjonaliteten.** Hint:
    * `game.pickUp()` plukker opp et spesifikt objekt som ligger i kartruten du står i, så du er nødt til å finne ut *hvilket objekt* du vil prøve å plukke opp hvis det er flere mulighetre (foreløpig har brukeren ingen måte å be om et spesifikt IItem på);
    * du finner alle tingene som ligger i kartruten med `game.getLocalItems()`, og du kan f.eks. prøve å ta den første. Du kan også finne tingene ved å få tak i kartet (game.getMap()) og undersøke det direkte – bare pass på at spilleren ikke ender opp med å plukke opp seg selv! (getLocalItems() gir deg bare items som ikke er IActor, så den er “trygg”)
    * husk at ruten kan være tom, dvs at `game.getLocalItems()` gir tom liste – da kan du f.eks. gi bruken beskjed om at det ikke er noe å plukke opp.
* **c)** **Lagre objektet du plukket opp i en feltvariabel i `Player`.** `Player.pickUp()` og `Player.drop()` ska sette/resette denne variabel. Foreløpig går det greit å tenke seg at du bare kan holde på én ting, så da er det lett å vite hva du skal legge fra deg. Implementér “dropp / legg fra deg”-funksjonaliteten. Hint:
   * Du kan gi en passende melding til brukeren om du ikke har noe å legge fra deg.
   * Pass på at når du har lagt fra deg objektet (med `game.drop()`) så sletter du det fra `Player`-objektet – ellers kan du massekopiere ting ved å plukke opp én ting og så legge den fra deg mange ganger.
   * `game.drop()` kan i prinsippet feile (fullt på bakken, kanskje?), i såfall returnerer den `false` og du bør *ikke* slette tingen fra `Player`-objektet
* **d)** **Legg inn navnet på objektet spilleren bær på i
  status-meldingen (fra B2.e).**

## *(5%)* Deloppgave B5: Finne synlige ting / ting i nærheten
[IGame](src/inf101/v19/rogue101/game/IGame.java) spesifiserer en metode `getVisible()` som skal gi deg alle locations i nærheten som du kan se. Denne er foreløpig ikke skikkelig implementert. Den kaller `map.getNeighbourhood(currentLocation, dist)`, som mangler implementasjon for dist > 0.

* **a)** **Lag en enkel versjon av `getNeighbourhood()` i [GameMap](src/inf101/v19/rogue101/map/GameMap.java) som bare returnerer alle de direkte naboene til lokasjonen.** ILocation har metoder som kan hjelpe deg (men du må kanskje flytte resultatet over i en liste). Med *naboer* mener vi her alle de åtte cellene som er rundt cellene i midten, *unntatt* de av naboene som er utenfor kartet (du vil uansett ikke få tak i ILocations for dissse); og med *nabolag* tenker også på naboene til naboene osv.
* **b)** Det er litt dumt å bare kunne se de aller nærmeste cellene. **Endre din `getNeighbourhood()` slik at den finner alle locations som er innenfor <code>dist</code> cellers avstand.** Du kan sette begrensninger på <code>dist</code> om vil, men du bør håndtere minst 3.
* **c)** **Lage tester for dette også, slik som i B3.** Du trenger et GameMap, og en ILocation på kartet.
   * Du kan sjekke at resultatet fra `getNeighbourhood()` er riktig ved å gå gjennom hele listen og så sjekke at `centre.gridDistanceTo(element) <= dist`.
   * Du må også teste at du får det forventede antall naboer (f.eks. så lenge du er midt inne i kartet skal du ha 8 naboer for `dist=1`, 24 for `dist=2`). Sjekk også at du får riktig antall naboer når du er ute i kanten eller hjørnene av kartet. Du bør gjerne ha tester for minst 3 scenarier.
* *d)* *(Ekstra:)* nabolagslisten er litt mer praktisk hvis de nærmeste naboenen kommer først. Det kan være din versjon allerede funker slik – men i såfall bør du også teste det.
   * Gjør om nødvendig om på `getNeighbourhood()` slik at de nærmeste cellene kommer først i listen.
   * Lag en test / juster testene over, slik at du sjekker avstanden (med `gridDistanceTo()`) til senere elementer i listen alltid er like eller større en avstanden til de tidligere elementene (du kan bruke liknende teknikk som når du testet at items var sortert etter størrelse).

Du vil kanskje ha lyst på en annen (mer fornuftig) `getVisible()` senere; du kan endre denne så den bruker noe annet enn `getNeighbourhood()` (eller evt. gjør justeringer på resultatet). Du kan bruke `getVisible()`/`getNeighbourhood()` til å gjøre kaninens gulrot-leting mer effektiv, f.eks.

Hvis du vil ha *skikkelig* synlighet, f.eks. slik at aktørene ikke kan oppdage det som er bak vegger, trenger du en [betydelig mer sofistikert synlighetsalgoritme](http://ncase.me/sight-and-light/). Den går an å lage en veldig simplistisk synlighetstest ved hjelp av `ILocation.gridLineTo(ILocation)`, som gir deg alle locations om ligger på en linje mellom denne og en annen location – men dette er ikke en del av oppgaven (du kan selvfølgelig likevel prøve deg i Del C!).

## *(5%)* Deloppgave B6: Enkelt “Angrep”
Tradisjonelle roguelikes er *veldig* opptatt av slossing (kan med rette kalles “[hack-and-slash](https://en.wikipedia.org/wiki/Hack_and_slash)”), så vi bør ha med en eller annen slik mekanikk. Du kan selvfølgelig velge selv (i del C) hva slags betydning dette skal ha for historien i spillet (om du har en historie).

Kampmekanikken er en forenkling av vanlige regler fra liknende dataspill og bord-rollespill:
* A *gjør et angrep* på B
* A har en *attack score* og B har en *defence score*
* Vi tilsetter litt tilfeldighet, og hvis *attack* > *defence* har A vunnet, ellers har B vunnet.
* Hvis A vinner, blir B skadet – A har en “damage score” som sier hvor mye og B sine “health points” holder rede på skade som har skjedd og hvor alvorlig den er
* Hvis B vinner, skjer det ingen ting – bortsett fra at det nå antakelig er B sin tur, og B kan angripe A (eller løpe sin vei)

En grei formel for angrep er f.eks.: `attack+random.nextInt(20)+1 >= defence+10` (brukt i et populert bordrollespill basert på 20-sidede terninger).

* **a)** **Implementere ferdig metoden `Game.attack(dir, target)`** (vi har lagt den i Game-klassen, slik at den kan håndtere spillereglene uten at aktørene "jukser"). Du kan justere reglene litt etter hva du ønsker selv, men formelen over er at bra utgangspunkt.
   * Når du har avgjort vinneren kan du gi en passende melding på skjermen. F.eks., `formatMessage("%s hits %s for %d damage", currentActor.getName(), target.getName(), damage);` og en tilsvarende hvis angrepet mislykkes.
   * Du skal også kalle `handleDamage()` på target-objektet. Denne metoden returnerer skaden som *faktisk* skjedde – det kan f.eks. brukes i tilfeller hvor forsvareren hadde en eller annen form for beskyttelse.
* **b)** Selv om Game nå kan avgjøre hva som skjer med angrep, er det foreløpig ingen som vil prøve seg på å angrip! **Oppdater `Rabbit` slik at `doTurn()` metoden kaller `attack()` i stedet for `move()`** (enten når det er mulig, eller når spilleren er ved siden av):
   * Du trenger å sjekke alle gyldige naboer, ikke bare de du kan gå til – du er kanskje særlig interessert i å angripe andre IActors, og de befinner seg i felter du ikke kan gå inn i (ja, i prinsippet vil det også være mulig å angripe og ødelegge veggene – men de har ganske mange helsepoeng!).
   * I prinsippet kan du angripe et hvilket som helst `IItem` så lenge det er i et nabofelt (evt. kan du oppgi `GridDirection.CENTER` og angripe noe i samme felt), men du har kanskje lyst til å sjekke mot `instanceof IActor` eller `instanceof IPlayer`.
   * Du må kalle `attack()`-metoden med et spesifikk `IItem` som mål, og med en spesifikk retning.
   * En mulighet er f.eks.: gå gjennom alle nabocellene; hvis du ser en gulrot, flytt dit, hvis du ser en IPlayer, angrip og eller beveg i en tilfeldig retning.
   * Prøv spillet og se hva som skjer.
* **c)** **Implementer kod som later spilleren angripe.** En grei mekanikk for det er at når spilleren prøver å “gå på” en annen aktør, så telles det som angrip (i stedet for å resultere i en “Ouch!”, eller – enda verre – IllegalMoveException). Går helt fint å la spilleren få lov å angripe veggene også. Siden du må velge et spesifikt item å angripe, kan du f.eks. velge det første fra `game.getLocalItems()` (evt. finne noe som er en IActor hvis mulig).
   * Prøv spillet og se hva som skjer.

*MERK:* det er forskjell om en kartcelle er “lovlig”, “lovlig og opptatt” og “ulovlig”. Hvis du prøver å gå `NORTH` fra (0,0) vil du f.eks. havne utenfor kartet, så dette er en ulovlig celle (egentlig ikke en celle i det hele tatt). Hvis du antar at du har en location `loc` (f.eks. din nåværende plassering fra `game.getLocation()`):
   * Hvis du gjør `loc.canGo(GridDirection.NORTH)`, så får du vite om det finnes en lovlig location nord for nåværende location; tilsvarende med `game.getMap().hasNeighbour(loc, GridDirection.NORTH)`. Det kan likevel godt være at du ikke får lov til å gjøre `game.move(GridDirection.NORTH)`, f.eks. fordi det er en vegg der.
   * Hvis du gjør `game.canGo(GridDirection.NORTH)` eller `map.canGo(GridDirection.NORTH)` får du vite om det går an å gå nordover, altså at naboen i nord ikke er ulovlig og ikke er opptatt (av en vegg eller en aktør). Litt forskjellige deler av systemet har altså litt forskjellig oppfatning av “canGo” og om man skal sjekke om ting er opptatt eller ikke.
   * For angrep er du antakelig særlig interessert i feltene som er lovlige men opptatte.

## *(5%)* Deloppgave B7: Spørsmål
* **a)** Du har måttet gjøre en del arbeid med nabo-celler og slikt. Håndterer du dette på en annen måte enn vi gjorde i labbene (f.eks. cell-automatene og labyrinten)? Hva synes du er mest praktisk?

* **b)** Hvorfor går de fleste av spill-"trekkene" (slik som at noen flytter seg, plukker en ting, legger ned en ting, angriper naboen, etc.) gjennom Game? Kan du se for deg fordeler / ulemper ved dette?

* **c)** I en del av metodene burde vi kanskje vært litt mer presise på hva som er “forkravene” – hva må være oppfylt for at det skal gå bra å kalle denne metoden? Vi har nevnt litt at `game.move()` krever at feltet i retningen er lovlig og ledig. Se gjennom de andre metodene i `Game` og `GameMap` og se om de har spesielle antakelser rundt parameterne. Ser du noe som burde endres? F.eks., enten metoder som sjekker for ting som kan gå galt, men ikke sier noe om forutsetningene i dokument – eller metoder som *burde* sjekket parameterne sine / andre forutsetninger. F.eks., hva med `Game.addItem()`  (og `drop()` også, forsåvidt) – bør den f.eks. sjekke at feltet ikke allerede er opptatt (f.eks. legge til ny IActor når det allerede er en IActor der, e.l.)?

* **d)** Tenker du annerledes om noen av spørsmålene fra Del A nå?

## *(5%)* generelt

*Ca. 5% for generell ryddighet og kvalitet.*

# Gå videre til [**DEL C**](SEM-1_DEL-C.md)
