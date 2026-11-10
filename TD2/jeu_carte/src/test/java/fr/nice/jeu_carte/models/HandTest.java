package fr.nice.jeu_carte.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Vérifie la main d'un joueur (TD §2.4). */
class HandTest {

    @Test
    @DisplayName("Une main est vide à la création")
    void mainVide() {
        Hand hand = new Hand();
        assertTrue(hand.isEmpty());
        assertEquals(0, hand.size());
        assertEquals("(main vide)", hand.toString());
    }

    @Test
    @DisplayName("Ajouter puis retirer une carte met la main à jour")
    void ajoutEtRetrait() {
        Hand hand = new Hand();
        Card as = new Card(Rank.ACE, Color.CLUBS);

        hand.addCard(as);
        assertEquals(1, hand.size());
        assertFalse(hand.isEmpty());
        assertSame(as, hand.getCard(0));

        Card retiree = hand.removeCard(0);
        assertSame(as, retiree);
        assertTrue(hand.isEmpty());
    }

    @Test
    @DisplayName("La vue des cartes est non modifiable")
    void vueNonModifiable() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.KING, Color.HEARTS));
        assertThrows(UnsupportedOperationException.class,
                () -> hand.cards().add(new Card(Rank.TWO, Color.SPADES)));
    }

    @Test
    @DisplayName("Une carte nulle est refusée")
    void carteNulle() {
        assertThrows(IllegalArgumentException.class, () -> new Hand().addCard(null));
    }

    @Test
    @DisplayName("Les indices hors bornes sont refusés")
    void indicesHorsBornes() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.SEVEN, Color.DIAMONDS));

        assertThrows(IndexOutOfBoundsException.class, () -> hand.getCard(1));
        assertThrows(IndexOutOfBoundsException.class, () -> hand.getCard(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> hand.removeCard(5));
    }

    @Test
    @DisplayName("La main s'affiche sous forme lisible")
    void affichage() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Color.CLUBS));
        hand.addCard(new Card(Rank.TEN, Color.HEARTS));
        assertEquals("As de Trèfles, 10 de Cœurs", hand.toString());
    }
}
