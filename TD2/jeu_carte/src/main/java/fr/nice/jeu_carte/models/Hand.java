package fr.nice.jeu_carte.models;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private ArrayList<Card> cards;
    private int size;

    public Hand() {
        this.cards = new ArrayList<>();
        this.size = 0;
    }

    public void addCard(Card card) {
        cards.add(card);
        size++;
    }

    public List<Card> getCards() {
        return cards;
    }

    public int getSize() {
        return size;
    }

    public Card getCard(int index) {
        if (index >= 0 && index < size) {
            return cards.get(index);
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }
    }

    public void removeCard(int index) {
        if (index >= 0 && index < size) {
            cards.remove(index);
            size--;
        } else {
            throw new IndexOutOfBoundsException("Index out of bounds.");
        }
    }
}
