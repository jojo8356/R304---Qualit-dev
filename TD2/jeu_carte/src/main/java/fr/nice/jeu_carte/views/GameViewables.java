package fr.nice.jeu_carte.views;

import fr.nice.jeu_carte.controllers.GameController;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Composite de vues (patron Composite, [PAT] page 7).
 *
 * <p>Le contrôleur traite une seule vue ou plusieurs vues exactement de la même
 * façon : chaque demande d'affichage est simplement retransmise à toutes les
 * vues enregistrées. Aucune signature ne change.</p>
 *
 * <p>La liste est une {@link CopyOnWriteArrayList} : elle peut être parcourue
 * par un thread pendant qu'une vue Swing en ajoute une autre, sans
 * {@code ConcurrentModificationException} ([PAT] pages 10-11).</p>
 */
public class GameViewables implements GameViewable {

    private final List<GameViewable> views;
    private volatile GameController controller;

    public GameViewables() {
        this.views = new CopyOnWriteArrayList<>();
    }

    /**
     * Enregistre une nouvelle vue. Si le contrôleur est déjà connu, la vue le
     * reçoit immédiatement (elle peut alors déclencher des actions).
     */
    public void add(GameViewable view) {
        if (view == null) {
            throw new IllegalArgumentException("La vue ne peut pas être nulle.");
        }
        GameController currentController = this.controller;
        if (currentController != null) {
            view.setController(currentController);
        }
        views.add(view);
    }

    public int size() {
        return views.size();
    }

    @Override
    public void setController(GameController controller) {
        this.controller = controller;
        for (GameViewable view : views) {
            view.setController(controller);
        }
    }

    @Override
    public void promptForPlayerName() {
        for (GameViewable view : views) {
            view.promptForPlayerName();
        }
    }

    @Override
    public void promptForFlip() {
        for (GameViewable view : views) {
            view.promptForFlip();
        }
    }

    @Override
    public void promptForNewGame() {
        for (GameViewable view : views) {
            view.promptForNewGame();
        }
    }

    @Override
    public void showPlayerName(int index, String name) {
        for (GameViewable view : views) {
            view.showPlayerName(index, name);
        }
    }

    @Override
    public void showFaceDownCardForPlayer(int index, String name) {
        for (GameViewable view : views) {
            view.showFaceDownCardForPlayer(index, name);
        }
    }

    @Override
    public void showCardForPlayer(int index, String name, String rank, String suit) {
        for (GameViewable view : views) {
            view.showCardForPlayer(index, name, rank, suit);
        }
    }

    @Override
    public void showWinner(String name) {
        for (GameViewable view : views) {
            view.showWinner(name);
        }
    }
}
