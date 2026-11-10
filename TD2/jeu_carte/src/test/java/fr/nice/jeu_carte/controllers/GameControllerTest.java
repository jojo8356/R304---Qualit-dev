package fr.nice.jeu_carte.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.nice.jeu_carte.controllers.GameController.GameState;
import fr.nice.jeu_carte.models.Deck;
import fr.nice.jeu_carte.models.Player;
import fr.nice.jeu_carte.models.factory.DeckBuilder;
import fr.nice.jeu_carte.models.factory.DeckType;
import fr.nice.jeu_carte.models.factory.GameSetup;
import fr.nice.jeu_carte.models.rules.HighestCardWins;
import fr.nice.jeu_carte.models.rules.WinnerRule;
import fr.nice.jeu_carte.views.RecordingView;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Vérifie la machine à états et le flux complet du contrôleur (TD §3),
 * sans console ni fenêtre grâce à la vue factice {@link RecordingView}.
 */
class GameControllerTest {

    private RecordingView view;
    private GameController controller;

    @BeforeEach
    void setUp() {
        view = new RecordingView();
        controller = new GameController(new DeckBuilder().build(), view);
    }

    @Test
    @DisplayName("Le contrôleur démarre à l'étape 1 avec un paquet complet")
    void etatInitial() {
        assertEquals(GameState.AddingPlayer, controller.getState());
        assertEquals(52, controller.getDeckSize());
        assertEquals(0, controller.getPlayerCount());
        assertFalse(controller.hasPlayers());
        assertTrue(controller.canAddPlayer());
        assertEquals(0, view.getPlayerNamePrompts(),
                "le constructeur n'affiche rien : c'est Main qui lance la première étape");
    }

    @Test
    @DisplayName("La première étape demande un nom de joueur")
    void premiereEtape() {
        controller.runOneStep();
        assertEquals(1, view.getPlayerNamePrompts());
    }

    @Test
    @DisplayName("La vue reçoit son contrôleur : elle ne le crée pas")
    void vueBranchee() {
        assertNotNull(view.getController());
        assertEquals(controller, view.getController());
    }

    @Test
    @DisplayName("Ajouter un joueur met la vue à jour (étape 2)")
    void ajoutJoueur() {
        controller.addPlayer("Alice");

        assertEquals(1, controller.getPlayerCount());
        assertEquals(List.of("0:Alice"), view.getPlayerNames());
        assertEquals(GameState.AddingPlayer, controller.getState());
        assertEquals(1, view.getPlayerNamePrompts(), "la vue redemande un nom");
    }

    @Test
    @DisplayName("Le nom d'un joueur ne peut pas être vide")
    void nomVide() {
        assertThrows(IllegalArgumentException.class, () -> controller.addPlayer("   "));
        assertThrows(IllegalArgumentException.class, () -> controller.addPlayer(null));
        assertEquals(0, controller.getPlayerCount());
    }

    @Test
    @DisplayName("Le jeu est limité à cinq joueurs")
    void limiteCinqJoueurs() {
        for (String nom : List.of("A", "B", "C", "D", "E")) {
            controller.addPlayer(nom);
        }
        assertEquals(GameController.MAX_PLAYERS, controller.getPlayerCount());
        assertFalse(controller.canAddPlayer());
        assertThrows(IllegalStateException.class, () -> controller.addPlayer("F"));
    }

    @Test
    @DisplayName("Distribuer sans joueur est refusé")
    void distributionSansJoueur() {
        assertThrows(IllegalStateException.class, controller::startGame);
        assertEquals(GameState.AddingPlayer, controller.getState());
    }

    @Test
    @DisplayName("La distribution retire une carte par joueur et invite à retourner")
    void distribution() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.startGame();

