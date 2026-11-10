package fr.nice.jeu_carte.controllers;

import fr.nice.jeu_carte.models.Card;
import fr.nice.jeu_carte.models.Hand;

/**
 * player
 */
public class PlayerController {
    private String name;
    private Hand hand;

    public PlayerController(String name, Hand hand) {
        this.name = name;
        this.hand = hand != null ? hand : new Hand();
    }

    public Hand getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }

    public void addCardToHand(Card card) {
        hand.addCard(card);
    }

    public void removeCardFromHand(int index) {
        hand.removeCard(index);
    }
}