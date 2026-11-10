package fr.nice.jeu_carte.models.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.nice.jeu_carte.models.Card;
import fr.nice.jeu_carte.models.Color;
import fr.nice.jeu_carte.models.Deck;
import fr.nice.jeu_carte.models.Player;
import fr.nice.jeu_carte.models.Rank;
import fr.nice.jeu_carte.models.rules.HighestCardWins;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Vérifie la fabrique de paquets (patron Factory, [PAT] pages 3-4). */
class DeckFactoryTest {

    @Test
    @DisplayName("Par défaut, la factory produit un jeu de 52 cartes")
    void paquetParDefaut() {
        DeckFactory factory = new DeckFactory();
        assertEquals(DeckType.NORMAL_52, factory.getType());
        Deck deck = factory.makeDeck();
        assertEquals(52, deck.size());
        assertInstanceOf(NormalDeck.class, deck);
    }

    @Test
    @DisplayName("La factory sait produire un paquet de 32 cartes")
    void paquet32() {
        Deck deck = new DeckFactory().makeDeck(DeckType.SMALL_32);
        assertEquals(32, deck.size());
        assertInstanceOf(SmallDeck.class, deck);
        assertTrue(deck.drawCard().getRank().getStrength() >= Rank.SEVEN.getStrength());
    }

    @Test
    @DisplayName("Le paquet de test contient un 10 par couleur")
    void paquetDeTest() {
        Deck deck = new DeckFactory().makeDeck(DeckType.TEST);
        assertEquals(4, deck.size());
        assertInstanceOf(TestDeck.class, deck);

        Set<Color> couleurs = new HashSet<>();
        while (!deck.isEmpty()) {
            Card card = deck.drawCard();
            assertEquals(Rank.TEN, card.getRank());
            assertTrue(couleurs.add(card.getColor()));
        }
        assertEquals(4, couleurs.size());
    }

    @Test
    @DisplayName("Paquet de test + carte haute : le 10 de Trèfles gagne, la couleur tranche")
    void paquetDeTestTrancheParLaCouleur() {
        Deck deck = new DeckFactory().makeDeck(DeckType.TEST);
        List<Player> joueurs = new ArrayList<>();
        while (!deck.isEmpty()) {
            Player joueur = new Player("J" + (joueurs.size() + 1));
            joueur.addCardToHand(deck.drawCard());
            joueurs.add(joueur);
        }

        Player gagnant = new HighestCardWins().evaluate(joueurs).orElseThrow(IllegalStateException::new);
        assertEquals("10 de Trèfles", gagnant.getHand().getCard(0).label());
    }

    @Test
    @DisplayName("Le type peut être configuré une fois puis réutilisé")
    void typeConfigure() {
        DeckFactory factory = new DeckFactory(DeckType.SMALL_32);
        assertEquals(32, factory.makeDeck().size());
        assertEquals(32, factory.makeDeck().size());

        factory.setType(DeckType.NORMAL_52);
        assertEquals(52, factory.makeDeck().size());
    }

    @Test
    @DisplayName("Un type nul est refusé")
    void typeNul() {
        assertThrows(NullPointerException.class, () -> new DeckFactory(null));
        assertThrows(IllegalArgumentException.class, () -> new DeckFactory().makeDeck(null));
        assertThrows(NullPointerException.class, () -> new DeckFactory().setType(null));
    }

    @Test
    @DisplayName("Chaque appel produit un paquet indépendant")
    void paquetsIndependants() {
        DeckFactory factory = new DeckFactory();
        Deck premier = factory.makeDeck();
        Deck second = factory.makeDeck();

        premier.shuffle();
        premier.drawCard();
        assertEquals(51, premier.size());
        assertEquals(52, second.size());
    }
}
