package fr.nice.jeu_carte.models.factory;

import fr.nice.jeu_carte.models.Deck;
import fr.nice.jeu_carte.models.rules.HighestCardWins;
import fr.nice.jeu_carte.models.rules.WinnerRule;
import java.util.Objects;

/**
 * Monte une configuration de partie pièce par pièce (patron Builder,
 * [PAT] pages 5-6) : taille du paquet puis règle de victoire.
 *
 * <pre>{@code
 * GameSetup setup = new DeckBuilder()
 *         .size(Deck.SMALL_DECK_SIZE)
 *         .rule(new LowestCardWins())
 *         .build();
 * }</pre>
 *
 * <p>Le builder assemble les mêmes parties à chaque fois ; seules les variantes
 * changent, et c'est lui qui choisit la bonne implémentation de la règle.</p>
 */
public class DeckBuilder {

    private DeckType type;
    private WinnerRule rule;

    public DeckBuilder() {
        this.type = DeckType.NORMAL_52;
        this.rule = new HighestCardWins();
    }

    /** Choisit un type de paquet (52, 32 ou paquet de test). */
    public DeckBuilder type(DeckType type) {
        this.type = Objects.requireNonNull(type, "type");
        return this;
    }

    /**
     * Choisit la taille du paquet.
     *
     * @param cards 52 ou 32
     * @throws IllegalArgumentException pour toute autre taille
     */
    public DeckBuilder size(int cards) {
        if (cards == Deck.STANDARD_DECK_SIZE) {
            return type(DeckType.NORMAL_52);
        }
        if (cards == Deck.SMALL_DECK_SIZE) {
            return type(DeckType.SMALL_32);
        }
        throw new IllegalArgumentException(
                "Taille de paquet non supportée : " + cards + " (52 ou 32 attendu).");
    }

    /** Choisit la règle de victoire (carte haute par défaut). */
    public DeckBuilder rule(WinnerRule rule) {
        this.rule = Objects.requireNonNull(rule, "rule");
        return this;
    }

    /** Produit le paquet configuré et la règle choisie. */
    public GameSetup build() {
        Deck deck = new DeckFactory(type).makeDeck();
        return new GameSetup(deck, rule);
    }
}
