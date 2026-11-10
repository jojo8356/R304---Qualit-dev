package fr.nice.jeu_carte.models.rules;

import fr.nice.jeu_carte.models.Card;
import fr.nice.jeu_carte.models.Player;
import java.util.List;
import java.util.Optional;

/**
 * Règle par défaut : la carte la mieux classée gagne
 * (As &gt; Roi &gt; ... &gt; 2, puis Trèfles &gt; Piques &gt; Cœurs &gt; Carreaux).
 */
public class HighestCardWins implements WinnerRule {

    @Override
    public Optional<Player> evaluate(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return Optional.empty();
        }
        Player best = null;
        Card bestCard = null;
        for (Player player : players) {
            Card card = requireCardOf(player);
            if (best == null || card.compareTo(bestCard) > 0) {
                best = player;
                bestCard = card;
            }
        }
        return Optional.ofNullable(best);
    }

    private Card requireCardOf(Player player) {
        if (player.getHand().isEmpty()) {
            throw new IllegalStateException(
                    "Le joueur " + player.getName() + " n'a aucune carte : impossible de désigner un gagnant.");
        }
        return player.getHand().getCard(0);
    }
}
