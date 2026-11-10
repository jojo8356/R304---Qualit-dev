package fr.nice.jeu_carte.models;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
        initializeDeck();
        shuffle();
    }

    private void initializeDeck() {
        for (final Color color : Color.values()) {
            for (final Rank rank : Rank.values()) {
                final Card card = new Card(rank, color);
                cards.add(card);
            }
        }
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("No more cards in the deck.");
        }
        return cards.remove(0);
    }

    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        cards.add(card);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public int getLength() {
        return cards.size();
    }
}
