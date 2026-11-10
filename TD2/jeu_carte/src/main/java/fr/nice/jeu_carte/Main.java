package fr.nice.jeu_carte;

import fr.nice.jeu_carte.controllers.GameController;
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
 * <p>C'est ici — et nulle part ailleurs — que l'on choisit la vue et la règle :
 * le contrôleur les reçoit déjà construites. Changer d'interface ou de règle ne
 * demande donc aucune modification du contrôleur (TD §4.5-4.7).</p>
 *
 * <p>Arguments facultatifs :</p>
 * <ul>
 *   <li>{@code swing} : lance la vue graphique au lieu de la console ;</li>
 *   <li>{@code lowest} : utilise la règle « la carte la plus basse gagne ».</li>
 * </ul>
 */
public final class Main {

    private Main() {
        // classe utilitaire : pas d'instanciation
    }

    public static void main(String[] args) {
        WinnerRule rule = choisirRegle(args);
        if (contient(args, "swing")) {
            lancerVueSwing(rule);
        } else {
            lancerVueConsole(rule);
        }
    }

    /** Vue console : la vue « historique » est branchée via l'adaptateur. */
    private static void lancerVueConsole(WinnerRule rule) {
        GameViewables views = new GameViewables();
        views.add(new GameViewAdapter(new ConsoleView()));

        GameController controller = new GameController(views, rule);
        controller.runOneStep();
    }

    /** Vue graphique Swing : elle implémente directement GameViewable. */
    private static void lancerVueSwing(WinnerRule rule) {
        GameViewables views = new GameViewables();
        GameSwing swing = new GameSwing();
        views.add(swing);

        GameController controller = new GameController(views, rule);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swing.createAndShowGUI();
                controller.runOneStep();
            }
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
