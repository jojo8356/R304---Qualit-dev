package fr.nice.jeu_carte.models.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.nice.jeu_carte.models.Card;
import fr.nice.jeu_carte.models.Color;
import fr.nice.jeu_carte.models.Player;
import fr.nice.jeu_carte.models.Rank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Vérifie les deux règles de victoire (patron Strategy, TD §4.2-4.4). */
class WinnerRuleTest {

    @Test
    @DisplayName("Carte haute : l'As l'emporte sur le Roi")
    void carteHaute() {
        Player alice = joueur("Alice", Rank.KING, Color.CLUBS);
        Player bob = joueur("Bob", Rank.ACE, Color.DIAMONDS);

        Optional<Player> gagnant = new HighestCardWins().evaluate(Arrays.asList(alice, bob));
        assertSame(bob, gagnant.orElseThrow(IllegalStateException::new));
    }

    @Test
    @DisplayName("Carte haute : à rang égal, la couleur départage")
    void carteHauteCouleur() {
        Player carreaux = joueur("Carreaux", Rank.TEN, Color.DIAMONDS);
        Player trefles = joueur("Trèfles", Rank.TEN, Color.CLUBS);

        Optional<Player> gagnant = new HighestCardWins().evaluate(Arrays.asList(carreaux, trefles));
        assertSame(trefles, gagnant.orElseThrow(IllegalStateException::new));
    }

    @Test
    @DisplayName("Carte basse : le 2 l'emporte sur l'As")
    void carteBasse() {
        Player alice = joueur("Alice", Rank.ACE, Color.CLUBS);
        Player bob = joueur("Bob", Rank.TWO, Color.SPADES);

        Optional<Player> gagnant = new LowestCardWins().evaluate(Arrays.asList(alice, bob));
        assertSame(bob, gagnant.orElseThrow(IllegalStateException::new));
    }

    @Test
    @DisplayName("Carte basse : à rang égal, la couleur la plus faible gagne")
    void carteBasseCouleur() {
        Player trefles = joueur("Trèfles", Rank.TEN, Color.CLUBS);
        Player carreaux = joueur("Carreaux", Rank.TEN, Color.DIAMONDS);

        Optional<Player> gagnant = new LowestCardWins().evaluate(Arrays.asList(trefles, carreaux));
        assertSame(carreaux, gagnant.orElseThrow(IllegalStateException::new));
    }

    @Test
    @DisplayName("Les deux règles renvoient la même chose sur une liste vide")
    void listeVide() {
        assertTrue(new HighestCardWins().evaluate(Collections.<Player>emptyList()).isEmpty());
        assertTrue(new LowestCardWins().evaluate(Collections.<Player>emptyList()).isEmpty());
        assertTrue(new HighestCardWins().evaluate(null).isEmpty());
    }

    @Test
    @DisplayName("Un joueur sans carte rend l'évaluation impossible")
    void joueurSansCarte() {
        List<Player> joueurs = new ArrayList<>();
        joueurs.add(new Player("Sans carte"));
        assertThrows(IllegalStateException.class, () -> new HighestCardWins().evaluate(joueurs));
        assertThrows(IllegalStateException.class, () -> new LowestCardWins().evaluate(joueurs));
    }

    @Test
    @DisplayName("Avec un seul joueur, il gagne toujours")
    void joueurUnique() {
        Player seul = joueur("Seul", Rank.FIVE, Color.HEARTS);
        assertSame(seul, new HighestCardWins().evaluate(List.of(seul)).orElseThrow());
        assertSame(seul, new LowestCardWins().evaluate(List.of(seul)).orElseThrow());
    }

    private static Player joueur(String nom, Rank rank, Color color) {
        Player player = new Player(nom);
        player.addCardToHand(new Card(rank, color));
        return player;
    }
}
