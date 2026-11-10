package fr.nice.jeu_carte.models.rules;

import fr.nice.jeu_carte.models.Card;
import fr.nice.jeu_carte.models.Player;
import java.util.List;
import java.util.Optional;

/**
 * Deuxième règle possible : la carte la plus basse gagne.
 *
 * <p>Elle existe pour illustrer le principe Open/Closed (TD §4.3-4.4) :
 * ajouter une règle revient à ajouter une classe, sans modifier le contrôleur.</p>
 */
public class LowestCardWins implements WinnerRule {

    @Override
    public Optional<Player> evaluate(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return Optional.empty();
        }
        Player worst = null;
        Card worstCard = null;
        for (Player player : players) {
            Card card = requireCardOf(player);
            if (worst == null || card.compareTo(worstCard) < 0) {
                worst = player;
                worstCard = card;
            }
        }
        return Optional.ofNullable(worst);
    }

    private Card requireCardOf(Player player) {
        if (player.getHand().isEmpty()) {
            throw new IllegalStateException(
                    "Le joueur " + player.getName() + " n'a aucune carte : impossible de désigner un gagnant.");
        }
        return player.getHand().getCard(0);
    }
}
