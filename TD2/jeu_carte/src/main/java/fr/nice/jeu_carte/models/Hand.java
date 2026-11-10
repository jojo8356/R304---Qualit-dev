package fr.nice.jeu_carte.models;

import java.util.ArrayList;
import java.util.List;

/**
 * La main d'un joueur (TD §2.4) : une collection de cartes, vide au départ.
 *
 * <p>Responsabilité unique : manipuler les cartes d'un joueur (voir le contenu,
 * ajouter, retirer). Le joueur s'occupe de son identité, la main s'occupe des
 * cartes.</p>
 */
public class Hand {

    private final List<Card> cards;

    public Hand() {
        this.cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Impossible d'ajouter une carte nulle à une main.");
        }
        cards.add(card);
    }

    /**
     * Retire et renvoie la carte située à l'indice donné.
     *
     * @throws IndexOutOfBoundsException si l'indice est invalide
     */
    public Card removeCard(int index) {
        checkIndex(index);
        return cards.remove(index);
    }

    /**
     * Renvoie la carte située à l'indice donné, sans la retirer.
     *
     * @throws IndexOutOfBoundsException si l'indice est invalide
     */
    public Card getCard(int index) {
        checkIndex(index);
        return cards.get(index);
    }

    /** Vue non modifiable du contenu de la main. */
    public List<Card> cards() {
        return List.copyOf(cards);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException(
                    "Indice " + index + " invalide pour une main de " + cards.size() + " carte(s).");
        }
    }

    /** Présente la main sous forme lisible, par exemple {@code "As de Trèfles"}. */
    @Override
    public String toString() {
        if (cards.isEmpty()) {
            return "(main vide)";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(cards.get(i).label());
        }
        return builder.toString();
    }
}
