package fr.nice.jeu_carte.models.factory;

import fr.nice.jeu_carte.models.Deck;

/**
 * Les types de paquets que la {@link DeckFactory} sait produire
 * ([PAT] pages 3-4).
 */
public enum DeckType {

    /** Jeu standard de 52 cartes. */
    NORMAL_52("52 cartes", Deck.STANDARD_DECK_SIZE),

    /** Petit jeu : du 7 à l'As (32 cartes). */
    SMALL_32("32 cartes (du 7 à l'As)", Deck.SMALL_DECK_SIZE),

    /** Paquet de test : un 10 par couleur, pour vérifier l'égalité par la couleur. */
    TEST("4 cartes (un 10 par couleur)", 4);

    private final String label;
    private final int size;

    DeckType(String label, int size) {
        this.label = label;
        this.size = size;
    }

    public String getLabel() {
        return label;
    }

    /** Nombre de cartes du paquet produit. */
    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return label;
    }
}
