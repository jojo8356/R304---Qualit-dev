# TD2 — MVC / Jeu de cartes · Réponses écrites et audit de conformité

> **Mise à jour.** Les cinq problèmes bloquants du §1.2 ont été corrigés dans le
> projet `TD2/jeu_carte` (réécriture du modèle, du contrôleur et des vues) :
> voir le **§12 — Corrections appliquées**. Les §1 à §11 décrivent l'état
> **initial** du code et servent de justification des choix.

**Projet audité :** `TD2/jeu_carte` (commit `47d7c55`)
**Documents de référence :**

| Réf. | Fichier | Contenu utilisé |
|---|---|---|
| **[TD]** | `TD2/TD_MVC_JeuCartes.pdf` (11 p.) | §1 spécifications, §2 modèle, §3 contrôleur, §3.5–3.7 vue, §4 SOLID, §4.6–4.7 `GameViewable` / DIP |
| **[ADP]** | `TD2/AdapterPattern-GameViewable.pdf` (4 p.) | interface standardisée `GameViewable`, `GameSwingAdapter` |
| **[PAT]** | `TD2/MVC-designPattern-Factory-Builder-Composite.pdf` (12 p.) | Factory, Builder, Composite, Observer / `addView`, threads, `runOneStep` |
| **[ENUM]** | `Leçons/Énumérations java.pdf` | §3.3 « comparer par un attribut, pas par `ordinal()` » |
| **[ALE]** | `Leçons/Aléatoire.pdf` | §4 `Collections.shuffle`, Fisher-Yates |
| **[IN]** | `Leçons/Input et bonne gestion des données.pdf` | valider à la frontière, objet construit = objet valide, `Scanner` |

**Comment lire ce document.** Chaque réponse est écrite en deux temps : **(a)** la réponse rédigée à la question du TD, **(b)** l'état réel de votre code par rapport à cette réponse. Les statuts utilisés : ✅ conforme · ⚠️ partiel / fragile · ❌ absent.

> Remarque de méthode : aucun JDK/Maven n'est disponible dans mon environnement d'analyse, je n'ai donc pas pu exécuter `mvn compile`. Toutes les erreurs signalées ci-dessous proviennent d'une lecture statique ligne à ligne du code (références `fichier:ligne` fournies) ; elles sont vérifiables en une compilation.

---

## 1. Verdict global

### 1.1 Tableau de bord

| Bloc du TD | Attendu | État | Note |
|---|---|---|---|
| §1 Spécifications du jeu (11 points) | partie jouable de bout en bout | 3 ✅ / 2 ⚠️ / 6 ❌ | **~30 %** |
| §2 Modèle (`Rank`, `Color`, `Card`, `Deck`, `Hand`, `Player`) | 6 classes correctes et comparables | 6 classes présentes, 5 défauts de fond | **~65 %** |
| §3 Contrôleur (machine à états, 8 étapes) | `addPlayer`, `startGame`, `flipCards`, `rebuildDeck`, états | 3 états déclarés, 1 méthode utile sur 8 | **~20 %** |
| §3.5–3.7 Vue + `GameViewable` | interface + vue console | `View` vide, aucune interface | **~5 %** |
| §4 SOLID (SRP → DIP + Strategy) | `WinnerRule` injectée | totalement absent | **0 %** |
| [PAT] Factory / Builder / Composite / Observer | 4 patrons | absents | **0 %** |
| [ADP] Adapter | `GameSwingAdapter` | impossible en l'état (pas d'interface de vue) | **0 %** |
| [PAT] p.10-12 Threads / `runOneStep` | pas de boucle infinie, pas de course | absent | **0 %** |
| **Compilation** | `mvn compile` OK | **échoue** (6 erreurs, §8.1) | **—** |

### 1.2 Conclusion en une phrase

Le **modèle est largement amorcé** (52 cartes, mélange, pioche, main, joueur), mais **rien n'est branché** : le contrôleur ne compile pas, la vue est vide, la comparaison des cartes donne le **mauvais gagnant** (l'As est actuellement la carte la plus faible), et aucun des chapitres SOLID/patrons du TD n'est traité. En l'état, le programme ne démarre pas et la partie décrite au §1 ne peut pas se dérouler.

---

## 2. Conformité aux spécifications du jeu — [TD] §1, p.1

### 2.1 Audit point par point

| # | Exigence | Où dans votre code | Statut | Écart / action |
|---|---|---|---|---|
| 1 | Jeu standard de **52 cartes** | `Deck.initializeDeck()` — 4 couleurs × 13 rangs = 52 | ✅ | RAS |
| 2 | **Saisir** les noms des joueurs | `GameController.createPlayers()` | ⚠️ | Méthode déclarée `ArrayList<Player>` mais **sans `return`** ; `players` jamais initialisé ; `numberOfPlayers` jamais affecté → ne compile pas et planterait en NPE |
| 3 | **Limiter à 5** joueurs | `GameController.java:37` (`< 2 || > 5`) | ✅ | Fonctionne, mais la borne doit être une constante `MAX_PLAYERS` et être vérifiée **aussi** dans `addPlayer()` (la règle appartient au contrôleur, pas à la saisie) |
| 4 | **Mélanger** le jeu | `Deck.shuffle()` → `Collections.shuffle` | ✅ | Conforme à [ALE] §4 et à [TD] §2.3 (qui autorise `Collections.swap`). Mais mélange fait **une seule fois dans le constructeur** : la manche suivante ne sera pas mélangée |
| 5 | **Distribuer 1 carte face cachée** | `Card.isFaceUp = false` + `GameController.startGame()` | ⚠️ | La distribution existe mais : pas de `deck.shuffle()` avant, aucun appel à la vue, aucun état intermédiaire, `startGame()` ne vérifie pas l'état |
| 6 | **Retourner** les cartes | `Card.flip()` existe… jamais appelé | ❌ | Aucune méthode `flipCards()` dans le contrôleur, aucun affichage |
| 7 | **Vérifier la meilleure carte** (As > Roi > … > 2) | `Card.compare()` `Card.java:22-29` | ❌ | Compare des **`ordinal()`** : `ACE` est déclaré en 1ᵉʳ ⇒ l'As est **le plus faible**. Le jeu de valeurs `1..13` de `Rank` n'est jamais utilisé |
| 8 | **Égalité tranchée par la couleur** (Trèfles > Piques > Cœurs > Carreaux) | `Color.java:4-7` | ❌ | Ordre inversé (Cœurs 1 < Carreaux 2, et Piques 4 > Trèfles 3) ; `compare()` renvoie `0` en cas d'égalité de rang sans jamais consulter la couleur |
| 9 | **Afficher** nom + carte du gagnant | — | ❌ | Rien dans `View`, rien dans le contrôleur |
| 10 | **Remettre** les cartes dans le paquet | — | ❌ | Aucun `rebuildDeck()` ; `Deck.addCard()` existe mais n'est jamais appelé |
| 11 | **Revenir au mélange** (manche suivante) | — | ❌ | Pas de boucle ni de transition d'état vers une nouvelle manche |

**Bilan §1 : 3 ✅ / 2 ⚠️ / 6 ❌.** La partie ne peut pas être jouée : on sait distribuer une carte, mais on ne sait ni la retourner, ni désigner un gagnant, ni rejouer.

### 2.2 Les trois écarts qui faussent tout le jeu

1. **Hiérarchie des rangs** — `Rank.java:4` déclare `ACE("Ace", 1)` en première position. Or :
   - `Card.compare()` utilise `ordinal()` ⇒ As = 0 = plus faible ;
   - `Rank.getValue()` (1..13) donnerait le même résultat.
   La spécification exige `As > Roi > Reine > Valet > 10 > … > 2`. **Correction :** déclarer les rangs dans l'ordre croissant `TWO … KING, ACE` avec `ACE = 14` (§3.2).
