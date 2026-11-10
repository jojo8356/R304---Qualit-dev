package fr.nice.jeu_carte.views.legacy;

import fr.nice.jeu_carte.controllers.GameController;

/**
 * Interface de vue « historique » du projet, écrite en français
 * (équivalent de {@code JeuVisible} dans [ADP] page 2).
 *
 * <p>Elle ne respecte pas l'interface standardisée {@code GameViewable}
 * attendue par la vue Swing : c'est exactement le problème que résout le
 * patron Adapter ([ADP] pages 3-4) grâce à
 * {@link fr.nice.jeu_carte.views.adapter.GameViewAdapter}.</p>
 *
 * <p>Règle du TD : on ne renomme rien et on ne modifie pas le contrôleur.</p>
 */
public interface VueJeu {

    void demandeNomJoueur();

    void demandeRetournerCarte();

    void demandeNouveauJeu();

    void montreNomJoueur(int index, String nom);

    void montreFaceCacheJoueur(int index, String nom);

    void montreCarteJoueur(int index, String nom, String rang, String couleur);

    void montreGagnant(String nom);

    void setController(GameController controller);
}
