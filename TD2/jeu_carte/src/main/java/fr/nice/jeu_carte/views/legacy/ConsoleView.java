package fr.nice.jeu_carte.views.legacy;

import fr.nice.jeu_carte.controllers.GameController;
import java.util.Scanner;

/**
 * Vue console du jeu (TD §3.5 et §3.7).
 *
 * <p>Elle ne fait qu'une chose : afficher ce que le contrôleur lui demande, et
 * transmettre les saisies de l'utilisateur. Elle ne manipule jamais les objets
 * du modèle (elle reçoit des noms et des libellés) et ne décide jamais de la
 * suite du jeu.</p>
 *
 * <p>Le {@link Scanner} n'est jamais fermé : fermer {@code System.in} rendrait
 * toute saisie ultérieure impossible. En fin de saisie (EOF sur une entrée
 * redirigée), la vue se contente de se mettre en sommeil : elle rend la main au
 * lieu de tuer la JVM.</p>
 */
public class ConsoleView implements VueJeu {

    private final Scanner scanner;
    private GameController controller;
    private boolean terminee;

    public ConsoleView() {
        this(new Scanner(System.in));
    }

    /** Constructeur utile pour les tests : on fournit la source des saisies. */
    public ConsoleView(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Le scanner ne peut pas être nul.");
        }
        this.scanner = scanner;
        this.terminee = false;
    }

    @Override
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /** Vrai lorsque l'entrée standard est épuisée (plus aucune saisie possible). */
    public boolean isFinished() {
        return terminee;
    }

    // ------------------------------------------------------------------
    // Sollicitations du contrôleur
    // ------------------------------------------------------------------

    @Override
    public void demandeNomJoueur() {
        while (!terminee) {
            StringBuilder message = new StringBuilder();
            if (controller.getState() == GameController.GameState.AddingPlayer && controller.canAddPlayer()) {
                message.append("Nom du joueur ").append(controller.getPlayerCount() + 1);
                if (controller.hasPlayers()) {
                    message.append(" (Entrée seule = distribuer les cartes)");
                }
                message.append(" : ");
            } else {
                message.append("Entrée = distribuer les cartes : ");
            }
            String nom = lire(message.toString());
            if (terminee) {
                return;
            }

            if (nom.isEmpty()) {
                if (!controller.hasPlayers()) {
                    System.out.println("Ajoutez au moins un joueur avant de distribuer.");
                    continue;
                }
                try {
                    controller.startGame();
                    return;
                } catch (RuntimeException e) {
                    System.out.println("Refusé : " + e.getMessage());
                    continue;
                }
            }

            if (!controller.canAddPlayer()) {
                System.out.println("Impossible d'ajouter un joueur supplémentaire.");
                continue;
            }
            try {
                controller.addPlayer(nom);
                return;
            } catch (RuntimeException e) {
                System.out.println("Refusé : " + e.getMessage());
            }
        }
    }

    @Override
    public void demandeRetournerCarte() {
        lire("Entrée pour retourner les cartes : ");
        if (terminee) {
            return;
        }
        try {
            controller.flipCards();
        } catch (RuntimeException e) {
            System.out.println("Refusé : " + e.getMessage());
            controller.runOneStep();
        }
    }

    @Override
    public void demandeNouveauJeu() {
        lire("Entrée pour récupérer les cartes et rejouer : ");
        if (terminee) {
            return;
        }
        try {
            controller.rebuildDeck();
        } catch (RuntimeException e) {
            System.out.println("Refusé : " + e.getMessage());
            controller.runOneStep();
        }
    }

    // ------------------------------------------------------------------
    // Affichage
    // ------------------------------------------------------------------

    @Override
    public void montreNomJoueur(int index, String nom) {
        System.out.println("Joueur " + (index + 1) + " : " + nom);
    }

    @Override
    public void montreFaceCacheJoueur(int index, String nom) {
        System.out.println(nom + " : [carte face cachée]");
    }

    @Override
    public void montreCarteJoueur(int index, String nom, String rang, String couleur) {
        System.out.println(nom + " : " + rang + " de " + couleur);
    }

    @Override
    public void montreGagnant(String nom) {
        System.out.println("Gagnant : " + nom);
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Outils internes
    // ------------------------------------------------------------------

    private String lire(String message) {
        if (terminee) {
            return "";
        }
        System.out.print(message);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("Fin de la saisie : la partie s'arrête.");
            terminee = true;
            return "";
        }
        return scanner.nextLine().trim();
    }
}
