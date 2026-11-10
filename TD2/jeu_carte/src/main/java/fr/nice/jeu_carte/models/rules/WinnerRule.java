package fr.nice.jeu_carte.models.rules;

import fr.nice.jeu_carte.models.Player;
import java.util.List;
import java.util.Optional;

/**
 * Règle de désignation du gagnant (TD §4.4 : le patron Strategy).
 *
 * <p>Chaque règle est une implémentation distincte. Le contrôleur ne sait pas
 * quelle règle est utilisée : il travaille toujours avec cette interface, qui
 * lui est fournie depuis l'extérieur (injection de dépendance, TD §4.5).</p>
 */
public interface WinnerRule {

    /**
     * Détermine le gagnant parmi les joueurs.
     *
     * @param players joueurs ayant chacun au moins une carte
     * @return le gagnant, ou {@link Optional#empty()} si la liste est vide
     */
    Optional<Player> evaluate(List<Player> players);
}
