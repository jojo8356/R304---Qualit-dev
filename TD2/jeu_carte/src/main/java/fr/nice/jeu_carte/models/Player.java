package fr.nice.jeu_carte.models;

/**
 * Un joueur (TD §2.4) : un nom indispensable et une main ({@link Hand}).
 *
 * <p>Le joueur ne connaît ni les règles du jeu ni l'affichage : il présente sa
 * main, y ajoute une carte et en retire une. Si les règles changent (nombre de
 * cartes, façon de les manipuler), seule {@code Hand} est concernée.</p>
 */
public class Player {

    private final String name;
    private final Hand hand;

    public Player(String name) {
        this(name, new Hand());
    }

    public Player(String name, Hand hand) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du joueur ne peut pas être vide.");
        }
        this.name = name.trim();
        this.hand = (hand != null) ? hand : new Hand();
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public void addCardToHand(Card card) {
        hand.addCard(card);
    }

    public Card removeCardFromHand(int index) {
        return hand.removeCard(index);
    }

    /** Présente le joueur et sa main, par exemple {@code "Alice : As de Trèfles"}. */
    public String presentHand() {
        return name + " : " + hand;
    }

    @Override
    public String toString() {
        return name;
    }
}
