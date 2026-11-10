package fr.nice.jeu_carte;

import fr.nice.jeu_carte.controllers.GameController;
import fr.nice.jeu_carte.models.Deck;
import fr.nice.jeu_carte.models.factory.DeckBuilder;
import fr.nice.jeu_carte.models.factory.GameSetup;
import fr.nice.jeu_carte.models.rules.HighestCardWins;
import fr.nice.jeu_carte.models.rules.LowestCardWins;
import fr.nice.jeu_carte.models.rules.WinnerRule;
import fr.nice.jeu_carte.views.GameViewables;
import fr.nice.jeu_carte.views.adapter.GameViewAdapter;
import fr.nice.jeu_carte.views.legacy.ConsoleView;
import fr.nice.jeu_carte.views.swing.GameSwing;
import javax.swing.SwingUtilities;

/**
 * Point d'entrée du jeu (TD §3.1).
 *
 * <p>C'est ici — et nulle part ailleurs — que l'on choisit la vue, la taille du
 * paquet et la règle : le contrôleur reçoit une configuration déjà construite
 * par le {@link DeckBuilder} ([PAT] pages 5-6) et n'a donc aucune dépendance
 * sur la façon de créer les objets.</p>
 *
 * <p>Arguments facultatifs :</p>
 * <ul>
 *   <li>{@code swing} : lance la vue graphique au lieu de la console ;</li>
 *   <li>{@code 32} : joue avec un paquet de 32 cartes (52 par défaut) ;</li>
 *   <li>{@code lowest} : utilise la règle « la carte la plus basse gagne ».</li>
 * </ul>
 */
public final class Main {

    private Main() {
        // classe utilitaire : pas d'instanciation
    }

    public static void main(String[] args) {
        GameSetup setup = new DeckBuilder()
                .size(contient(args, "32") ? Deck.SMALL_DECK_SIZE : Deck.STANDARD_DECK_SIZE)
                .rule(choisirRegle(args))
                .build();

        if (contient(args, "swing")) {
            lancerVueSwing(setup);
        } else {
            lancerVueConsole(setup);
        }
    }

    /** Vue console : la vue « historique » est branchée via l'adaptateur. */
    private static void lancerVueConsole(GameSetup setup) {
        GameViewables views = new GameViewables();
        views.add(new GameViewAdapter(new ConsoleView()));

        GameController controller = new GameController(setup, views);
        controller.runOneStep();
    }

    /** Vue graphique Swing : elle implémente directement GameViewable. */
    private static void lancerVueSwing(GameSetup setup) {
        GameViewables views = new GameViewables();
        GameSwing swing = new GameSwing();
        views.add(swing);

        GameController controller = new GameController(setup, views);

        SwingUtilities.invokeLater(() -> {
            swing.createAndShowGUI();
            controller.runOneStep();
        });
    }

    private static WinnerRule choisirRegle(String[] args) {
        if (contient(args, "lowest")) {
            return new LowestCardWins();
        }
        return new HighestCardWins();
    }

    private static boolean contient(String[] args, String valeur) {
        if (args == null) {
            return false;
        }
        for (String argument : args) {
            if (valeur.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }
}
