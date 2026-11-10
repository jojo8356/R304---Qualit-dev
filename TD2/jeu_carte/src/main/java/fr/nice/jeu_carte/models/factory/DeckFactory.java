package fr.nice.jeu_carte.models.factory;

import fr.nice.jeu_carte.models.Deck;
import java.util.Objects;

/**
 * Fabrique de paquets (patron Factory, [PAT] pages 3-4).
 *
 * <p>Le contrôleur ne fait plus {@code new Deck()} : il demande un paquet à la
 * factory. Changer de type de paquet (32, 52, paquet de test) ne demande donc
 * aucune modification du code métier.</p>
 *
 * <p>La factory peut être configurée à l'avance ({@link #setType(DeckType)}) ou
 * recevoir le type à chaque appel ({@link #makeDeck(DeckType)}), comme le
 * décrit le polycopié (page 4).</p>
 */
public class DeckFactory {

    private DeckType type;

    /** Factory configurée pour produire des paquets de 52 cartes. */
    public DeckFactory() {
        this(DeckType.NORMAL_52);
    }

    public DeckFactory(DeckType type) {
        setType(type);
    }

    public void setType(DeckType type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    public DeckType getType() {
        return type;
    }

    /** Produit un paquet du type configuré. */
    public Deck makeDeck() {
        return makeDeck(type);
    }

    /**
     * Produit un paquet du type demandé.
     *
     * @throws IllegalArgumentException si le type est {@code null}
     */
    public Deck makeDeck(DeckType type) {
        if (type == null) {
            throw new IllegalArgumentException("Le type de paquet ne peut pas être nul.");
        }
        switch (type) {
            case SMALL_32:
                return new SmallDeck();
            case TEST:
                return new TestDeck();
            case NORMAL_52:
            default:
                return new NormalDeck();
        }
    }
}
