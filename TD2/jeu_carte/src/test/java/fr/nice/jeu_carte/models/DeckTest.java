package fr.nice.jeu_carte.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Vérifie la construction, le mélange, la pioche et la reconstitution du paquet (TD §1, §2.3). */
class DeckTest {

    @Test
    @DisplayName("Le jeu standard contient 52 cartes : 4 couleurs × 13 rangs")
    void tailleStandard() {
        Deck deck = new Deck();
        assertEquals(52, deck.size());
        assertEquals(Deck.STANDARD_DECK_SIZE, deck.size());

        List<Card> cards = tirerTout(deck);
        int as = 0;
        for (Card card : cards) {
            if (card.getRank() == Rank.ACE) {
                as++;
            }
        }
        assertEquals(4, as, "un As par couleur");

        for (Color color : Color.values()) {
            int parCouleur = 0;
            for (Card card : cards) {
                if (card.getColor() == color) {
                    parCouleur++;
                }
            }
            assertEquals(13, parCouleur, "13 cartes de couleur " + color.getLabel());
        }
    }

    @Test
    @DisplayName("Un paquet de 32 cartes va du 7 à l'As")
    void tailleReduite() {
        Deck deck = new Deck(Deck.SMALL_DECK_SIZE);
        assertEquals(32, deck.size());

        List<Card> cards = tirerTout(deck);
        boolean sept = false;
        boolean as = false;
        for (Card card : cards) {
            assertTrue(card.getRank().getStrength() >= Rank.SEVEN.getStrength(),
                    "aucune carte sous le 7 ne doit rester : " + card.label());
            sept |= card.getRank() == Rank.SEVEN;
            as |= card.getRank() == Rank.ACE;
        }
        assertTrue(sept, "le 7 est la plus petite carte du paquet");
        assertTrue(as, "l'As est la plus grande carte du paquet");
    }

    @Test
    @DisplayName("Toute autre taille de paquet est refusée")
    void tailleInvalide() {
        assertThrows(IllegalArgumentException.class, () -> new Deck(0));
        assertThrows(IllegalArgumentException.class, () -> new Deck(35));
        assertThrows(IllegalArgumentException.class, () -> new Deck(54));
    }

    @Test
    @DisplayName("Un paquet ne contient aucune carte en double")
    void pasDeDoublon() {
        Deck deck = new Deck();
        Set<String> libelles = new HashSet<>();
        for (Card card : tirerTout(deck)) {
            assertTrue(libelles.add(card.label()), "carte en double : " + card.label());
        }
        assertEquals(52, libelles.size());
    }

    @Test
    @DisplayName("Mélanger conserve exactement les 52 mêmes cartes, sans doublon")
    void melangeConserveLesCartes() {
        Deck reference = new Deck();
        Set<String> attendues = new HashSet<>();
        for (Card card : tirerTout(reference)) {
            attendues.add(card.label());
        }

        Deck melange = new Deck();
        melange.shuffle();
        assertEquals(52, melange.size());

        Set<String> obtenues = new HashSet<>();
        for (Card card : tirerTout(melange)) {
            obtenues.add(card.label());
        }
        assertEquals(52, obtenues.size(), "aucun doublon après mélange");
        assertEquals(attendues, obtenues);
    }

    @Test
    @DisplayName("drawCard retire bien la première carte")
    void pioche() {
        Deck deck = new Deck();
        Card premiere = deck.drawCard();
        assertEquals(51, deck.size());
        assertEquals(Rank.TWO, premiere.getRank(), "les cartes sont ajoutées du 2 à l'As");
        assertEquals(Color.DIAMONDS, premiere.getColor());
    }

    @Test
    @DisplayName("Piocher dans un paquet vide est une erreur explicite")
    void piochePaquetVide() {
        Deck deck = new Deck(Deck.SMALL_DECK_SIZE);
        while (!deck.isEmpty()) {
            deck.drawCard();
        }
        assertTrue(deck.isEmpty());
        assertThrows(IllegalStateException.class, deck::drawCard);
    }

    @Test
    @DisplayName("Une carte remise dans le paquet y retourne face cachée")
    void addCardRemetFaceCachee() {
        Deck deck = new Deck();
        Card card = deck.drawCard();
        card.flip();
        assertTrue(card.isFaceUp());

        deck.addCard(card);
        assertFalse(card.isFaceUp());
        assertEquals(52, deck.size());
    }

    @Test
    @DisplayName("Ajouter une carte nulle est refusé")
    void addCardNulle() {
        assertThrows(IllegalArgumentException.class, () -> new Deck().addCard(null));
    }

    @Test
    @DisplayName("Un paquet construit sur une liste nulle ou vide est refusé")
    void paquetSurListeInvalide() {
        assertThrows(IllegalArgumentException.class, () -> new Deck((List<Card>) null));
        assertThrows(IllegalArgumentException.class, () -> new Deck(new ArrayList<Card>()));
    }

    @Test
    @DisplayName("Un paquet construit sur une liste garde les cartes fournies")
    void paquetSurListe() {
        List<Card> cartes = new ArrayList<>();
        cartes.add(new Card(Rank.TEN, Color.CLUBS));
        cartes.add(new Card(Rank.TEN, Color.SPADES));

        Deck deck = new Deck(cartes);
        assertEquals(2, deck.size());
        assertEquals(Rank.TEN, deck.drawCard().getRank());
        assertEquals(Color.SPADES, deck.drawCard().getColor());
        // la liste d'origine n'est pas modifiée par les pioches
        assertEquals(2, cartes.size());
    }

    private List<Card> tirerTout(Deck deck) {
        List<Card> cards = new ArrayList<>();
        while (!deck.isEmpty()) {
            cards.add(deck.drawCard());
        }
        return cards;
    }
}
