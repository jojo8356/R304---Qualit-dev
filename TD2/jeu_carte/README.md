# Jeu de cartes — TD2 (MVC, SOLID, patrons)

Projet Maven du TD « MVC / jeu de cartes » : un jeu de 52 cartes où chaque joueur
reçoit une carte face cachée, la carte la mieux classée gagne
(As > Roi > … > 2, puis Trèfles > Piques > Cœurs > Carreaux).

## Lancer

```bash
mvn -q compile
java -cp target/classes fr.nice.jeu_carte.Main          # vue console
java -cp target/classes fr.nice.jeu_carte.Main swing    # vue Swing
java -cp target/classes fr.nice.jeu_carte.Main lowest   # la carte la plus basse gagne
```

## Architecture

| Paquet | Rôle | Points du TD |
|---|---|---|
| `models` | `Rank`, `Color`, `Card`, `Deck`, `Hand`, `Player` | §2.1 – §2.4 |
| `models.rules` | `WinnerRule` (Strategy) + `HighestCardWins`, `LowestCardWins` | §4.2 – §4.5 |
| `controllers` | `GameController` : machine à états, `addPlayer`, `startGame`, `flipCards`, `evaluateWinner`, `rebuildDeck`, `runOneStep`, `addView` | §3, §4.5, §4.7 |
| `views` | `GameViewable` (interface standard), `GameViewables` (composite) | §4.6, [PAT] p.7 |
| `views.swing` | `GameSwing` (téléchargée au TD page 11) + bouton « Ajouter une vue » | §4.7, [PAT] p.9 |
| `views.legacy` | `VueJeu` (API française historique) + `ConsoleView` | [ADP] p.2 |
| `views.adapter` | `GameViewAdapter` : rend `VueJeu` compatible avec `GameViewable` | [ADP] |

Le contrôleur ne dépend **que** de `GameViewable` et d'une `WinnerRule` injectée ;
il n'écrit jamais à l'écran. Le paquet de 52 cartes est construit par le
contrôleur, la vue et la règle lui sont fournies depuis `Main`.

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