2. **Hiérarchie des couleurs** — `Color.java:4-7` : `HEARTS(1), DIAMONDS(2), CLUBS(3), SPADES(4)`. La spécification donne `Trèfles > Piques > Cœurs > Carreaux`, soit `CLUBS=4, SPADES=3, HEARTS=2, DIAMONDS=1`. **Les deux paires sont inversées.**
3. **Égalité non tranchée** — `Card.compare()` `return 0` dès que les rangs sont égaux : la couleur n'est jamais consultée, donc la règle d'égalité du §1 est inopérante.

À noter aussi : les cartes s'affichent en anglais (`"Ace"`, `"Hearts"`) alors que la spécification est rédigée en français (`As`, `cœurs`). Pour une vue console, prévoyez un libellé d'affichage séparé du nom de constante.

---

## 3. Réponses « Comment planifier le modèle ? » — [TD] §2, p.1-2

### 3.1 Question du TD : quels sont les éléments du modèle ?

On relit l'énoncé et on repère les **noms** : un **jeu de cartes** (collection de cartes), des **joueurs**, un **rang**, une **couleur**. Il manque le lien joueur ↔ cartes : « un joueur possède une carte pendant la partie » ⇒ on regroupe les cartes d'un joueur dans une classe **`Hand`**. C'est ce qui respecte **SRP** : la main sait gérer *une collection de cartes* (ajouter, retirer, montrer), le joueur ne gère que *son identité* (nom) + *sa main*. Si les règles changent (nombre de cartes par main, façon de les manipuler), seule `Hand` change ; `Player` reste stable quel que soit le jeu.

Votre code suit exactement ce découpage (`Hand` existe, `Player` a `name` + `hand`) : **la planification est bonne**, c'est l'implémentation qui est incomplète.

### 3.2 §2.1 — Générer le rang et la couleur (réponse + état)

> **Réponse attendue.** Deux `enum` (`Rank`, `Color`) portant chacune une **valeur entière**, afin de comparer deux cartes par soustraction/comparaison d'entiers.

```java
public enum Rank {
    TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5), SIX("6", 6),
    SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9), TEN("10", 10),
    JACK("Valet", 11), QUEEN("Reine", 12), KING("Roi", 13), ACE("As", 14);

    private final String label;
    private final int strength;          // plus grand = plus fort

    Rank(String label, int strength) { this.label = label; this.strength = strength; }
    public String getLabel()   { return label; }
    public int    getStrength(){ return strength; }
}
```

```java
public enum Color {
    DIAMONDS("Carreaux", 1), HEARTS("Cœurs", 2), SPADES("Piques", 3), CLUBS("Trèfles", 4);

    private final String label;
    private final int strength;

    Color(String label, int strength) { this.label = label; this.strength = strength; }
    public String getLabel()    { return label; }
    public int    getStrength() { return strength; }
}
```

Deux règles d'implémentation à retenir :
- **déclarer dans l'ordre croissant de force** : l'ordre de déclaration (`ordinal()`) et la valeur entière disent alors la même chose, ce qui évite les incohérences silencieuses ;
- **comparer par l'attribut, pas par `ordinal()`** ([ENUM] §3.3) : `ordinal()` est « figé » et se casse dès qu'on réordonne les constantes.

> **État de votre code.** ✅ Les deux `enum` existent avec un champ entier. ❌ Mais : (a) `ACE = 1` contredit « As > Roi » ; (b) `Color` a les paires Cœurs/Carreaux et Piques/Trèfles inversées ; (c) **les valeurs `getValue()` ne sont utilisées nulle part** (la comparaison passe par `ordinal()`), donc le travail du §2.1 est fait « pour la forme ».

### 3.3 §2.2 — Générer les cartes (réponse + état)

> **Réponse attendue.** `Card` compose un `Rank` et une `Color` **immuables** (jamais réassignés après construction ⇒ champs `final`, pas de setter), plus un attribut **mutable** `faceUp` rendant compte de l'état « face cachée / face visible », avec l'opération `flip()`.

```java
public class Card {
    private final Rank rank;          // immuable
    private final Color color;        // immuable
    private boolean faceUp = false;   // varie pendant la partie

    public Card(Rank rank, Color color) {
        this.rank = rank;
        this.color = color;
    }

    public Rank  getRank()  { return rank; }
    public Color getColor() { return color; }
    public boolean isFaceUp(){ return faceUp; }
    public void flip()      { faceUp = !faceUp; }
    public void faceDown()  { faceUp = false; }

    /** > 0 si cette carte bat l'autre : rang d'abord, couleur en cas d'égalité. */
    public int compareTo(Card other) {
        int byRank = Integer.compare(rank.getStrength(), other.rank.getStrength());
        return byRank != 0 ? byRank
                           : Integer.compare(color.getStrength(), other.color.getStrength());
    }

    public String label() { return rank.getLabel() + " de " + color.getLabel(); }
}
```

> **État de votre code.** ⚠️ La structure est la bonne (`rank`, `color`, `isFaceUp`, `flip()`), mais : `rank`/`color` ne sont pas `final` alors que le §2.2 les dit immuables ; `compare()` ignore la couleur ; il compare des `ordinal()` ; il n'y a pas de `label()`/`toString()`, donc la vue n'a aucun moyen d'afficher « As de trèfle ».

### 3.4 §2.3 — Générer le tas / deck (réponse + état)

