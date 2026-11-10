package fr.nice.jeu_carte.models.factory;

import fr.nice.jeu_carte.models.Deck;

/**
 * Jeu standard de 52 cartes ([PAT] page 3, « NormalDeck »).
 *
 * <p>La sous-classe ne change que la taille : elle existe pour que la factory
 * renvoie des types concrets distincts et que l'appelant n'ait jamais à
 * connaître les paramètres de construction.</p>
 */
public class NormalDeck extends Deck {

    public NormalDeck() {
        super(Deck.STANDARD_DECK_SIZE);
    }
}
