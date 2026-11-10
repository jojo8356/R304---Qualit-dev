package fr.nice.jeu_carte;

import fr.nice.jeu_carte.controllers.GameController;
import fr.nice.jeu_carte.models.Hand;
import fr.nice.jeu_carte.models.Player;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        int numberOfPlayers = GameController.getLenPlayers();
        ArrayList<Player> players = GameController.createPlayers(numberOfPlayers);
        GameController gameController = new GameController();
        gameController.startGame(players);
    }
}
