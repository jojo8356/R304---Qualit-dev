package fr.nice.jeu_carte.models.factory;

import fr.nice.jeu_carte.models.Card;
import fr.nice.jeu_carte.models.Color;
import fr.nice.jeu_carte.models.Deck;
import fr.nice.jeu_carte.models.Rank;
import java.util.ArrayList;
import java.util.List;

/**
 * Paquet de test ([PAT] page 3, « TestDeck ») : un 10 par couleur.
 *
 * <p>Toutes les cartes ont le même rang, donc le gagnant est toujours celui qui
 * a la couleur la plus forte : c'est le paquet idéal pour vérifier la règle
 * d'égalité (Trèfles &gt; Piques &gt; Cœurs &gt; Carreaux).</p>
 */
public class TestDeck extends Deck {

    public TestDeck() {
        super(cartesDeTest());
    }

    private static List<Card> cartesDeTest() {
        List<Card> cartes = new ArrayList<>();
        for (Color color : Color.values()) {
            cartes.add(new Card(Rank.TEN, color));
        }
        return cartes;
    }
}
