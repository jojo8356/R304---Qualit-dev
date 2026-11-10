package fr.nice.jeu_carte.models.factory;

import fr.nice.jeu_carte.models.Deck;
import fr.nice.jeu_carte.models.rules.WinnerRule;
import java.util.Objects;

/**
 * Configuration d'une partie produite par le {@link DeckBuilder} :
 * le paquet à utiliser et la règle de victoire ([PAT] page 6).
 *
 * <p>C'est un {@code record} (Java 16+) : deux champs immuables, un
 * constructeur qui valide, et {@code deck()} / {@code rule()} générés. Trente
 * lignes de code de plomberie deviennent une déclaration.</p>
 */
public record GameSetup(Deck deck, WinnerRule rule) {

    public GameSetup {
        Objects.requireNonNull(deck, "deck");
        Objects.requireNonNull(rule, "rule");
    }

    @Override
    public String toString() {
        return "GameSetup{paquet=" + deck.size() + " cartes, règle=" + rule + "}";
    }
}
