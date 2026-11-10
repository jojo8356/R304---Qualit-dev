package fr.nice.jeu_carte.models.factory;

import fr.nice.jeu_carte.models.Deck;
import fr.nice.jeu_carte.models.rules.WinnerRule;
import java.util.Objects;

/**
 * Configuration d'une partie produite par le {@link DeckBuilder} :
 * le paquet à utiliser et la règle de victoire ([PAT] page 6).
 */
public final class GameSetup {

    private final Deck deck;
    private final WinnerRule rule;

    public GameSetup(Deck deck, WinnerRule rule) {
        this.deck = Objects.requireNonNull(deck, "deck");
        this.rule = Objects.requireNonNull(rule, "rule");
    }

    public Deck getDeck() {
        return deck;
    }

    public WinnerRule getRule() {
        return rule;
    }

    @Override
    public String toString() {
        return "GameSetup{paquet=" + deck.size() + " cartes, règle=" + rule + "}";
    }
}