        assertEquals(GameState.CardsDealt, controller.getState());
        assertEquals(50, controller.getDeckSize());
        assertEquals(2, view.getFaceDownCards().size());
        assertTrue(view.getFaceDownCards().contains("1:Bob"));
        assertEquals(1, view.getFlipPrompts());
    }

    @Test
    @DisplayName("Retourner les cartes affiche les cartes, puis le gagnant")
    void retournementEtGagnant() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.startGame();
        controller.flipCards();

        assertEquals(GameState.WinnerRevealed, controller.getState());
        assertEquals(2, view.getVisibleCards().size());
        assertEquals(1, view.getWinnerShown());
        assertEquals(1, view.getNewGamePrompts());

        Optional<Player> gagnant = controller.getWinner();
        assertTrue(gagnant.isPresent());
        assertEquals(view.getLastWinner(), gagnant.get().getName());
    }

    @Test
    @DisplayName("Sans distribution, on ne peut pas retourner les cartes")
    void retournementInterdit() {
        controller.addPlayer("Alice");
        assertThrows(IllegalStateException.class, controller::flipCards);
        assertThrows(IllegalStateException.class, controller::rebuildDeck);
    }

    @Test
    @DisplayName("Reconstituer le paquet ramène les 52 cartes et l'étape 1")
    void reconstitution() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.addPlayer("Carol");
        controller.startGame();
        controller.flipCards();
        assertEquals(49, controller.getDeckSize());

        controller.rebuildDeck();
        assertEquals(GameState.AddingPlayer, controller.getState());
        assertEquals(52, controller.getDeckSize());
        assertTrue(controller.getWinner().isEmpty());
    }

    @Test
    @DisplayName("Rejouer depuis l'étape 7 récupère les cartes puis redistribue")
    void nouvelleManche() {
        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.startGame();
        controller.flipCards();

        controller.startGame();
        assertEquals(GameState.CardsDealt, controller.getState());
        assertEquals(50, controller.getDeckSize(), "les cartes ont été récupérées avant la nouvelle pioche");
        assertEquals(2, view.getFlipPrompts());
    }

    @Test
    @DisplayName("La règle injectée décide du gagnant (patron Strategy)")
    void regleInjectee() {
        // règle « espionne » : elle désigne toujours le premier joueur
        WinnerRule regleEspionne = joueurs -> Optional.of(joueurs.get(0));

        RecordingView vueLocale = new RecordingView();
        GameController ctrl = new GameController(new Deck(), vueLocale, regleEspionne);
        ctrl.addPlayer("Alice");
        ctrl.addPlayer("Bob");
        ctrl.startGame();
        ctrl.flipCards();

        assertTrue(ctrl.getWinner().isPresent());
        assertEquals("Alice", ctrl.getWinner().get().getName());
        assertEquals("Alice", vueLocale.getLastWinner());
        assertEquals(GameState.WinnerRevealed, ctrl.getState());
    }

    @Test
    @DisplayName("Un paquet de test suffit pour jouer une manche complète")
    void paquetDeTest() {
        GameSetup setup = new DeckBuilder().type(DeckType.TEST).rule(new HighestCardWins()).build();
        RecordingView vueTest = new RecordingView();
        GameController ctrl = new GameController(setup, vueTest);

        ctrl.addPlayer("Alice");
        ctrl.addPlayer("Bob");
        ctrl.startGame();
        assertEquals(2, ctrl.getDeckSize());
        ctrl.flipCards();

        assertEquals(GameState.WinnerRevealed, ctrl.getState());
        assertEquals(1, vueTest.getWinnerShown());
    }

    @Test
    @DisplayName("Ajouter une seconde vue la met immédiatement à jour (patron Observer)")
    void ajoutVue() {
        RecordingView seconde = new RecordingView();
        controller.addView(seconde);

        assertEquals(controller, seconde.getController());
        assertEquals(1, seconde.getPlayerNamePrompts());
        assertThrows(IllegalArgumentException.class, () -> controller.addView(null));
    }

    @Test
    @DisplayName("Les constructeurs refusent les dépendances nulles")
    void dependancesNulles() {
        assertThrows(IllegalArgumentException.class,
                () -> new GameController(new Deck(), null, new HighestCardWins()));
        assertThrows(NullPointerException.class,
                () -> new GameController((GameSetup) null, view));
        assertThrows(NullPointerException.class,
                () -> new GameController((Deck) null, view, new HighestCardWins()));
        assertThrows(NullPointerException.class,
                () -> new GameController(new Deck(), view, null));
    }
}
