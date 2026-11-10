package fr.nice.jeu_carte.controllers;

import fr.nice.jeu_carte.models.Card;
import fr.nice.jeu_carte.models.Deck;
import fr.nice.jeu_carte.models.Player;
import fr.nice.jeu_carte.models.factory.GameSetup;
import fr.nice.jeu_carte.models.rules.WinnerRule;
import fr.nice.jeu_carte.views.GameViewable;
import fr.nice.jeu_carte.views.GameViewables;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Contrôleur du jeu (TD §3) : il séquence les interactions entre l'utilisateur
 * et le jeu, sans jamais afficher quoi que ce soit lui-même.
 *
 * <ul>
 *   <li>il reçoit le paquet (construit par la {@code DeckFactory}) et part d'une
 *       liste de joueurs vide (TD §3.1, [PAT] pages 3-6) ;</li>
 *   <li>il ne dépend que de l'abstraction {@link GameViewable} et d'une règle
 *       injectée {@link WinnerRule} (inversion de dépendance, TD §4.5-4.7) ;</li>
 *   <li>il connaît l'étape courante ({@link GameState}) et refuse les entrées
 *       qui n'ont pas de sens à cette étape (TD §3.6).</li>
 * </ul>
 *
 * <p>Chaque action met à jour le modèle puis appelle {@link #runOneStep()} :
 * la machine à états n'est plus pilotée par une boucle infinie, mais par les
 * événements de la vue (TD, pages 11-12).</p>
 */
public class GameController {

    /** Nombre maximal de joueurs (TD §1 : « limitez le nombre de joueurs à cinq »). */
    public static final int MAX_PLAYERS = 5;

    /** Les étapes du jeu (TD §3.6). */
    public enum GameState {
        /** Saisie des noms : on peut ajouter des joueurs. */
        AddingPlayer,
        /** Les cartes sont distribuées face cachée : on peut les retourner. */
        CardsDealt,
        /** Le gagnant est affiché : on peut reconstituer le paquet. */
        WinnerRevealed
    }

    private final Deck deck;
    private final GameViewables views;
    private final List<Player> players;
    private final WinnerRule rule;

    private GameState state;
    private Optional<Player> winner;

    /**
     * Construit le contrôleur à partir d'une configuration produite par le
     * {@link fr.nice.jeu_carte.models.factory.DeckBuilder} ([PAT] pages 5-6).
     */
    public GameController(GameSetup setup, GameViewable view) {
        this(Objects.requireNonNull(setup, "setup").getDeck(), view, setup.getRule());
    }

    public GameController(Deck deck, GameViewable view, WinnerRule rule) {
        this.deck = Objects.requireNonNull(deck, "deck");
        this.rule = Objects.requireNonNull(rule, "rule");
        this.players = new ArrayList<>();
        this.state = GameState.AddingPlayer;
        this.winner = Optional.empty();
        this.views = asComposite(view);
        this.views.setController(this);
    }

    /** Une vue seule est enveloppée dans le composite : le contrôleur n'en voit qu'une. */
    private static GameViewables asComposite(GameViewable view) {
        if (view == null) {
            throw new IllegalArgumentException("La vue ne peut pas être nulle.");
        }
        if (view instanceof GameViewables) {
            return (GameViewables) view;
        }
        GameViewables composite = new GameViewables();
        composite.add(view);
        return composite;
    }

    // ------------------------------------------------------------------
    // Étape 2 du TD : saisie des noms des joueurs
    // ------------------------------------------------------------------

    /**
     * Ajoute un joueur (TD §3.2).
     *
     * @throws IllegalStateException    si la partie a déjà commencé ou si le
     *                                  maximum de joueurs est atteint
     * @throws IllegalArgumentException si le nom est vide
     */
    public void addPlayer(String name) {
        requireState(GameState.AddingPlayer);
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du joueur ne peut pas être vide.");
        }
        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalStateException("Le jeu est limité à " + MAX_PLAYERS + " joueurs.");
        }
        Player player = new Player(name);
        players.add(player);
        views.showPlayerName(players.size() - 1, player.getName());
        runOneStep();
    }

    // ------------------------------------------------------------------
    // Étapes 3 et 4 du TD : mélanger puis distribuer
    // ------------------------------------------------------------------

    /**
     * Mélange le paquet et distribue une carte face cachée à chaque joueur.
     *
     * <p>Appelée depuis l'état {@code WinnerRevealed}, elle récupère d'abord les
     * cartes des mains (étape 8 : reconstituer le paquet), ce qui permet de
     * rejouer une manche avec les mêmes joueurs.</p>
     */
    public void startGame() {
        if (state != GameState.AddingPlayer && state != GameState.WinnerRevealed) {
            throw new IllegalStateException("Impossible de distribuer depuis l'état " + state + ".");
        }
        if (state == GameState.WinnerRevealed) {
            recoverCards();
        }
        if (players.isEmpty()) {
            throw new IllegalStateException("Ajoutez au moins un joueur avant de distribuer.");
        }
        if (deck.size() < players.size()) {
            throw new IllegalStateException("Pas assez de cartes dans le paquet pour distribuer.");
        }
        deck.shuffle();
        for (Player player : players) {
            player.addCardToHand(deck.drawCard());
        }
        for (int index = 0; index < players.size(); index++) {
            views.showFaceDownCardForPlayer(index, players.get(index).getName());
        }
        state = GameState.CardsDealt;
        runOneStep();
    }

    // ------------------------------------------------------------------
    // Étapes 5, 6 et 7 du TD : retourner, évaluer, afficher
    // ------------------------------------------------------------------

    /**
     * Retourne les cartes, désigne le gagnant et l'affiche (TD §3.3).
     *
     * @throws IllegalStateException si les cartes n'ont pas été distribuées
     */
    public void flipCards() {
        requireState(GameState.CardsDealt);
        for (int index = 0; index < players.size(); index++) {
            Player player = players.get(index);
            for (Card card : player.getHand().cards()) {
                card.flip();
            }
            Card visibleCard = player.getHand().getCard(0);
            views.showCardForPlayer(index, player.getName(),
                    visibleCard.getRank().getLabel(), visibleCard.getColor().getLabel());
        }
        winner = evaluateWinner();
        state = GameState.WinnerRevealed;
        if (winner.isPresent()) {
            views.showWinner(winner.get().getName());
        }
        runOneStep();
    }

    /**
     * Évalue le gagnant en déléguant à la règle injectée (TD §4.2 : le contrôleur
     * ne décide plus du vainqueur, il demande le résultat).
     */
    public Optional<Player> evaluateWinner() {
        return rule.evaluate(players);
    }

    // ------------------------------------------------------------------
    // Étape 8 du TD : reconstituer le paquet
    // ------------------------------------------------------------------

    /**
     * Reprend toutes les cartes des mains et les remet dans le paquet, puis
     * revient à la phase de saisie : le paquet est complet et prêt à être
     * mélangé pour une nouvelle manche.
     */
    public void rebuildDeck() {
        requireState(GameState.WinnerRevealed);
        recoverCards();
        state = GameState.AddingPlayer;
        runOneStep();
    }

    private void recoverCards() {
        for (Player player : players) {
            while (!player.getHand().isEmpty()) {
                deck.addCard(player.removeCardFromHand(0));
            }
        }
        winner = Optional.empty();
    }

    // ------------------------------------------------------------------
    // Machine à états (TD §3.6) et gestion des vues (TD §4.6-4.7, [PAT] p.7-9)
    // ------------------------------------------------------------------

    /** Enregistre une nouvelle vue et la met immédiatement à jour ([PAT] p.9). */
    public void addView(GameViewable newView) {
        if (newView == null) {
            throw new IllegalArgumentException("La vue ne peut pas être nulle.");
        }
        views.add(newView);
        runOneStep();
    }

    /**
     * Exécute une seule étape de la machine à états : demande à toutes les vues
     * d'afficher la consigne correspondant à l'état courant (TD, pages 11-12).
     */
    public void runOneStep() {
        switch (state) {
            case AddingPlayer:
                views.promptForPlayerName();
                break;
            case CardsDealt:
                views.promptForFlip();
                break;
            case WinnerRevealed:
                views.promptForNewGame();
                break;
            default:
                break;
        }
    }

    // ------------------------------------------------------------------
    // Lecture de l'état (utilisée par les vues et les tests)
    // ------------------------------------------------------------------

    public GameState getState() {
        return state;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean hasPlayers() {
        return !players.isEmpty();
    }

    /** Vrai si un joueur peut encore être ajouté à cette étape. */
    public boolean canAddPlayer() {
        return state == GameState.AddingPlayer && players.size() < MAX_PLAYERS;
    }

    public int getDeckSize() {
        return deck.size();
    }

    /** Gagnant de la dernière manche, s'il a déjà été désigné. */
    public Optional<Player> getWinner() {
        return winner;
    }

    private void requireState(GameState expected) {
        if (state != expected) {
            throw new IllegalStateException(
                    "Action impossible dans l'état " + state + " (état attendu : " + expected + ").");
        }
    }
}
