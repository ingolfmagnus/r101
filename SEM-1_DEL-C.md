# [Semesteroppgave 1: “Rogue One oh one”](https://retting.ii.uib.no/inf101.v19.sem1/blob/master/SEM-1_DEL-C.md) Del C: Videreutvikling

* [README](README.md)
* [Oversikt](SEM-1.md) – [Praktisk informasjon 5%](SEM-1.md#praktisk-informasjon)
* [Del A: Bakgrunn, modellering og utforskning 15%](SEM-1_DEL-A.md)
* [Del B: Fullfør basisimplementasjonen 40%](SEM-1_DEL-B.md)
* **Del C: Videreutvikling 40%**

## Videreutvikling av Rogue-101

Vi overlater nå ansvaret for utviklingen til deg – du finner noen forslag under som du kan jobbe ut ifra, eller så kan du finne på din egen utvikdelse av koden. For å få maks poengsum på oppgaven må du gjøre to av forslagene, eventuelt erstatte ett eller begge med noe du kommer på selv, som er tilsvarende stort i omfang.

Når du nå er ferdig med Del A og B skal du ha et dungeon crawler (evt. rabbit hopping) spill med en spiller som kan
* bevege seg rundt på kartet
* se synlige ting rundt seg
* plukke opp ting
* bære én ting
* legge fra seg ting
* angripe

Du har også gulrøtter som kan plukkes opp, og kaniner som kan spise gulrøtter og angripe ting.

Herfra kan du enten fortsette å legge på funksjonalitet på koden du har, eller brette opp ermene og lage ditt eget spill med andre klasser  enn de vi har gitt her – det kanskje mest aktuelt å bytte ut item-klassene. Du kan gjerne skrive en “intro” til spillet, og “flavour” i `displayMessage`. Uansett om du vil bruke klassene vi har gitt deg eller lage dine egne, må du huske å levere klassene du jobbet med i del A og B.

Du må selv bestemme hva mer som skal legges til av funksjonalitetet, og hva du evt. vil gjøre for å gi spillet litt mer stemning (grafikk, kanskje?). Som nevnt tidligere kan du velge “setting” og “storyline” som du vil – du trenger ikke å lage huleutforskning med magi, sverd, orker og hobbiter – eller med kaniner og gulrøtter. Skriv ned forklaring til det du gjør i README.md – både funksjonalitet som du legger til, og kreative ting du finner på – vi legger ikke nødvendigvis merke til alt når vi prøvekjører ting, så det er greit å vite hva vi skal se etter.

Siden vi ikke vet hva slags lure ting dere kommer på å implementere, skriver vi de følgende forslagene ut ifra koden vi har gitt dere. Du står fritt til å i stedet lage tilsvarende funksjonalitet for klasser i spillet du lager selv. Når vi skriver “spilleren”, kan det altså være at du vil bruke en annen klasse enn `Player`-klassen, eller at du vil gi denne funksjonaliteten til en `INonPlayer`.

Følgende er forslag til hva du kan gjøre, ikke nødvendigvis i den rekkefølgen.

### C1: Ting/items som påvirker spillet

Spilleren vår vil gjerne kunne finne andre ting i labyrinten enn gulrøtter. I denne delen kan du lage flere ting, for eksempel noe som gjør spilleren flinkere til å angripe, eller øker helsen den har. Du kan lage “healings potions” som øker helsepoengene til spilleren. Dersom du vil at spilleren skal kunne slåss bedre, kan du lage våpen, for eksempel av typen langkost. Dersom du angriper en kanin med en langkost gir det deg kanskje en høyere attack score enn uten. (Du kan selv velge hvordan type ting påvirker angrep, helsepoeng, og kanskje også hverandre).

Det kan være at tingen tar effekt når du plukker den opp, eller kanskje spilleren må trykke på en tast (klassiske roguelikes bruker gjerne `q` for “quaff a potion” eller `w` for “wield a weapon”).

For å implementere disse tingene må du lage klasser for dem og extende IItem. De vil likne på gulrot-klassen. Du må selv finne ut hvordan de skal tegnes. Se styling-seksjonen for tips til grafikk.

### C2: Inventory - bærenett

Dersom spilleren kan finne flere forskjellige typer ting i labyrinten, er det kjekt å kunne bære mer enn én ting av gangen. Hvis labyrinten enda bare har gulrøtter, vil spilleren kanskje kunne samle mange av dem, og bære dem med seg – og alt du trenger å holde rede på er antall gulrøtter. Men for varierende typer ting, trenger vi en bedre løsning.

I objektorientering er vi opptatt av abstraksjon og forståelig kode, så selv om vi kunne ha latt spilleren få flere feltvariabler for å holde styr på alle tingene den bærer, så vil vi heller implementere en egen klasse for en *samling* (eller Collection) av ting. Java har standard lister, men her trenger vi kanskje noe litt annet: det bør jo gjerne ha en begrensning på hvor mye man kan bære med seg.

* Samlingen bør lagre `IItem`-objekter, eventuelt at den er generisk med `<T extends IItem>`.
* Du kan velge om begrensningen er på antall elementer, eller på total størrelse (`getSize()`) for alle elementene i samlingen.
* Du må ha metoder for å putte noe inn i samlingen, hente noe ut, sjekke om det er ledig plass, osv. Du trenger ikke ha indekser – det holder å kunne putte inn og hente ut.
* Hvis noen prøver å putte noe inn i en full samling (eller noe det ikke er plass til i samlingen) bør du kaste en exception.

For tips til implementasjon av samling kan du se på [IList/MyList-listene fra tidligere labber](https://retting.ii.uib.no/inf101.v19.oppgaver/inf101.v19.lab4/tree/master/src/inf101/v19/datastructures). Du kan gjerne bruke Java sitt [standard-bibliotek for samlinger](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html) i implementasjonen (legg merke til at du trenger en konkret implementasjon av interfacet, for eksempel en [ArrayList](https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html)).

* For litt mer solid INF101-design, bør du gjerne designe et eget grensesnitt, f.eks. `IContainer<T extends IItem>` – da kan du også ha flere varianter av samlinger (som f.eks. virker på forskjellig måte).
* Spilleren (og kaninene) kan bruke samlingen du har laget som bærenett eller ryggsekk til å lagre items som blir plukket opp. Brukeren vil antakelig ha lyst til å ha oversikt over hva som er i “sekken” – det er ledig plass på skjermen hvor du kan vise mer informasjon – se på metodene `getPrinter()` (du kan skrive ting på skjermen med `printAt(x,y, text)`), `getFreeTextAreaBounds()` og `clearFreeTextArea()`.
* En mer avansert og artig bruk av `IContainer` er å la den også utvide `IItem`. Da kan du putte ting i sekken din, og legge sekken på bakken – eller putte en kanin inn i en hatt inn i en sekk inn i en koffert og plukke opp og sette fra deg kofferten.

### C3 Styling

Du kan kommet et godt stykke på vei med litt kreativ bruk av tekst-symbolene. For enkel blokk-grafikk (til vegger, f.eks.) så finnes det en del forskjellige tegn du kan bruke i [BlocksAndBoxes](inf101/v19/gfx/textmode/BlocksAndBoxes.java). Hvis du implementer `getPrintSymbol()` så kan du bruke et eget tegn til grafikk-visningen (hvis du bruker bokstaven i `getSymbol()` til noe – fancy fabrikk, f.eks.).

#### Fancy vegger
*(Du kan selvfølgelig gjøre tilsvarende med andre ting enn vegger)*

F.eks. bruker `Wall`-objektene `BlocksAndBoxes.BLOCK_FULL` som symbol. Det kan være litt tricky å få `Wall` til å variere `getSymbol()` avhengig av hvor den har andre vegger som naboer, men du kan i prinsippet lage flere varianter av wall:
   * Her er det mest praktisk å bruk *arv*, f.eks.:

```
public class DiagonalWall extends Wall {
    // Java vil bruke denne i stedet for getSymbol() fra Wall – men alle andre metoder følger
    // med fra Wall.
	@Override
	public String getSymbol() {
		return BlocksAndBoxes.DIAG_FORWARD;
	}
}
```

   * Du trenger også å legge til alle vegg-variantene i `createItem`-metoden.
   * Du trenger at alle vegger har `Wall`-typen, fordi kartet bruker det til å se om et felt er opptatt (bla gjennom items på en lokasjon, sjekk om `item instanceof Wall`). Når du sier at `DiagonalWall extends Wall`, så vi alle diagonale vegger også telle som vanlige vekker.
   * Alternativet ville vært å enten legge til en metode `isWall()` i `IItem`; eller si at en kartcelle er opptatt hvis det er en veldig stor ting der (`getSize()` større enn 100 eller 1000, f.eks.); eller lage et ekstra grensesnitt `IWall` (og både `Wall` og `DiagonalWall` `implements IWall`) og bruke det istedenfor der hvor man trenger å sjekke etter vegger.

En annen mulighet for fancy vegg er å la veggene tilpasse seg omgivelsene. Det krever litt samarbeid mellom Game og Wall – f.eks. kan du finne alle veggene (enten i `beginTurn()` eller når du setter opp kartet) og kalle en ny metode som du lager (f.eks. `setup(IGame game)`). Veggen kan så utforske naboene sine, og finne ut hvilke av dem som er vegger, og velge riktig box-tegn – enten en av `BLOCK_*`-tegnene fra `BlocksAndBoxes`, eller med [Box-drawing characters](https://en.wikipedia.org/wiki/Box-drawing_character) (disse var mye brukt på gamle DOS-datamaskiner, men det er nå en standard del av Unicode-tegnsettet, og `Printer`-klassen støtter dem uavhengig av hvilken font du bruker.). Du kan paste box-tegn (og block-tegn) rett inn i koden din (så lenge [Eclipse er riktig satt opp med UTF-8](https://retting.ii.uib.no/inf101/inf101.v19/wikis/oppsett#fiks-for-tegnsett)), eller bruke [`\uXXXX` escape-koder](https://docs.oracle.com/javase/tutorial/java/data/characters.html) i strengen. F.eks., `"╣"` er `"\u2563"`.

#### Emojis
Hvis du bytter font vil du kunne bruke en haug med praktiske symboler. Du kan sette fonten i `start()` metoden i [Main](inf101/v19/rogue101/Main.java)-klassen. “Symbola” inneholder standard [Unicode emojis](https://en.wikipedia.org/wiki/Emoji#Unicode_blocks) i tillegg til vanlige bokstaver. Du kan [laste den ned herfra](http://users.teilar.gr/~g1951d/).

* Hvis du putter `Symbola.ttf` i `src/inf101/v19/gfx/fonts/`, skal du kunne kjøre `printer.setFont(Printer.FONT_SYMBOLA);` i `start()`, og så bruke f.eks. `"☺️"` (`"\u263a"`) som symbol for spilleren.
* Merk at en del mer obskure Unicode-emojis, som [`"🦆"`](https://en.wikipedia.org/wiki/%F0%9F%A6%86) må skrives med to escape koder (`"\ud83e\udd86"`) og er ikke nødvendigvis med i fonten du bruker.
* Vanlige bokstaver ser dessverre ikke så veldig fine ut i Symbola-fonten, siden grafikken vår baserer seg på monospaced tekst (det går foreløpig ikke an å bruke mer enn én font i systemet).
* Hvis du prøver deg med andre fonter (som du selvfølgelig må oppgi kilde til og ha rettigheter til å bruke – og finne ut hvordan du setter de opp med `Printer`), vil de gjerne ikke passe så veldig godt på skjermen. Det går an å justere størrelse og posisjon på bokstavene med `inf101.v19.gfx.textmode.TextFondAdjuster`.

#### Farger
Du kan lage farger med [ANSI escape-koder](https://en.wikipedia.org/wiki/ANSI_escape_code#Colors). For eksempel `"\u001b[31m" + "@" + "\u001b[0m"` for å lage et rødt @-tegn. `"\u001b[31m"` velger rød tekstfarge og `"\u001b[0m"` skifter tilbake til standard (hvis du ikke har med den så blir all teksten rød).

#### Skilpaddegrafikk
Alternativet til tekstgrafikken er å bruke `ITurtle`/`TurtlePainter`, som du har sett litt i bruk på forelesningene for å lage frosker og ender. Gulroten er tegnet slik – hvis du implementerer `draw()`-metoden for et item, og lar den returnere `true` blir teksten ikke tegnet. Du kan se på gulrot-eksempelet, og på [koden fra forelesningene](https://retting.ii.uib.no/inf101/inf101.v19/wikis/kode-fra-forelesninger) for eksempler.

* Les mer om [skilpaddegrafikk her](https://en.wikipedia.org/wiki/Turtle_graphics)
* Draw metoden får bredden og høyden til kartcellen som parametre. Som standard er ting laget til slik at bredde og høyde er 32 – selv om skjermen viser smale tegn (16x32). Hvis du trenger kontroll over bredden, is stedet for at tegningen blir “skvist” til halv bredde, kan du sette `Main.MAP_AUTO_SCALE_ITEM_DRAW` til `false`.
* Foreløpig støtter grafikksystemet ikke at du kan bruke egne bilder (i jpg eller png filer, f.eks.)

### C5: Meldingsvindu
* lage meldings"vindu"

### C6: Win condition

Det går foreløpig ikke an å vinne spillet – her må du eventuelt være kreativ selv. F.eks. at spilleren vinner når alle gulrøttene er samlet i det ene hjørnet – eller når spilleren er alene igjen – eller noe helt annet.

### C?: Noe du finner på / noe annet vi finner på

* Du står fritt til å finne på ting selv; og det kan også være vi legger ut litt flere ideer underveis.

# Diverse

## Åpne kilder til grafikk / lyd / media
*Foreløpig støtter grafikksystemet ikke at du kan bruke egne bilder (i jpg eller png filer, f.eks.). Du kan uansett tegne med skilpaddegrafikken (TurtlePainter). Det ligger heller ikke med kode for å spille av lyd – men det kan være vi har noe slikt på lur til når du har kommet skikkelig i gang med ting.*

* Om du ikke er flink til å tegne selv, kan du finne glimrende grafikk på [OpenGameArt](http://opengameart.org/) – **husk å skrive i oversiktsdokumentet hvor du har fått grafikken fra** (webside, opphavsperson, copyright-lisens – om du bruker OpenGameArt, finner du opplysningene i *License(s)* og *Copyright/Attribution Notice*).
* [Wikimedia Commons](https://commons.wikimedia.org/wiki/Main_Page) har en god del bilder og andre mediafiler tilgjengelig – du får til og med en “You need to attribute this author – show me how” instruks når du laster ned ting.
* [Kevin MacLeod](https://incompetech.com/music/) er komponist og har masse musikk tilgjengelig som egner seg for spill og småfilmer; du trenger bare å kreditere ham (han bruker vanlig [CC-BY-3.0](http://creativecommons.org/licenses/by/3.0/) lisens).
* Tidligere INF101-student [Øyvind Aandalen](https://soundcloud.com/user-616269685) har litt musikk han har laget på [SoundCloud](https://soundcloud.com/user-616269685) som han sier dere kan bruke som dere vil.



# Gå videre til [**Innlevering**](SEM-1.md#praktisk-informasjon)
