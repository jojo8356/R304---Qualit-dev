package fr.nice.jeu_carte.models;

/**
 * Couleur d'une carte (TD §2.1).
 *
 * <p>Ordre imposé par la spécification : Trèfles &gt; Piques &gt; Cœurs &gt; Carreaux.
 * Les constantes sont déclarées dans l'ordre croissant de force ; la valeur
 * entière associée permet de comparer deux cartes de même rang.</p>
 */
public enum Color {

    DIAMONDS("Carreaux", 1),
    HEARTS("Cœurs", 2),
    SPADES("Piques", 3),
    CLUBS("Trèfles", 4);

    private final String label;
    private final int strength;

    Color(String label, int strength) {
        this.label = label;
        this.strength = strength;
    }

    /** Libellé affichable : {@code "Trèfles"}, {@code "Cœurs"}, ... */
    public String getLabel() {
        return label;
    }

    /** Force de la couleur : plus la valeur est grande, plus la couleur est forte. */
    public int getStrength() {
        return strength;
    }

    @Override
    public String toString() {
        return label;
    }
}
