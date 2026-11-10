package fr.nice.jeu_carte.models.factory;

import fr.nice.jeu_carte.models.Deck;

/**
 * Petit jeu de 32 cartes, du 7 à l'As ([PAT] page 3, « SmallDeck »).
 */
public class SmallDeck extends Deck {

    public SmallDeck() {
        super(Deck.SMALL_DECK_SIZE);
    }
}
