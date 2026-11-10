package fr.nice.jeu_carte.models;

import java.util.Objects;

/**
 * Une carte à jouer (TD §2.2).
 *
 * <p>Le rang et la couleur ne changent jamais : ce sont des attributs
 * <strong>immuables</strong> ({@code final}, aucun setter). Seul l'état
 * « face visible / face cachée » varie pendant la partie.</p>
 */
public class Card implements Comparable<Card> {

    private final Rank rank;
    private final Color color;
    private boolean faceUp;

    public Card(Rank rank, Color color) {
        this.rank = Objects.requireNonNull(rank, "rank");
        this.color = Objects.requireNonNull(color, "color");
        this.faceUp = false;
    }

    public Rank getRank() {
        return rank;
    }

    public Color getColor() {
        return color;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    /** Retourne la carte (face cachée &lt;-&gt; face visible). */
    public void flip() {
        this.faceUp = !this.faceUp;
    }

    /** Remet la carte face cachée (utilisé quand elle retourne dans le paquet). */
    public void faceDown() {
        this.faceUp = false;
    }

    /**
     * Compare deux cartes : le rang d'abord, la couleur en cas d'égalité
     * (Trèfles &gt; Piques &gt; Cœurs &gt; Carreaux).
     *
     * @return un entier négatif si {@code this} est plus faible, positif si elle
     *         est plus forte, {@code 0} si les deux cartes sont identiques
     */
    @Override
    public int compareTo(Card other) {
        Objects.requireNonNull(other, "other");
        int byRank = Integer.compare(this.rank.getStrength(), other.rank.getStrength());
        if (byRank != 0) {
            return byRank;
        }
        return Integer.compare(this.color.getStrength(), other.color.getStrength());
    }

    /** Libellé affichable, par exemple {@code "As de Trèfles"}. */
    public String label() {
        return rank.getLabel() + " de " + color.getLabel();
    }

    @Override
    public String toString() {
        return label();
    }
}