> **Réponse attendue.** Une `ArrayList<Card>` (l'ordre compte), remplie par **deux boucles** sur `Color.values()` × `Rank.values()`, avec trois opérations : **mélanger** (`Collections.shuffle` ou `Collections.swap`), **retirer la première carte**, **ajouter une carte**.

```java
public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() { this(52); }

    /** @param size 52, ou 32 (= du 7 à l'As, pour le patron Factory) */
    public Deck(int size) {
        if (size != 32 && size != 52) throw new IllegalArgumentException("Taille invalide : " + size);
        for (Color color : Color.values())
            for (Rank rank : Rank.values())
                if (size == 52 || rank.getStrength() >= 7)
                    cards.add(new Card(rank, color));  // 52 ou 32 cartes, ordre = couleurs × rangs
    }

    public void shuffle()      { Collections.shuffle(cards); }

    public Card drawCard() {
        if (cards.isEmpty()) throw new IllegalStateException("Paquet vide");
        return cards.remove(0);
    }

    public void addCard(Card card) {
        if (card == null) throw new IllegalArgumentException("Carte nulle");
        card.faceDown();                            // remise face cachée
        cards.add(card);
    }

    public int size() { return cards.size(); }
}
```

> **État de votre code.** ✅ `ArrayList`, double boucle, 52 cartes, `shuffle()`, `drawCard()` (avec exception si vide, bien), `addCard()` (avec contrôle de `null`, bien). ⚠️ Deux réserves : le mélange est fait **dans le constructeur** (l'étape 3 du déroulement devient invisible et non rejouable) ; `getLength()` n'est jamais utilisé ; pas de `faceDown()` pour la reconstitution du paquet.

### 3.5 §2.4 — Générer la main et les joueurs (réponse + état)

> **Réponse attendue.** `Hand` = `ArrayList<Card>` vide au départ, avec **voir le contenu / ajouter / retirer**. `Player` = **nom** (indispensable) + `Hand`, avec **présenter sa main / ajouter / retirer**.

```java
public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card)   { cards.add(Objects.requireNonNull(card)); }
    public Card removeCard(int i)    { return cards.remove(i); }
    public List<Card> cards()        { return List.copyOf(cards); }   // vue non modifiable
    public boolean isEmpty()         { return cards.isEmpty(); }
    public int size()                { return cards.size(); }
    public Card getCard(int i)       { return cards.get(i); }

    @Override public String toString() {
        return cards.stream().map(Card::label).collect(Collectors.joining(", "));
    }
}

public class Player {
    private final String name;
    private final Hand hand = new Hand();

    public Player(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Nom vide");
        this.name = name.trim();
    }

    public String getName() { return name; }
    public Hand   getHand() { return hand; }
    public void   addCardToHand(Card card)      { hand.addCard(card); }
    public Card   removeCardFromHand(int index) { return hand.removeCard(index); }
    public String presentHand()                 { return name + " : " + hand; }
}
```

> **État de votre code.** ✅ La structure `Player → Hand` est bonne. ❌ **Problème bloquant :** `models/Player.java` déclare `package fr.nice.jeu_carte.controllers;` et `public class PlayerController` (`Player.java:1,9`). Le fichier ne respecte donc pas la convention *fichier = classe publique* (erreur de compilation) **et** place une classe du modèle dans le paquet des contrôleurs (violation MVC). ⚠️ `Hand` maintient un champ `size` recopié à la main : la moindre modification de la liste le désynchronise — utilisez `cards.size()`. `getCards()` renvoie la liste interne mutable (fuite d'encapsulation : la vue peut modifier le modèle). Il manque une représentation textuelle (« présenter sa main »).

### 3.6 Récapitulatif modèle

| Classe | Existe | Conforme au §2 | Défaut principal |
|---|---|---|---|
| `Rank` | ✅ | ⚠️ | `ACE = 1` (As le plus faible) |
| `Color` | ✅ | ❌ | Paires de couleurs inversées |
| `Card` | ✅ | ⚠️ | non immuable, comparaison sans couleur, pas de libellé |
| `Deck` | ✅ | ✅ | mélange dans le constructeur seulement |
| `Hand` | ✅ | ⚠️ | `size` dupliqué, liste interne exposée, pas de `toString` |
| `Player` | ✅ | ❌ | mauvais paquet / mauvais nom de classe ⇒ ne compile pas |

---

## 4. Réponses « Comment planifier le contrôleur ? » — [TD] §3, p.3-6

### 4.1 Réponse courte

Le contrôleur est un **chef d'orchestre** : il ne connaît ni les règles d'affichage (la vue), ni les détails de comparaison (règle injectée). Il maintient **(1)** une **machine à états** (`AddingPlayer → CardsDealt → WinnerRevealed`), **(2)** le **modèle** (`Deck`, `List<Player>`), **(3)** une référence à la **vue** via son interface. Chaque entrée utilisateur arrive **par la vue** ; le contrôleur la **valide au regard de l'état courant**, met à jour le modèle s'il l'accepte, puis demande à la vue d'afficher.

### 4.2 Les 8 étapes du TD : qui fait quoi

C'est la réponse à « analyser la séquence pour identifier les actions, les requêtes à la vue, et les informations à lire ou valider » :

| # | Étape [TD p.3] | Action du contrôleur | Requête adressée à la vue | Ce qu'il lit / valide |
|---|---|---|---|---|
| 1 | Créer le deck | `new Deck()` (ou `DeckFactory`) dans le constructeur ; `players = new ArrayList<>()` | — | — |
| 2 | Saisir les noms | `addPlayer(name)` | `showPlayerName(index, name)` puis `promptForPlayerName()` | État = `AddingPlayer` ; nom non vide ; `players.size() < 5` |
| 3 | Mélanger | `deck.shuffle()` | — | Paquet non vide ; état = `AddingPlayer` (fin de phase) |
| 4 | Distribuer | `deck.drawCard()` → `player.addCardToHand(card)` pour chaque joueur | `showFaceDownCardForPlayer(index, name)` | Au moins 1 joueur ; `cards.size() ≥ nb joueurs` |
| 5 | Retourner les cartes | `card.flip()` pour toutes les cartes | `showCardForPlayer(index, name, rank, color)` | État = `CardsDealt` |
| 6 | Évaluer le gagnant | `winner = rule.evaluate(players)` (règle **injectée**) | — | Exactement 1 carte par main |
| 7 | Afficher le gagnant | mémorise `winner`, passe l'état à `WinnerRevealed` | `showWinner(name)` | `winner` présent |
| 8 | Reconstituer le deck | `rebuildDeck()` : reprendre chaque carte des mains → `deck.addCard(card)` ; vider les mains | `promptForNewGame()` | État = `WinnerRevealed` |

« Revenir à l'étape de mélange » = l'appel suivant à `startGame()` (ou une méthode `nextRound()`), qui **remélange** avant de redistribuer.

### 4.3 §3.1 — Démarrer le jeu

> **Réponse attendue.** `main` instancie la **vue** (dans son propre paquet), la transmet au **contrôleur**, qui construit le paquet et une liste de joueurs vide, puis appelle une première étape de la machine à états.

```java
public static void main(String[] args) {
    GameViewable view = new CommandLineView();        // vue créée à l'extérieur
    WinnerRule rule   = new HighestCardWins();        // règle injectée
    GameController controller = new GameController(new Deck(), view, rule);
    controller.runOneStep();                          // affiche la consigne « nom du joueur »
}
```

```java
public GameController(Deck deck, GameViewable view, WinnerRule rule) {
    this.deck = Objects.requireNonNull(deck);
    this.view = Objects.requireNonNull(view);
    this.rule = Objects.requireNonNull(rule);
    this.players = new ArrayList<>();
    this.state = GameState.AddingPlayer;
    view.setController(this);            // la vue connaît le contrôleur…
}
```
…mais **le contrôleur ne connaît que l'interface** : si demain on ajoute `GameSwing`, seule la ligne `new CommandLineView()` change. C'est exactement l'argument DIP du §3.1 (p.4).

> **État de votre code.** ❌ `Main.java:13-16` fait l'inverse du TD : il appelle des méthodes **statiques** qui n'existent pas (`GameController.getLenPlayers()`, `createPlayers(int)`), instancie `new GameController()` (aucun constructeur sans argument, `GameController.java:25`) puis `startGame(players)` (signature inexistante, `:61`). Aucun `Deck` ni `View` n'est créé. → **4 erreurs de compilation** et une architecture à l'envers (le contrôleur ne possède pas ses objets de base).

### 4.4 §3.2 — Saisie des noms

> **Réponse attendue.** Chaque nom est transmis par la vue à `addPlayer` ; le contrôleur ajoute un `Player` à la liste et demande l'affichage du nom. Quand la saisie s'arrête, `startGame()` **ferme la phase d'ajout** : plus aucun joueur ne peut être ajouté.

```java
public void addPlayer(String name) {
    requireState(GameState.AddingPlayer);                       // la phase est-elle ouverte ?
    if (name == null || name.isBlank()) throw new IllegalArgumentException("Nom vide");
    if (players.size() >= MAX_PLAYERS) throw new IllegalStateException("5 joueurs maximum");
    Player p = new Player(name);
    players.add(p);
    view.showPlayerName(players.size() - 1, p.getName());
    runOneStep();
}

/** Garde d'état : refuse toute entrée qui n'a pas de sens à cette étape. */
private void requireState(GameState expected) {
    if (state != expected)
        throw new IllegalStateException("Action impossible dans l'état " + state);
}
```

> **État de votre code.** ⚠️ La boucle de saisie existe (`createPlayers()`), mais : elle ne retourne rien, n'est jamais appelée correctement, lit `numberOfPlayers` qui vaut toujours `0`, et écrit dans `players` qui est `null`. Surtout, **le contrôleur affiche lui-même** (`System.out.println`) au lieu de passer par la vue : la logique d'interface est dans le contrôleur, ce que le TD interdit (§3.5). `addPlayer()` n'existe pas, donc la règle « plus de joueur après le démarrage » n'est pas exprimable.

### 4.5 §3.3 — Donner les cartes, révéler, rejouer

```java
public void startGame() {
    requireState(GameState.AddingPlayer);
    if (players.isEmpty()) throw new IllegalStateException("Aucun joueur");
    deck.shuffle();                                        // étape 3
    for (Player p : players) p.addCardToHand(deck.drawCard());   // étape 4
    for (int i = 0; i < players.size(); i++)
        view.showFaceDownCardForPlayer(i, players.get(i).getName());
    state = GameState.CardsDealt;
    runOneStep();
}

public void flipCards() {
    requireState(GameState.CardsDealt);
    for (int i = 0; i < players.size(); i++) {
        Player p = players.get(i);
        Card c = p.getHand().getCard(0);
        c.flip();                                          // étape 5
        view.showCardForPlayer(i, p.getName(), c.getRank().getLabel(), c.getColor().getLabel());
    }
    winner = rule.evaluate(players);                       // étape 6, règle délégée
    state = GameState.WinnerRevealed;
    winner.ifPresent(w -> view.showWinner(w.getName()));   // étape 7
    runOneStep();                                          // affiche la consigne « nouvelle manche »
}

public void rebuildDeck() {                                 // étape 8
    requireState(GameState.WinnerRevealed);
    for (Player p : players) {
        while (!p.getHand().isEmpty()) deck.addCard(p.removeCardFromHand(0));
    }
    winner = Optional.empty();
    state = GameState.AddingPlayer;   // puis startGame() remélange ⇒ « revenir à l'étape de mélange »
    runOneStep();
}
```

> **État de votre code.** ⚠️ Seule la distribution existe (sans mélange ni affichage). ❌ `flipCards`, `evaluateWinner`, `rebuildDeck`, `nextRound` sont absents ; `Card.flip()` et `Card.compare()` ne sont **jamais appelés** (code mort).

### 4.6 §3.4 — Organisation du contrôleur

| Membre | Type | Rôle |
|---|---|---|
| `deck` | `Deck` | le tas, créé/injecté au démarrage |
| `players` | `List<Player>` | vide au départ, rempli pendant `AddingPlayer` |
| `view` | `GameViewable` | **abstraction** (jamais `View` concrète) |
| `rule` | `WinnerRule` | **règle de victoire injectée** (Strategy, §4.4-4.5 du TD) |
| `state` | `GameState` | `AddingPlayer` / `CardsDealt` / `WinnerRevealed` |
| `winner` | `Optional<Player>` | mémorisé entre l'évaluation et l'affichage |
| `hasPlayers()` | `boolean` | lecture seule pour la vue (« puis-je lancer la partie ? ») sans lui donner accès au modèle |

> **État de votre code.** ✅ Les trois états portent les bons noms et les champs `deck`, `view`, `players`, `gameState` existent. ❌ Pas de règle injectée, `players` non initialisé, `view` typée sur une classe concrète vide, `winner` absent, aucune transition d'état sauf `CardsDealt` en fin de `startGame()`.

### 4.7 « Comment le contrôleur sait ce qui lui a été introduit ? »

**Réponse.** Il ne « devine » pas : **la vue lui transmet un message explicite** (`addPlayer(nom)`, `startGame()`, `flipCards()`, `rebuildDeck()`), et **l'état courant** dit quels messages sont légitimes. Deux protections complémentaires :
1. **Une méthode = une intention** (`flipCards()` ≠ « lire une chaîne et deviner ») ;
2. **une garde en tête de méthode** (`requireState(...)`) qui lève `IllegalStateException` si l'entrée n'est pas valide à cette étape.

C'est le point que le TD résume par « le contrôleur doit connaître l'étape courante et déterminer quelles entrées sont valides ». Dans votre code, `getLenPlayers()` lit directement `System.in` : le contrôleur devient sa propre vue, et aucune validation d'état n'est possible.

### 4.8 §3.6 — Machine à états (transitions attendues)

| État courant | Entrée reçue | Garde | Action | État suivant |
|---|---|---|---|---|
| `AddingPlayer` | `addPlayer(name)` | `size < 5`, nom non vide | crée le joueur, `showPlayerName` | `AddingPlayer` |
| `AddingPlayer` | `startGame()` | ≥ 1 joueur | mélange, distribue, `showFaceDownCardForPlayer` | `CardsDealt` |
| `CardsDealt` | `flipCards()` | 1 carte / main | retourne, `showCardForPlayer`, évalue, `showWinner` | `WinnerRevealed` |
| `WinnerRevealed` | `rebuildDeck()` | — | reprend les cartes, `promptForNewGame` | `AddingPlayer` |
| autre | toute autre entrée | — | `IllegalStateException` | inchangé |

---

## 5. Réponse « Comment implémentez-vous la vue ? » — [TD] §3.5, §3.7 + [ADP] p.1

### 5.1 Le contrat `GameViewable`

La vue est **à la fois interface utilisateur et source d'événements**. Le contrôleur ne lui demande qu'**une chose** : afficher. Elle ne reçoit **jamais** d'objet du modèle — seulement des données plates (indice, nom, libellé de rang, libellé de couleur). C'est la traduction exacte de [TD p.5] : « la vue ne manipule jamais directement les objets du modèle ».

```java
public interface GameViewable {
    void promptForPlayerName();
    void promptForFlip();
    void promptForNewGame();

    void showPlayerName(int index, String name);
    void showFaceDownCardForPlayer(int index, String name);
    void showCardForPlayer(int index, String name, String rank, String suit);

    void showWinner(String name);

    void setController(GameController gc);
}
```
C'est la signature **exacte** de [ADP p.1] (8 méthodes) : ne l'inventez pas, la vue Swing fournie au TD p.11 l'implémente telle quelle. Attention à une incohérence du polycopié : p.11, `GameSwing.showWinner` prend un `StringBuilder` alors que l'interface déclare `String` — **gardez `String`**.

### 5.2 Vue console

```java
public class CommandLineView implements GameViewable {
    private GameController controller;
    private final Scanner in = new Scanner(System.in);   // jamais fermé : System.in reste ouvert

    @Override public void setController(GameController gc) { this.controller = gc; }

    @Override public void promptForPlayerName() {
        System.out.print("Nom du joueur (Entrée seule pour terminer) : ");
        String name = in.nextLine().trim();
        if (name.isEmpty()) {
            if (controller.hasPlayers()) controller.startGame();
            else System.out.println("Ajoutez au moins un joueur.");
        } else {
            try { controller.addPlayer(name); }
            catch (RuntimeException e) { System.out.println("Refusé : " + e.getMessage()); }
        }
    }

    @Override public void showPlayerName(int i, String name)  { System.out.println("[" + i + "] " + name); }
    @Override public void showFaceDownCardForPlayer(int i, String name) {
        System.out.println(name + " : [carte face cachée]");
    }
    @Override public void showCardForPlayer(int i, String name, String rank, String suit) {
        System.out.println(name + " : " + rank + " de " + suit);
    }
    @Override public void showWinner(String name) { System.out.println("Gagnant : " + name); }
    @Override public void promptForFlip()     { System.out.print("Entrée pour retourner les cartes…"); in.nextLine(); controller.flipCards(); }
    @Override public void promptForNewGame()  { System.out.print("Entrée pour une nouvelle manche…"); in.nextLine(); controller.rebuildDeck(); }
}
```

### 5.3 Ce que la vue ne doit **jamais** faire

- comparer des cartes, décider du gagnant, distribuer, mélanger (logique métier) ;
- manipuler `Deck`, `Card`, `Hand`, `Player` (elle reçoit des `String`/`int`) ;
- choisir la prochaine étape du jeu (c'est le contrôleur).

> **État de votre code.** ❌ `View.java` ne contient **aucune** méthode d'affichage, ne dépend d'aucune interface, et `public void View()` n'est pas un constructeur (méthode inutile). Le contrôleur imprime directement dans la console (`System.out.println`, `Scanner`) : la vue est de fait « fondue » dans le contrôleur, exactement ce que le TD interdit. C'est **le plus gros écart du projet** avec [TD] §3.5-3.7 et [ADP].

---

## 6. Réponses SOLID — [TD] §4, p.7-10

### 6.1 §4.1-4.2 : le point de départ et SRP

**Constat du TD.** Écrire l'évaluation du gagnant dans le contrôleur mélange deux responsabilités : *dérouler la partie* et *décider qui gagne*. **Réponse SRP :** extraire une classe dédiée (ex. `WinnerEvaluator`) ; le contrôleur ne décide plus, il **demande** un résultat.

```java
public class WinnerEvaluator {
    public Optional<Player> evaluate(List<Player> players) {
        Player best = null;
        for (Player p : players) {
            if (best == null || p.getHand().getCard(0).compareTo(best.getHand().getCard(0)) > 0)
                best = p;
        }
        return Optional.ofNullable(best);
    }
}
```
Nuance du TD : cela suffit **tant qu'il n'y a qu'une règle**.

### 6.2 §4.3 : le problème O/C

**Dès qu'on ajoute une deuxième règle** (« la carte la plus basse gagne »), il faut ajouter un booléen `lowestWins`, des `if`, ou un `switch` dans la méthode : à chaque nouvelle règle on **modifie** du code existant ⇒ violation d'**Open/Closed**.

### 6.3 §4.4 : passage à une interface (Strategy)

```java
public interface WinnerRule {
    Optional<Player> evaluate(List<Player> players);
}

public class HighestCardWins implements WinnerRule { /* max par Card.compareTo */ }
public class LowestCardWins  implements WinnerRule { /* min par Card.compareTo */ }
```
Ajouter une règle = **ajouter une classe**, pas modifier le contrôleur (OCP).

### 6.4 §4.5 : injection de dépendance

« Le contrôleur ne doit pas créer lui-même la règle ; elle lui est fournie au moment de sa création. » ⇒ constructeur `GameController(Deck, GameViewable, WinnerRule)`. Le contrôleur reste inchangé quelle que soit la règle (DIP).

### 6.5 §4.6 : `GameViewable`, c'est quel principe / quel pattern ?

**Réponse (celle du TD).**
- **ISP (I)** : interface petite, ciblée, cohérente (8 méthodes d'affichage/événements, rien de plus) ;
- **OCP (O)** : on ajoute `GameSwing`, `CommandLineView`, un composite… sans toucher au contrôleur ;
- **découplage MVC** : vue ↔ contrôleur via une interface, pas via des classes concrètes ;
- **ce n'est pas un design pattern en soi**, c'est une abstraction utilisée par Strategy / Adapter / Facade.

### 6.6 §4.7 : DIP et ajout d'une nouvelle vue — checklist de vérification

| Critère [TD p.10] | Votre code |
|---|---|
| Le contrôleur n'utilise **que** `GameViewable` | ❌ il n'y a pas d'interface ; `View` concrète |
| La vue reçoit son contrôleur via `setController(...)` | ⚠️ `View.setGameController` existe, mais `View` n'est jamais instanciée |
| Aucun appel à une vue concrète dans le contrôleur | ⚠️ pire : aucun appel à la vue du tout, le contrôleur écrit dans `System.out` |
| Aucune logique métier dans la vue | ⚠️ non applicable (vue vide), mais la logique d'E/S est dans le contrôleur |

Tant que ces quatre lignes ne sont pas vertes, **brancher `GameSwing` est impossible** : c'est le prérequis explicite du TD.

---

## 7. Réponses « patrons de conception » — [PAT] + [ADP]

### 7.1 Factory ([PAT] p.3-4)

**Problème.** `new Deck()` fige le type de paquet dans le contrôleur. Pour un jeu à 32 cartes, il faudrait modifier le contrôleur.
**Réponse.** `DeckFactory.makeDeck(type)` renvoie `SmallDeck` / `NormalDeck` / `TestDeck` ; le client ne dépend plus du constructeur concret.

```java
public enum DeckType { SMALL_32, NORMAL_52, TEST }

public class DeckFactory {
    public Deck makeDeck(DeckType type) {
        return switch (type) {
            case SMALL_32  -> new Deck(32);
            case NORMAL_52 -> new Deck(52);
            case TEST      -> new TestDeck();
        };
    }
}
```
(Le polycopié inverse les libellés `NormalDeck`/`SmallDeck` p.3 : gardez une nomenclature cohérente, l'important est le principe.)

### 7.2 Builder ([PAT] p.5-6)

Objet **complexe** composé des **mêmes parties** mais avec des **variantes** : ici, taille du paquet + règle de victoire.
```java
Deck deck = new DeckBuilder()
        .size(52)
        .rule(new HighestCardWins())
        .build();
```
Le builder assemble ; il ne remplace pas la factory (factory = *quel* objet, builder = *comment l'assembler*).

### 7.3 Composite ([PAT] p.7)

Le contrôleur doit traiter **une** vue ou **plusieurs** de la même façon ⇒ composite.
```java
public class GameViewables implements GameViewable {
    private final List<GameViewable> views = new CopyOnWriteArrayList<>();

    public void add(GameViewable v) { views.add(v); }

    @Override public void showWinner(String name) { views.forEach(v -> v.showWinner(name)); }
    @Override public void showPlayerName(int i, String n) { views.forEach(v -> v.showPlayerName(i, n)); }
    // … idem pour les 6 autres méthodes
}
```
Le contrôleur ne connaît qu'un `GameViewable` : `new GameViewables()` puis `views.add(new CommandLineView())`. Aucune signature ne change ⇒ c'est le bénéfice annoncé par le TD.

### 7.4 Observer / `addView` ([PAT] p.9)

Travail demandé : un bouton Swing crée une **nouvelle** vue et l'enregistre auprès du contrôleur.
```java
public void addView(GameViewable newView) {
    newView.setController(this);      // la nouvelle vue reçoit le contrôleur déjà présent
    view.add(newView);                // ici view est le composite GameViewables
    runOneStep();                     // elle se met immédiatement à jour
}
```
Les vues sont des observateurs : le modèle (sujet) ne change pas quand on ajoute/enlève une vue.

### 7.5 Threads / concurrence et `runOneStep` ([PAT] p.10-12)

**Problème.** Une boucle `while (true) { run(); }` itère la liste des vues sur le thread principal pendant que l'EDT Swing ajoute une vue via `addView` ⇒ **`ConcurrentModificationException`** et condition de course ; de plus la boucle **monopolise un cœur** et ne correspond pas au modèle événementiel de Swing.

**Réponse en quatre points :**
1. **Supprimer la boucle infinie** → `runOneStep()` qui ne joue **qu'une** étape de la machine à états :
```java
public void runOneStep() {
    switch (state) {
        case AddingPlayer  -> view.promptForPlayerName();
        case CardsDealt    -> view.promptForFlip();
        case WinnerRevealed-> view.promptForNewGame();
    }
}
```
2. Le `main` fait **un** appel initial ; ensuite **chaque méthode du contrôleur** (après avoir modifié modèle/état) rappelle `runOneStep()`.
3. Protéger la liste partagée : `CopyOnWriteArrayList` **ou** `synchronized`, et mettre à jour les composants Swing sur l'EDT (`SwingUtilities.invokeLater`).
4. Ne pas fermer `System.in` : `Scanner.close()` sur `System.in` ferme l'entrée standard — c'est exactement le bug présent deux fois dans votre `GameController` (`:43`, `:57`).

### 7.6 Adapter : rendre votre vue compatible avec `GameViewable` ([ADP])

**Prérequis que vous n'avez pas.** L'adapter suppose une **interface de vue personnelle** (`JeuVisible` dans le polycopié) que le contrôleur connaît déjà. Votre `View` est une **classe vide** : il faut d'abord l'extraire en interface.

```java
public interface VueJeu {                      // votre nommage, cf. [ADP] p.2
    void demandeNomJoueur();
    void demandeRetournerCarte();
    void demandeNouveauJeu();
    void montreNomJoueur(int index, String nom);
    void montreFaceCacheJoueur(int index, String nom);
    void montreCarteJoueur(int index, String nom, String rang, String couleur);
    void montreGagnant(String nom);
    void setController(GameController gc);
}
```

```java
public class GameSwingAdapter implements VueJeu {
    private final GameViewable target;          // la vue Swing standard

    public GameSwingAdapter(GameViewable target) { this.target = target; }

    @Override public void demandeNomJoueur()        { target.promptForPlayerName(); }
    @Override public void demandeRetournerCarte()   { target.promptForFlip(); }
    @Override public void demandeNouveauJeu()       { target.promptForNewGame(); }
    @Override public void montreNomJoueur(int i, String n)      { target.showPlayerName(i, n); }
    @Override public void montreFaceCacheJoueur(int i, String n){ target.showFaceDownCardForPlayer(i, n); }
    @Override public void montreCarteJoueur(int i, String n, String r, String c) {
        target.showCardForPlayer(i, n, r, c);
    }
    @Override public void montreGagnant(String n)   { target.showWinner(n); }
    @Override public void setController(GameController gc) { target.setController(gc); }
}
```

Câblage final ([ADP] p.4) :
```java
GameViewable swing = new GameSwing();
VueJeu vue = new GameSwingAdapter(swing);
GameController gc = new GameController(deck, vue, new HighestCardWins());
vue.setController(gc);
swing.createAndShowGUI();
gc.runOneStep();
```
Règles du TD : **aucun renommage**, **aucune modification du contrôleur**, **pas d'extension de `GameSwing`**.

> **Direction effectivement implémentée dans le projet.** Le contrôleur dépend de
> l'interface standard `GameViewable` (exigence du §4.7) ; c'est donc la vue
> console, écrite avec l'API française historique `VueJeu`, qui est adaptée :
> `GameViewAdapter implements GameViewable` et délègue à `VueJeu`. La table de
> correspondance est identique à celle du polycopié, seul le sens de la flèche
> s'inverse. Voir §12.5.

---

## 8. Audit du code : ce qui bloque, ce qui est faux, ce qui manque

### 8.1 Ce qui empêche le programme de tourner — 6 erreurs de compilation, 1 bloquant d'exécution, 1 avertissement

| # | Fichier:ligne | Erreur | Correction |
|---|---|---|---|
| 1 | `models/Player.java:1,9` | fichier `Player.java` déclare `package ...controllers;` et `public class PlayerController` | `package fr.nice.jeu_carte.models; public class Player` (+ constructeur `Player(String)` / `Player(String, Hand)`) |
| 2 | `Main.java:13` | `GameController.getLenPlayers()` : appel **statique** d'une méthode d'instance | passer par une instance (et sortir la saisie du contrôleur) |
| 3 | `Main.java:14` | `createPlayers(int)` : méthode inexistante (elle existe sans paramètre) | `controller.createPlayers()` **avec** `return` |
| 4 | `Main.java:15` | `new GameController()` : aucun constructeur sans argument | `new GameController(deck, view, rule)` |
| 5 | `Main.java:16` | `startGame(players)` : signature inexistante (`startGame()` sans paramètre) | `startGame()` |
| 6 | `GameController.java:47-59` | méthode déclarée `ArrayList<Player>` **sans `return`** | ajouter le `return`, ou passer à `void` + champ `players` |
| 7 | `GameController.java:25` vs `Main.java:15` | *(exécution)* le constructeur `(Deck, View)` n'est jamais utilisé : `deck` et `view` restent `null` | construire le contrôleur avec ses dépendances |
| 8 | `GameController.java:16-17` | `enum GameState` close par `};` (point-virgule superflu, toléré mais signalé) | `}` |

Conséquences en cascade même une fois ces lignes corrigées : `players` est `null` (`:54`), `numberOfPlayers` vaut `0` (le champ n'est jamais affecté : `:36` utilise une variable locale), `scanner.close()` ferme `System.in` (`:43`) puis un second `Scanner` lit un flux fermé (`:48`) ⇒ `IllegalStateException`/`NoSuchElementException` au 2ᵉ appel.

### 8.2 Bugs fonctionnels (indépendants de la compilation)

| # | Symptôme | Cause | Impact |
|---|---|---|---|
| 1 | L'**As perd** contre un 2 | `Rank.ACE` déclaré en 1ᵉʳ + comparaison par `ordinal()` | gagnant faux dès qu'un As est en jeu |
| 2 | L'égalité n'est **jamais** tranchée | `Card.compare()` renvoie 0 sans regarder `Color` | règle du §1 non implémentée |
| 3 | Hiérarchie des couleurs **inversée** | `Color.java:4-7` | gagnant faux en cas d'égalité |
| 4 | Une seule manche possible | `shuffle()` uniquement dans le constructeur ; pas de `rebuildDeck` | impossible de rejouer |
| 5 | Le paquet ne se reconstitue pas | `Deck.addCard` jamais appelé | cartes perdues entre les manches |
| 6 | Saisie fragile | `nextInt()` puis `nextLine()` : le `\n` résiduel fait un nom vide ([IN] ch.4) | un joueur fantôme |
| 7 | `Hand.size` désynchronisable | compteur recopié, liste exposée | incohérence silencieuse |
| 8 | Conversion « nom → joueur » absente | `Player(name)` recréé à chaque fois, pas de recherche | le gagnant ne peut pas être identifié |

### 8.3 Violations d'architecture

| Principe | Ce que dit le TD | Votre code |
|---|---|---|
| **SRP** | le contrôleur orchestre, une classe évalue | évaluation inexistante ; le contrôleur fait aussi de l'E/S console |
| **OCP** | ajouter une règle = ajouter une classe | aucune règle abstraite ⇒ rien à étendre |
| **DIP** | le contrôleur dépend de `GameViewable` | il dépend de `System.out`/`Scanner`, et de `View` concrète |
| **MVC** | modèle / vue / contrôleur séparés ; vue instanciée hors du contrôleur | `Player` rangé dans `controllers`, vue vide, E/S dans le contrôleur |
| **ISP** | interface de vue minimale et ciblée | aucune interface |
| **[PAT] Composite** | le contrôleur ne voit qu'une vue | `View` unique, non substituable |
| **[PAT] Threads** | pas de boucle infinie | aucune boucle… mais aucune machine à états non plus |

### 8.4 Éléments manquants (arborescence cible)

```
TD2/jeu_carte/src/main/java/fr/nice/jeu_carte/
├── Main.java                              ⚠️ à réécrire (câblage)
├── controllers/
│   └── GameController.java                ⚠️ addPlayer / flipCards / rebuildDeck / runOneStep
├── models/
│   ├── Card.java  Color.java  Rank.java   ⚠️ ordres + comparaison + libellés
│   ├── Deck.java  Hand.java  Player.java  ⚠️ Player à replacer dans models/
│   ├── rules/
│   │   ├── WinnerRule.java                ❌
│   │   ├── HighestCardWins.java           ❌
│   │   └── LowestCardWins.java            ❌
│   └── factory/
│       ├── DeckFactory.java               ❌ (optionnel [PAT] p.3)
│       └── DeckBuilder.java               ❌ (optionnel [PAT] p.5)
└── views/
    ├── GameViewable.java                  ❌ [ADP] p.1
    ├── CommandLineView.java               ❌
    ├── GameViewables.java                 ❌ composite [PAT] p.7
    ├── swing/GameSwing.java               ❌ [TD] p.11
    └── adapter/GameSwingAdapter.java      ❌ [ADP] p.3
```

Et côté outillage : `pom.xml` ne déclare **aucune** dépendance de test alors que les projets du TD1 en ont (`junit-jupiter`) ; ajoutez au minimum des tests sur `Deck` (52 cartes, pioche), `Card.compareTo` (As > Roi, trèfle > pique) et `GameController` (5 joueurs max, `flipCards` refusé hors `CardsDealt`).

```xml
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter</artifactId>
  <version>5.11.0</version>
  <scope>test</scope>
</dependency>
```

---

## 9. Plan d'action priorisé

**P0 — faire compiler (30 min)**
1. `Player.java` → `package ...models; public class Player` (+ constructeur avec `Hand`).
2. `Main` → câblage réel : `new Deck()`, une vue, `new GameController(deck, vue, …)`, puis `runOneStep()`.
3. `GameController.createPlayers()` → `return players` (ou suppression de la méthode au profit de `addPlayer`).
4. Retirer les `scanner.close()` sur `System.in`.

**P1 — rendre la partie conforme au §1 (2 h)**
5. Corriger `Rank` (As = 14, déclaré en dernier) et `Color` (Trèfles 4 > Piques 3 > Cœurs 2 > Carreaux 1).
6. Réécrire `Card.compareTo` avec égalité tranchée par la couleur + `label()`.
7. Implémenter `addPlayer` / `startGame` / `flipCards` / `rebuildDeck` + gardes d'état.
8. Extraire `GameViewable` + `CommandLineView` ; supprimer tout `System.out`/`Scanner` du contrôleur.
9. Vérifier la boucle complète : 5 joueurs max, distribution, révélation, gagnant, reconstitution, nouvelle manche.

**P2 — traiter le reste du TD (2-3 h)**
10. `WinnerRule` + `HighestCardWins`/`LowestCardWins` injectée (SRP/OCP/DIP).
11. `GameViewables` (composite) + `addView` (Observer).
12. `runOneStep()` + `CopyOnWriteArrayList` + `SwingUtilities.invokeLater` (threads).
13. `GameSwing` (p.11) + `GameSwingAdapter` ([ADP]) et câblage dans `Main`.

**P3 — qualité**
14. Tests JUnit (modèle + contrôleur) et `mvn test` vert.
15. `Hand` : supprimer le champ `size`, exposer `List.copyOf(...)`.

---

## 10. Traçabilité : chaque document → question → réponse → statut

| Doc. | Question / exigence | Réponse ici | Statut du code |
|---|---|---|---|
| [TD] p.1 §1 | 11 points du jeu de cartes | §2.1 | 3 ✅ / 2 ⚠️ / 6 ❌ |
| [TD] p.1 §2 | éléments du modèle et lien joueur ↔ cartes | §3.1 | ✅ |
| [TD] p.1 §2.1 | deux `enum` + valeurs entières | §3.2 | ⚠️ valeurs fausses / inutilisées |
| [TD] p.1-2 §2.2 | carte immuable + état face visible | §3.3 | ⚠️ |
| [TD] p.2 §2.3 | deck `ArrayList`, mélange, pioche, remise | §3.4 | ✅ (mélange au constructeur) |
| [TD] p.2 §2.4 | main + joueur (nom, présenter/ajouter/retirer) | §3.5 | ⚠️ `Player` ne compile pas |
| [TD] p.3 §3 | séquence des 8 étapes | §4.2 | ⚠️ 1 étape sur 8 |
| [TD] p.3 §3.1 | démarrage, vue injectée, DIP | §4.3 | ❌ |
| [TD] p.4 §3.2 | saisie des noms, `addPlayer`, fin de phase | §4.4 | ❌ |
| [TD] p.4 §3.3 | distribution / révélation / rejouer | §4.5 | ⚠️ distribution seule |
| [TD] p.4 §3.4 | organisation du contrôleur | §4.6 | ⚠️ |
| [TD] p.4-5 §3.5 | la vue ne manipule pas le modèle | §5.1 | ❌ |
| [TD] p.5-6 §3.6 | états `AddingPlayer` / `CardsDealt` / `WinnerRevealed` | §4.7-4.8 | ⚠️ déclarés, non pilotés |
| [TD] p.6 §3.7 | implémentation console remplaçable par GUI | §5.2 | ❌ |
| [TD] p.7 §4.1-4.3 | SRP puis problème O/C | §6.1-6.2 | ❌ |
| [TD] p.8 §4.4-4.5 | Strategy + injection de dépendance | §6.3-6.4 | ❌ |
| [TD] p.9 §4.6 | `GameViewable` = ISP + OCP + découplage | §6.5 | ❌ |
| [TD] p.10 §4.7 | checklist DIP + nouvelle vue | §6.6 | ❌ |
| [TD] p.11 | code `GameSwing` fourni | §7.6 | ❌ non branché |
| [ADP] p.1 | interface `GameViewable` (8 méthodes) | §5.1 | ❌ |
| [ADP] p.2-4 | `GameSwingAdapter`, câblage `main` | §7.6 | ❌ (pas d'interface de vue) |
| [PAT] p.3-4 | Factory de decks | §7.1 | ❌ |
| [PAT] p.5-6 | Builder (taille + règle) | §7.2 | ❌ |
| [PAT] p.7 | Composite de vues | §7.3 | ❌ |
| [PAT] p.9 | Observer / `addView` | §7.4 | ❌ |
| [PAT] p.10-12 | threads, `ConcurrentModificationException`, `runOneStep` | §7.5 | ❌ |
| [ENUM] §3.3 | comparer par attribut, pas `ordinal()` | §3.2-3.3 | ❌ |
| [ALE] §4 | `Collections.shuffle` | §3.4 | ✅ |
| [IN] ch.2/4 | valider la saisie, ne pas fermer `System.in` | §4.4, §7.5 | ❌ |

---

## 11. Ce qu'il faut retenir pour la soutenance

1. **Ce qui est déjà bon :** le découpage du modèle (`Deck` / `Hand` / `Player` / `Card` / enums) est celui attendu par le TD ; le paquet fait bien 52 cartes et se mélange ; la distribution d'une carte face cachée fonctionne au niveau modèle.
2. **Ce qui est rouge :** le projet **ne compile pas** (7 erreurs), la **hiérarchie des cartes est inversée** (l'As est le plus faible), la **vue est vide** et le contrôleur fait l'interface lui-même, la **machine à états n'est pas pilotée**, et **aucun chapitre SOLID/patrons** (Strategy, DIP, Composite, Observer, Adapter, threads) n'est traité.
3. **Ordre de travail conseillé :** §9 (P0 → P1 → P2). Tant que P0 et P1 ne sont pas verts, les patrons du P2 ne peuvent pas être démontrés : le TD les présente comme des étapes *après* une partie fonctionnelle.

---

## 12. Corrections appliquées

### 12.1 Les cinq problèmes du §1.2

| # | Problème initial | Correction apportée | Fichiers |
|---|---|---|---|
| 1 | Le projet ne compile pas | `Player` replacé dans `fr.nice.jeu_carte.models` (classe `Player`, plus `PlayerController`) ; `Main` réécrit (câblage réel, plus d'appels statiques fantômes) ; `GameController` réécrit (plus de `createPlayers`, plus de `Scanner`) ; `views/View.java` supprimée | `models/Player.java`, `Main.java`, `controllers/GameController.java` |
| 2 | Hiérarchie des cartes inversée | `Rank` déclaré dans l'ordre `2 … Roi, As` avec `ACE = 14` ; `Color` dans l'ordre `Carreaux 1 < Cœurs 2 < Piques 3 < Trèfles 4` ; comparaison sur `getStrength()`, jamais sur `ordinal()` | `models/Rank.java`, `models/Color.java`, `models/Card.java` |
| 3 | Égalité jamais tranchée | `Card.compareTo` compare le rang puis **la couleur** ; `0` uniquement pour deux cartes identiques | `models/Card.java` |
| 4 | Le contrôleur faisait la vue | plus aucun `System.out`/`Scanner` dans le contrôleur ; interface `GameViewable` (8 méthodes, [ADP] p.1), vue console, vue Swing, composite, adaptateur | `views/**`, `controllers/GameController.java` |
| 5 | Rien n'était branché | `addPlayer`, `startGame`, `flipCards`, `evaluateWinner`, `rebuildDeck`, `runOneStep`, `addView` ; `WinnerRule` + 2 règles ; `GameViewables` ; `GameViewAdapter` ; `GameSwing` ; `Card.flip()` et `compareTo()` réellement utilisés | `controllers/`, `models/rules/`, `views/` |

### 12.2 État des spécifications après correction

| # | Exigence [TD] §1 | État |
|---|---|---|
| 1 | jeu standard de 52 cartes | ✅ `Deck()` = 4 couleurs × 13 rangs |
| 2 | saisie des noms | ✅ `ConsoleView.demandeNomJoueur` → `addPlayer` |
| 3 | 5 joueurs maximum | ✅ `GameController.MAX_PLAYERS` + `canAddPlayer()` |
| 4 | mélange | ✅ `deck.shuffle()` à **chaque** manche |
| 5 | une carte face cachée par joueur | ✅ `startGame()` + `showFaceDownCardForPlayer` |
| 6 | retourner les cartes | ✅ `flipCards()` + `showCardForPlayer` |
| 7 | meilleure carte (As > Roi > … > 2) | ✅ `Card.compareTo` |
| 8 | égalité tranchée par la couleur | ✅ Trèfles > Piques > Cœurs > Carreaux |
| 9 | afficher le nom et la carte du gagnant | ✅ `showWinner` (+ carte affichée avant) |
| 10 | remettre les cartes dans le paquet | ✅ `rebuildDeck()` / `recoverCards()` |
| 11 | revenir à l'étape de mélange | ✅ `rebuildDeck()` → `AddingPlayer` → `startGame()` |

| Bloc | État |
|---|---|
| §4.1-4.2 SRP (`WinnerRule` extraite) | ✅ |
| §4.3-4.4 OCP + Strategy (`HighestCardWins`, `LowestCardWins`) | ✅ |
| §4.5 DIP (règle et vue injectées) | ✅ |
| §4.6 ISP / `GameViewable` | ✅ |
| §4.7 DIP + nouvelle vue sans toucher au contrôleur | ✅ (`GameSwing`) |
| [PAT] Composite (`GameViewables`) | ✅ |
| [PAT] Observer / `addView` (+ bouton « Ajouter une vue ») | ✅ |
| [PAT] threads : `runOneStep`, `CopyOnWriteArrayList`, EDT | ✅ |
| [ADP] Adapter | ✅ (`GameViewAdapter` ↔ `VueJeu`) |
| [PAT] Factory / Builder | ⏳ non implémentés (facultatifs, §7.1-7.2) |
| Tests JUnit | ⏳ à ajouter (voir §12.6) |

### 12.3 Arborescence livrée

```
TD2/jeu_carte/src/main/java/fr/nice/jeu_carte/
├── Main.java
├── controllers/GameController.java
├── models/
│   ├── Card.java  Color.java  Deck.java  Hand.java  Player.java  Rank.java
│   └── rules/  WinnerRule.java  HighestCardWins.java  LowestCardWins.java
└── views/
    ├── GameViewable.java  GameViewables.java
    ├── adapter/GameViewAdapter.java
    ├── legacy/VueJeu.java  ConsoleView.java
    └── swing/GameSwing.java
```

### 12.4 Machine à états réellement implémentée

| État | Entrée | Effet | État suivant |
|---|---|---|---|
| `AddingPlayer` | `addPlayer(nom)` | crée le joueur, `showPlayerName` | `AddingPlayer` |
| `AddingPlayer` | `startGame()` | mélange, distribue face cachée, `showFaceDownCardForPlayer` | `CardsDealt` |
| `CardsDealt` | `flipCards()` | retourne, `showCardForPlayer`, `evaluateWinner`, `showWinner` | `WinnerRevealed` |
| `WinnerRevealed` | `rebuildDeck()` | reprend les cartes, `promptForPlayerName` | `AddingPlayer` |
| `WinnerRevealed` | `startGame()` | reprend les cartes, remélange, redistribue | `CardsDealt` |

Toute autre combinaison lève `IllegalStateException` : c'est la garde qui matérialise
« le contrôleur détermine quelles entrées sont valides à cette étape » (TD §3).

### 12.5 Pourquoi l'adaptateur va dans ce sens

Le TD impose que **le contrôleur dépende de `GameViewable`** (§4.7). La vue Swing
l'implémente donc directement. Restait la vue console, écrite avec l'API française
`VueJeu` : c'est elle qui a besoin d'un adaptateur pour être branchée sur
l'interface standard. La correspondance des méthodes est exactement celle du
polycopié (`demandeNomJoueur ↔ promptForPlayerName`, etc.) ; seul le sens de
l'adaptation est inversé, ce qui est cohérent avec l'architecture finale du TD.

Le câblage (`Main`) :

```java
GameViewables views = new GameViewables();
views.add(new GameViewAdapter(new ConsoleView()));
GameController controller = new GameController(views, new HighestCardWins());
controller.runOneStep();
```

### 12.6 Comment vérifier

```bash
cd TD2/jeu_carte
mvn -q clean compile
java -cp target/classes fr.nice.jeu_carte.Main          # console
java -cp target/classes fr.nice.jeu_carte.Main swing    # interface Swing
```

Le `pom.xml` compile en `release 17` : le projet fonctionne aussi bien avec un
JDK 17 qu'avec un JDK 21. Toujours lancer `mvn clean compile` après un changement
de JDK, sinon des `.class` produits par l'autre JDK provoquent un
`UnsupportedClassVersionError` (Maven et `java` doivent utiliser le **même** JDK ;
`mvn -v` indique celui de Maven).

Contrôles déjà effectués sur cette branche :

- **compilation** du projet complet par `javac` (build WASM TeaVM, faute de JDK
  dans l'environnement d'analyse) : **0 erreur** ; les classes JDK absentes de
  cette bibliothèque réduite (`java.util.Scanner`, `javax.swing.*`, `java.awt.*`)
  ont été remplacées par des stubs de même signature, et `getMessage()` par
  `toString()` dans les copies de vérification uniquement ;
- **65 assertions exécutées** sur le vrai code du projet : 52 cartes uniques,
  sens des rangs et des couleurs, main/joueur, limite de 5 joueurs, transitions
  d'état, gagnant réellement le mieux classé, reconstitution du paquet, enchaînement
  de deux manches, adaptateur, composite, `addView`, et une partie console
  complète jouée avec une entrée scriptée.

À ajouter pour finir (facultatif) : dépendance `junit-jupiter` dans `pom.xml` et
tests dans `src/test/java` (les assertions ci-dessus s'y transposent directement).

### 12.7 Ce qui n'a pas changé

`TD1/` est intact. Le `pom.xml` reste volontairement minimal (compilation en
`release 17` pour être compatible JDK 17 **et** 21, encodage UTF-8) ; aucune
dépendance externe n'a été ajoutée.
