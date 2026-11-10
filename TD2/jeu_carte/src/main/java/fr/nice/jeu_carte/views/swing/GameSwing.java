package fr.nice.jeu_carte.views.swing;

import fr.nice.jeu_carte.controllers.GameController;
import fr.nice.jeu_carte.views.GameViewable;
import java.awt.Component;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Vue graphique Swing (TD page 11).
 *
 * <p>Elle implémente l'interface standardisée {@link GameViewable} sans que le
 * contrôleur ait besoin d'être modifié (TD §4.7). Le bouton « Ajouter une vue »
 * illustre le patron Observer / Composite : chaque clic crée une nouvelle vue
 * et l'enregistre auprès du contrôleur ([PAT] pages 7 et 9).</p>
 */
public class GameSwing implements GameViewable {

    private GameController controller;

    private JButton btnAddPlayer;
    private JButton btnDealCards;
    private JButton btnFindWinner;
    private JButton btnNewView;
    private JTextArea textArea;
    private char nextPlayerName = 'A';

    /** Construit et affiche la fenêtre. À appeler sur l'EDT (Swing). */
    public void createAndShowGUI() {
        JFrame frame = new JFrame("MVC - SOLID - Jeu de cartes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        addAddPlayerButton(contentPane);
        addDealCardsButton(contentPane);
        addFindWinnerButton(contentPane);
        addNewViewButton(contentPane);
        addControllerCommandTracker(contentPane);

        frame.setVisible(true);
    }

    // ------------------------------------------------------------------
    // Boutons
    // ------------------------------------------------------------------

    private void addAddPlayerButton(Container contentPane) {
        btnAddPlayer = new JButton("Ajouter un joueur");
        addCenteredComponent(btnAddPlayer, contentPane);
        btnAddPlayer.addActionListener(event -> {
            appendText("Joueurs :");
            executer(() -> controller.addPlayer("Joueur " + nextPlayerName++));
        });
    }

    private void addDealCardsButton(Container contentPane) {
        btnDealCards = new JButton("Distribuer les cartes");
        addCenteredComponent(btnDealCards, contentPane);
        btnDealCards.addActionListener(event -> {
            appendText("Distribution :");
            executer(controller::startGame);
        });
    }

    private void addFindWinnerButton(Container contentPane) {
        btnFindWinner = new JButton("Retourner les cartes");
        addCenteredComponent(btnFindWinner, contentPane);
        btnFindWinner.addActionListener(event -> {
            appendText("Cartes retournées :");
            executer(controller::flipCards);
        });
    }

    /** Nouveau bouton du TD ([PAT] page 9) : crée et enregistre une vue. */
    private void addNewViewButton(Container contentPane) {
        btnNewView = new JButton("Ajouter une vue");
        addCenteredComponent(btnNewView, contentPane);
        btnNewView.addActionListener(event -> {
            GameSwing nouvelleVue = new GameSwing();
            nouvelleVue.createAndShowGUI();
            executer(() -> controller.addView(nouvelleVue));
        });
    }

    private void addControllerCommandTracker(Container contentPane) {
        textArea = new JTextArea("État du jeu\n", 100, 1);
        JScrollPane scrollPane = new JScrollPane(textArea);
        addCenteredComponent(scrollPane, contentPane);
        textArea.setSize(500, 500);
    }

    // ------------------------------------------------------------------
    // GameViewable
    // ------------------------------------------------------------------

    @Override
    public void setController(GameController controller) {
        this.controller = controller;
    }

    @Override
    public void promptForPlayerName() {
        appendText("Saisissez les joueurs avec le bouton « Ajouter un joueur ».");
        setButtons(controller.canAddPlayer(), controller.hasPlayers(), false);
    }

    @Override
    public void promptForFlip() {
        appendText("Cliquez sur « Retourner les cartes ».");
        setButtons(false, false, true);
    }

    @Override
    public void promptForNewGame() {
        appendText("Cliquez sur « Distribuer les cartes » pour rejouer.");
        setButtons(false, true, false);
    }

    @Override
    public void showPlayerName(int index, String name) {
        appendText("[" + index + "][" + name + "]");
    }

    @Override
    public void showFaceDownCardForPlayer(int index, String name) {
        appendText("[" + name + "][carte face cachée]");
    }

    @Override
    public void showCardForPlayer(int index, String name, String rank, String suit) {
        appendText("[" + name + "][" + rank + " de " + suit + "]");
    }

    @Override
    public void showWinner(String name) {
        appendText("Gagnant : " + name);
    }

    // ------------------------------------------------------------------
    // Outils internes
    // ------------------------------------------------------------------

    /** Exécute une action du contrôleur en affichant proprement un refus. */
    private void executer(Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            appendText("Refusé : " + e.getMessage());
        }
    }

    private void setButtons(boolean ajouterJoueur, boolean distribuer, boolean retourner) {
        if (btnAddPlayer != null) {
            btnAddPlayer.setEnabled(ajouterJoueur);
        }
        if (btnDealCards != null) {
            btnDealCards.setEnabled(distribuer);
        }
        if (btnFindWinner != null) {
            btnFindWinner.setEnabled(retourner);
        }
    }

    private void appendText(String text) {
        if (textArea == null) {
            System.out.println(text);
            return;
        }
        textArea.append(text + "\n");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    private void addCenteredComponent(JComponent component, Container contentPane) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(component);
    }
}
