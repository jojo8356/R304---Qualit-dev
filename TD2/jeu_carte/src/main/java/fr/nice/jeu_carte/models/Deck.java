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

    /** Taille du jeu standard (4 couleurs × 13 rangs). */
    public static final int STANDARD_DECK_SIZE = 52;

    /** Taille d'un petit jeu (du 7 à l'As : 8 rangs × 4 couleurs). */
    public static final int SMALL_DECK_SIZE = 32;

    private final List<Card> cards;

    /** Construit le jeu standard de 52 cartes (4 couleurs × 13 rangs). */
    public Deck() {
        this(STANDARD_DECK_SIZE);
    }

    /**
     * Construit un paquet de 52 cartes, ou de 32 cartes (du 7 à l'As).
     *
     * @param size {@value #STANDARD_DECK_SIZE} ou {@value #SMALL_DECK_SIZE}
     * @throws IllegalArgumentException pour toute autre taille
     */
    public Deck(int size) {
        if (size != STANDARD_DECK_SIZE && size != SMALL_DECK_SIZE) {
            throw new IllegalArgumentException(
                    "Taille de paquet non supportée : " + size + " (52 ou 32 attendu).");
        }
        this.cards = new ArrayList<>();
        for (Color color : Color.values()) {
            for (Rank rank : Rank.values()) {
                if (size == STANDARD_DECK_SIZE || rank.getStrength() >= Rank.SEVEN.getStrength()) {
                    cards.add(new Card(rank, color));
                }
            }
        }
    }

    /**
     * Construit un paquet à partir d'une liste de cartes : utilisé par la
     * factory pour les paquets de test (deuxième boucle du TD §2.3 remplacée
     * par une liste fournie).
     *
     * @throws IllegalArgumentException si la liste est nulle ou vide
     */
    public Deck(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            throw new IllegalArgumentException("Un paquet ne peut pas être vide.");
        }
        this.cards = new ArrayList<>(cards.size());
        for (Card card : cards) {
            if (card == null) {
                throw new IllegalArgumentException("Un paquet ne peut pas contenir de carte nulle.");
            }
            this.cards.add(card);
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
