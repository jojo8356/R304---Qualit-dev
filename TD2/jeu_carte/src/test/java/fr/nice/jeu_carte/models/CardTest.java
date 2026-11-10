package fr.nice.jeu_carte.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Vérifie les règles de comparaison des cartes (TD §1 et §2.2). */
class CardTest {

    @Test
    @DisplayName("Les 13 rangs sont ordonnés 2 < 3 < ... < Roi < As")
    void rangsOrdonnes() {
        Rank[] expected = {
            Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX, Rank.SEVEN,
            Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE
        };
        for (int i = 1; i < expected.length; i++) {
            assertTrue(expected[i - 1].getStrength() < expected[i].getStrength(),
                    expected[i - 1] + " doit être plus faible que " + expected[i]);
        }
        assertEquals(14, Rank.ACE.getStrength());
        assertEquals(2, Rank.TWO.getStrength());
    }

    @Test
    @DisplayName("Les 4 couleurs sont ordonnées Carreaux < Cœurs < Piques < Trèfles")
    void couleursOrdonnees() {
        assertTrue(Color.DIAMONDS.getStrength() < Color.HEARTS.getStrength());
        assertTrue(Color.HEARTS.getStrength() < Color.SPADES.getStrength());
        assertTrue(Color.SPADES.getStrength() < Color.CLUBS.getStrength());
    }

    @Test
    @DisplayName("Une carte est plus forte qu'une autre si son rang est plus élevé")
    void comparaisonParRang() {
        Card as = new Card(Rank.ACE, Color.DIAMONDS);
        Card roi = new Card(Rank.KING, Color.CLUBS);

        assertTrue(as.compareTo(roi) > 0, "As > Roi, quelle que soit la couleur");
        assertTrue(roi.compareTo(as) < 0);
    }

    @Test
    @DisplayName("À rang égal, Trèfles > Piques > Cœurs > Carreaux")
    void comparaisonParCouleur() {
        Card dixTrefles = new Card(Rank.TEN, Color.CLUBS);
        Card dixPiques = new Card(Rank.TEN, Color.SPADES);
        Card dixCoeurs = new Card(Rank.TEN, Color.HEARTS);
        Card dixCarreaux = new Card(Rank.TEN, Color.DIAMONDS);

        assertTrue(dixTrefles.compareTo(dixPiques) > 0);
        assertTrue(dixPiques.compareTo(dixCoeurs) > 0);
        assertTrue(dixCoeurs.compareTo(dixCarreaux) > 0);
        assertTrue(dixCarreaux.compareTo(dixTrefles) < 0);
    }

    @Test
    @DisplayName("Deux cartes de même rang et même couleur sont égales à la comparaison")
    void egalite() {
        assertEquals(0, new Card(Rank.QUEEN, Color.HEARTS).compareTo(new Card(Rank.QUEEN, Color.HEARTS)));
    }

    @Test
    @DisplayName("Une carte commence face cachée et se retourne")
    void retournement() {
        Card card = new Card(Rank.ACE, Color.SPADES);
        assertFalse(card.isFaceUp());

        card.flip();
        assertTrue(card.isFaceUp());

        card.flip();
        assertFalse(card.isFaceUp());

        // faceDown() est idempotent : utile quand la carte retourne au paquet
        card.flip();
        card.faceDown();
        card.faceDown();
        assertFalse(card.isFaceUp());
    }

    @Test
    @DisplayName("Le libellé d'une carte est lisible")
    void libelle() {
        assertEquals("As de Trèfles", new Card(Rank.ACE, Color.CLUBS).label());
        assertEquals("10 de Carreaux", new Card(Rank.TEN, Color.DIAMONDS).label());
        assertEquals("Valet de Cœurs", new Card(Rank.JACK, Color.HEARTS).label());
    }
}
