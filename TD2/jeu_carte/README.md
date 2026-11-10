# Jeu de cartes — TD2 (MVC, SOLID, patrons)

Projet Maven du TD « MVC / jeu de cartes » : un jeu de 52 cartes où chaque joueur
reçoit une carte face cachée, la carte la mieux classée gagne
(As > Roi > … > 2, puis Trèfles > Piques > Cœurs > Carreaux).

## Prérequis

- **JDK 17 ou 21** (le `pom.xml` compile en `release 17`, donc les deux conviennent) ;
- **Maven 3.9+**.

Le JDK utilisé par Maven est celui de `JAVA_HOME`, pas forcément celui de ton
terminal : vérifie-le avec `mvn -v` (ligne « Java version »).

## Lancer

```bash
mvn -q clean compile
java -cp target/classes fr.nice.jeu_carte.Main            # vue console, 52 cartes
java -cp target/classes fr.nice.jeu_carte.Main swing      # vue Swing
java -cp target/classes fr.nice.jeu_carte.Main lowest     # la carte la plus basse gagne
java -cp target/classes fr.nice.jeu_carte.Main 32         # paquet de 32 cartes (du 7 à l'As)
java -cp target/classes fr.nice.jeu_carte.Main swing 32 lowest   # les deux options
```

Les arguments sont cumulables et dans n'importe quel ordre ; `Main` les traduit en
une configuration de partie (voir `DeckBuilder` ci-dessous).

## Tests

```bash
mvn -q clean test        # 68 tests JUnit 5 (Jupiter 5.10.2)
```

Les tests couvrent les modèles (`Card`, `Deck`, `Hand`, `Player`), les deux règles
de victoire, la fabrique et le builder, et tout le flux du contrôleur avec une vue
factice `RecordingView` (aucune saisie, aucune fenêtre).

## Dépannage

| Message | Cause | Solution |
|---|---|---|
| `error: release version 21 not supported` | Maven tourne sur un JDK < 21 | le `pom.xml` compile désormais en `release 17` ; sinon `mvn -v` puis pointer `JAVA_HOME` sur un JDK 21 (`export JAVA_HOME=…`) |
| `UnsupportedClassVersionError: … class file version 65.0 … up to 61.0` | des `.class` compilés par un JDK 21 traînent dans `target/` et sont exécutés par un `java` 17 | `mvn clean compile` (le `mvn clean` supprime les classes obsolètes) puis relancer avec le **même** JDK |
| `Error: Could not find or load main class fr.nice.jeu_carte.Main` | la compilation a échoué (ou `target/classes` absent) | lire l'erreur de `mvn compile` avant de lancer `java` |
| `UnsupportedClassVersionError` sur **une seule** classe alors que `mvn` ne dit rien | un `.class` compilé par un autre outil (JDK 21, IDE, ou `target/` versionné) traîne dans `target/classes` | `rm -rf target` puis `mvn clean compile` ; ne jamais committer `target/` |

Vérifier la version des classes produites (61 = Java 17, 65 = Java 21) :

```bash
javap -verbose -cp target/classes fr.nice.jeu_carte.models.Player | grep "major version"
```

> Le dossier `target/` a longtemps été versionné dans ce dépôt : il contenait des
> `.class` compilés en Java 21, dont ceux de classes supprimées depuis
> (`PlayerController`, `View`). Il a été retiré de Git (`.gitignore` à la racine).
> Si un IDE (VS Code, IntelliJ) recompile dans `target/classes` avec son propre
> JDK, vérifie qu'il est réglé sur 17 ou 21 — sinon le mélange de versions
> réapparaît.

## Architecture

| Paquet | Rôle | Points du TD |
|---|---|---|
| `models` | `Rank`, `Color`, `Card`, `Deck`, `Hand`, `Player` | §2.1 – §2.4 |
| `models.factory` | `DeckFactory` (Factory), `DeckType`, `DeckBuilder` + `GameSetup` (Builder) | [PAT] p.3 – p.6 |
| `models.rules` | `WinnerRule` (Strategy) + `HighestCardWins`, `LowestCardWins` | §4.2 – §4.5 |
| `controllers` | `GameController` : machine à états, `addPlayer`, `startGame`, `flipCards`, `evaluateWinner`, `rebuildDeck`, `runOneStep`, `addView` | §3, §4.5, §4.7 |
| `views` | `GameViewable` (interface standard), `GameViewables` (composite) | §4.6, [PAT] p.7 |
| `views.swing` | `GameSwing` (téléchargée au TD page 11) + bouton « Ajouter une vue » | §4.7, [PAT] p.9 |
| `views.legacy` | `VueJeu` (API française historique) + `ConsoleView` | [ADP] p.2 |
| `views.adapter` | `GameViewAdapter` : rend `VueJeu` compatible avec `GameViewable` | [ADP] |

Le contrôleur ne dépend **que** de `GameViewable` et d'une `WinnerRule` injectée ;
il n'écrit jamais à l'écran. Il ne construit plus son paquet : il reçoit un
`GameSetup` (paquet + règle) assemblé par le `DeckBuilder`, lequel délègue la
création du paquet à la `DeckFactory`.

```java
// dans Main : seule la configuration change d'une partie à l'autre
GameSetup setup = new DeckBuilder()
        .size(Deck.SMALL_DECK_SIZE)          // 52 par défaut
        .rule(new LowestCardWins())          // HighestCardWins par défaut
        .build();
GameController controller = new GameController(setup, views);
```

- `DeckType` énumère les paquets connus : `NORMAL_52`, `SMALL_32` (du 7 à l'As) et
  `TEST` (un 10 par couleur, pratique pour tester l'égalité par la couleur) ;
- `DeckFactory.makeDeck()` / `makeDeck(type)` produit un `Deck` sans que l'appelant
  connaisse les boucles de construction ;
- `DeckBuilder` enchaîne `.size(…)`, `.type(…)`, `.rule(…)` puis `.build()` et reste
  réutilisable.

## Déroulement (machine à états)

| État | Action acceptée | Étape du TD |
|---|---|---|
| `AddingPlayer` | `addPlayer(nom)` (5 max), puis `startGame()` | 2, 3, 4 |
| `CardsDealt` | `flipCards()` : retourne, évalue, affiche | 5, 6, 7 |
| `WinnerRevealed` | `rebuildDeck()` (retour aux noms) ou `startGame()` (nouvelle manche) | 8 puis 3 |

Toute action hors état lève `IllegalStateException` : c'est la garde qui empêche
d'ajouter un 6ᵉ joueur ou de retourner des cartes non distribuées.

## Règles vérifiées

- 52 cartes toutes différentes, pioche et remise (face cachée) ;
- As > Roi > Reine > Valet > 10 … > 2 ; égalité tranchée par la couleur ;
- 5 joueurs maximum, un joueur sans carte est signalé ;
- reconstitution du paquet (52 cartes) et enchaînement des manches ;
- adaptation `VueJeu` → `GameViewable` et diffusion aux vues du composite.
