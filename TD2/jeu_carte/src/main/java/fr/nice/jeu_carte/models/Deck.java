package fr.nice.jeu_carte.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Le tas (paquet) de cartes (TD §2.3).
 *
 * <p>Une {@link ArrayList} convient parfaitement : l'ordre des cartes compte.
 * Le paquet est rempli par deux boucles sur les couleurs et les rangs, puis
 * peut être mélangé, vidé d'une carte ou complété à nouveau.</p>
 */
public class Deck {

    private final List<Card> cards;

    /** Construit le jeu standard de 52 cartes (4 couleurs × 13 rangs). */
    public Deck() {
        this.cards = new ArrayList<>();
        for (Color color : Color.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, color));
            }
        }
    }

    /** Mélange le paquet (Fisher-Yates, via {@link Collections#shuffle}). */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Retire et renvoie la première carte du paquet.
     *
     * @throws IllegalStateException si le paquet est vide
     */
    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Le paquet est vide : aucune carte à distribuer.");
        }
        return cards.remove(0);
    }

    /**
     * Ajoute une carte au paquet (elle y retourne face cachée).
     *
     * @throws IllegalArgumentException si la carte est {@code null}
     */
    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Impossible d'ajouter une carte nulle au paquet.");
        }
        card.faceDown();
        cards.add(card);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
