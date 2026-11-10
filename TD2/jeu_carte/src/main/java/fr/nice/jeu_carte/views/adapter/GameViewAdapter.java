package fr.nice.jeu_carte.views.adapter;

import fr.nice.jeu_carte.controllers.GameController;
import fr.nice.jeu_carte.views.GameViewable;
import fr.nice.jeu_carte.views.legacy.VueJeu;

/**
 * Adaptateur (patron Adapter, [ADP]) : il rend une vue écrite avec l'API
 * française {@link VueJeu} utilisable là où le contrôleur attend l'interface
 * standardisée {@link GameViewable}.
 *
 * <p>Il n'y a aucun renommage de méthode et aucune modification du contrôleur :
 * chaque appel de l'interface standard est simplement redirigé vers son
 * équivalent français.</p>
 *
 * <table border="1">
 *   <caption>Correspondances</caption>
 *   <tr><th>GameViewable (standard)</th><th>VueJeu (historique)</th></tr>
 *   <tr><td>promptForPlayerName()</td><td>demandeNomJoueur()</td></tr>
 *   <tr><td>promptForFlip()</td><td>demandeRetournerCarte()</td></tr>
 *   <tr><td>promptForNewGame()</td><td>demandeNouveauJeu()</td></tr>
 *   <tr><td>showPlayerName(i, n)</td><td>montreNomJoueur(i, n)</td></tr>
 *   <tr><td>showFaceDownCardForPlayer(i, n)</td><td>montreFaceCacheJoueur(i, n)</td></tr>
 *   <tr><td>showCardForPlayer(i, n, r, c)</td><td>montreCarteJoueur(i, n, r, c)</td></tr>
 *   <tr><td>showWinner(n)</td><td>montreGagnant(n)</td></tr>
 * </table>
 */
public class GameViewAdapter implements GameViewable {

    private final VueJeu target;

    public GameViewAdapter(VueJeu target) {
        if (target == null) {
            throw new IllegalArgumentException("La vue à adapter ne peut pas être nulle.");
        }
        this.target = target;
    }

    @Override
    public void promptForPlayerName() {
        target.demandeNomJoueur();
    }

    @Override
    public void promptForFlip() {
        target.demandeRetournerCarte();
    }

    @Override
    public void promptForNewGame() {
        target.demandeNouveauJeu();
    }

    @Override
    public void showPlayerName(int index, String name) {
        target.montreNomJoueur(index, name);
    }

    @Override
    public void showFaceDownCardForPlayer(int index, String name) {
        target.montreFaceCacheJoueur(index, name);
    }

    @Override
    public void showCardForPlayer(int index, String name, String rank, String suit) {
        target.montreCarteJoueur(index, name, rank, suit);
    }

    @Override
    public void showWinner(String name) {
        target.montreGagnant(name);
    }

    @Override
    public void setController(GameController controller) {
        target.setController(controller);
    }
}
