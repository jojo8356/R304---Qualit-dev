package fr.nice.jeu_carte.models.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.nice.jeu_carte.models.Deck;
import fr.nice.jeu_carte.models.rules.HighestCardWins;
import fr.nice.jeu_carte.models.rules.LowestCardWins;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Vérifie le montage d'une partie pièce par pièce (patron Builder, [PAT] pages 5-6). */
class DeckBuilderTest {

    @Test
    @DisplayName("Sans configuration, on obtient un jeu de 52 cartes et la carte haute")
    void configurationParDefaut() {
        GameSetup setup = new DeckBuilder().build();

        assertEquals(52, setup.getDeck().size());
        assertInstanceOf(HighestCardWins.class, setup.getRule());
    }

    @Test
    @DisplayName("size(32) produit un paquet de 32 cartes")
    void taille32() {
        GameSetup setup = new DeckBuilder().size(Deck.SMALL_DECK_SIZE).build();
        assertEquals(32, setup.getDeck().size());
    }

    @Test
    @DisplayName("size(52) produit un paquet de 52 cartes")
    void taille52() {
        GameSetup setup = new DeckBuilder().size(Deck.STANDARD_DECK_SIZE).build();
        assertEquals(52, setup.getDeck().size());
    }

    @Test
    @DisplayName("Une taille non supportée est refusée")
    void tailleInvalide() {
        assertThrows(IllegalArgumentException.class, () -> new DeckBuilder().size(12));
    }

    @Test
    @DisplayName("La règle injectée est conservée")
    void regleInjectee() {
        LowestCardWins regle = new LowestCardWins();
        GameSetup setup = new DeckBuilder().rule(regle).build();
        assertEquals(regle, setup.getRule());
    }

    @Test
    @DisplayName("Les appels se chaînent dans n'importe quel ordre")
    void chainageLibre() {
        GameSetup setup = new DeckBuilder()
                .rule(new LowestCardWins())
                .type(DeckType.SMALL_32)
                .build();

        assertEquals(32, setup.getDeck().size());
        assertInstanceOf(LowestCardWins.class, setup.getRule());
    }

    @Test
    @DisplayName("Le builder reste réutilisable : build() ne le consomme pas")
    void builderReutilisable() {
        DeckBuilder builder = new DeckBuilder().type(DeckType.TEST);
        GameSetup premier = builder.build();
        GameSetup second = builder.build();

        assertEquals(4, premier.getDeck().size());
        assertEquals(4, second.getDeck().size());
        assertTrue(premier.getDeck() != second.getDeck(), "deux paquets distincts");
    }

    @Test
    @DisplayName("Une règle nulle est refusée")
    void regleNulle() {
        assertThrows(NullPointerException.class, () -> new DeckBuilder().rule(null));
    }
}
