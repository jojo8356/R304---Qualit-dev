package fr.nice.jeu_carte.models;

public class Card {
    private Rank rank;
    private Color color;
    private boolean isFaceUp;

    public Card(Rank rank, Color color) {
        this.rank = rank;
        this.color = color;
        this.isFaceUp = false;
    }

    public Rank getRank() {
        return rank;
    }

    public Color getColor() {
        return color;
    }

    public int compare(Card other) {
        if (this.rank.ordinal() < other.rank.ordinal()) {
            return -1;
        } else if (this.rank.ordinal() > other.rank.ordinal()) {
            return 1;
        }
        return 0;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public void flip() {
        isFaceUp = !isFaceUp;
    }
}
