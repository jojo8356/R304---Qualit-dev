package fr.nice.jeu_carte.models;

/**
 * Rang d'une carte (TD §2.1).
 *
 * <p>Les constantes sont déclarées dans l'ordre croissant de force et portent
 * explicitement leur valeur entière : As (14) &gt; Roi (13) &gt; ... &gt; 2.
 * L'ordre de déclaration (utilisé par {@code ordinal()}) et {@link #getStrength()}
 * disent donc la même chose, ce qui évite les incohérences silencieuses.</p>
 */
public enum Rank {

    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("Valet", 11),
    QUEEN("Reine", 12),
    KING("Roi", 13),
    ACE("As", 14);

    private final String label;
    private final int strength;

    Rank(String label, int strength) {
        this.label = label;
        this.strength = strength;
    }

    /** Libellé affichable : {@code "As"}, {@code "Valet"}, {@code "10"}, ... */
    public String getLabel() {
        return label;
    }

    /** Force du rang : plus la valeur est grande, plus la carte est forte. */
    public int getStrength() {
        return strength;
    }

    @Override
    public String toString() {
        return label;
    }
}
