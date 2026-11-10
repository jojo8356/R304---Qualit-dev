package fr.nice.jeu_carte.views;

import fr.nice.jeu_carte.controllers.GameController;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue factice utilisée par les tests : elle enregistre les appels reçus au lieu
 * d'afficher quoi que ce soit.
 *
 * <p>C'est l'intérêt de l'interface {@link GameViewable} : le contrôleur se
 * teste sans console, sans fenêtre et sans interaction humaine.</p>
 */
public class RecordingView implements GameViewable {

    private GameController controller;

    private int playerNamePrompts;
    private int flipPrompts;
    private int newGamePrompts;
    private int winnerShown;

    private final List<String> playerNames = new ArrayList<>();
    private final List<String> faceDownCards = new ArrayList<>();
    private final List<String> visibleCards = new ArrayList<>();
    private String lastWinner;

    @Override
    public void promptForPlayerName() {
        playerNamePrompts++;
    }

    @Override
    public void promptForFlip() {
        flipPrompts++;
    }

    @Override
    public void promptForNewGame() {
        newGamePrompts++;
    }

    @Override
    public void showPlayerName(int index, String name) {
        playerNames.add(index + ":" + name);
    }

    @Override
    public void showFaceDownCardForPlayer(int index, String name) {
        faceDownCards.add(index + ":" + name);
    }

    @Override
    public void showCardForPlayer(int index, String name, String rank, String suit) {
        visibleCards.add(name + "=" + rank + " de " + suit);
    }

    @Override
    public void showWinner(String name) {
        winnerShown++;
        lastWinner = name;
    }

    @Override
    public void setController(GameController controller) {
        this.controller = controller;
    }

    public GameController getController() {
        return controller;
    }

    public int getPlayerNamePrompts() {
        return playerNamePrompts;
    }

    public int getFlipPrompts() {
        return flipPrompts;
    }

    public int getNewGamePrompts() {
        return newGamePrompts;
    }

    public int getWinnerShown() {
        return winnerShown;
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public List<String> getFaceDownCards() {
        return faceDownCards;
    }

    public List<String> getVisibleCards() {
        return visibleCards;
    }

    public String getLastWinner() {
        return lastWinner;
    }
}
