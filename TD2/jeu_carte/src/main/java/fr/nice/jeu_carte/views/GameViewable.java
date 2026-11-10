package fr.nice.jeu_carte.views;

import fr.nice.jeu_carte.controllers.GameController;

/**
 * Interface standardisée de la vue (TD §4.6 et [ADP] page 1).
 *
 * <p>Elle traduit trois principes :</p>
 * <ul>
 *   <li><strong>ISP</strong> : interface petite, ciblée, cohérente ;</li>
 *   <li><strong>OCP</strong> : on ajoute une vue (console, Swing, composite…)
 *       sans modifier le contrôleur ;</li>
 *   <li><strong>DIP</strong> : le contrôleur ne dépend que de cette abstraction,
 *       jamais d'une vue concrète.</li>
 * </ul>
 *
 * <p>La vue ne reçoit que des informations d'affichage (indice, nom, libellés
 * de rang et de couleur) : elle ne manipule jamais les objets du modèle.</p>
 */
public interface GameViewable {

    /** Invite l'utilisateur à saisir le nom d'un joueur (état {@code AddingPlayer}). */
    void promptForPlayerName();

    /** Invite l'utilisateur à retourner les cartes (état {@code CardsDealt}). */
    void promptForFlip();

    /** Invite l'utilisateur à rejouer / reconstituer le paquet (état {@code WinnerRevealed}). */
    void promptForNewGame();

    void showPlayerName(int index, String name);

    void showFaceDownCardForPlayer(int index, String name);

    void showCardForPlayer(int index, String name, String rank, String suit);

    void showWinner(String name);

    /** La vue reçoit son contrôleur : elle ne le crée jamais (TD §4.7). */
    void setController(GameController controller);
}
