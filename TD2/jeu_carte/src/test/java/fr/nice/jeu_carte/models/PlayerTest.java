package fr.nice.jeu_carte.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Vérifie l'identité et la main d'un joueur (TD §2.4). */
class PlayerTest {

    @Test
    @DisplayName("Le nom est indispensable (pas de null ni de blanc)")
    void nomObligatoire() {
        assertThrows(IllegalArgumentException.class, () -> new Player(null));
        assertThrows(IllegalArgumentException.class, () -> new Player(""));
        assertThrows(IllegalArgumentException.class, () -> new Player("   "));
    }

    @Test
    @DisplayName("Les espaces autour du nom sont supprimés")
    void nomNettoye() {
        assertEquals("Alice", new Player("  Alice  ").getName());
    }

    @Test
    @DisplayName("Un joueur possède une main, vide au départ")
    void mainParDefaut() {
        Player player = new Player("Bob");
        assertNotNull(player.getHand());
        assertEquals(0, player.getHand().size());
    }

    @Test
    @DisplayName("Une main fournie est conservée telle quelle")
    void mainFournie() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.QUEEN, Color.SPADES));
        Player player = new Player("Carol", hand);
        assertSame(hand, player.getHand());
        assertEquals(1, player.getHand().size());
    }

    @Test
    @DisplayName("Ajouter et retirer une carte passe par la main du joueur")
    void cartesDuJoueur() {
        Player player = new Player("Dave");
        player.addCardToHand(new Card(Rank.JACK, Color.HEARTS));

        assertEquals(1, player.getHand().size());
        assertEquals(Rank.JACK, player.removeCardFromHand(0).getRank());
        assertEquals(0, player.getHand().size());
    }

    @Test
    @DisplayName("Le joueur se présente avec sa main")
    void presentation() {
        Player player = new Player("Eve");
        assertEquals("Eve : (main vide)", player.presentHand());

        player.addCardToHand(new Card(Rank.ACE, Color.DIAMONDS));
        assertEquals("Eve : As de Carreaux", player.presentHand());
        assertEquals("Eve", player.toString());
    }
}
