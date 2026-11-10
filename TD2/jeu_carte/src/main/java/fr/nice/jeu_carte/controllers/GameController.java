package fr.nice.jeu_carte.controllers;

import java.util.ArrayList;
import java.util.Scanner;

import fr.nice.jeu_carte.models.Deck;
import fr.nice.jeu_carte.models.Hand;
import fr.nice.jeu_carte.models.Player;
import fr.nice.jeu_carte.views.View;

public class GameController {
    
    private enum GameState {
        AddingPlayer,
        CardsDealt,
        WinnerRevealed
    };

    GameState gameState;
    Deck deck;
    View view;
    ArrayList<Player> players;
    int numberOfPlayers;

    public GameController(Deck deck, View view) {
        this.deck = deck;
        this.view = view;
        this.gameState = GameState.AddingPlayer;
        view.setGameController(this);
    }

    public int getLenPlayers() {
        System.out.println("Combien de joueurs voulez-vous ajouter ?");
        Scanner scanner = new Scanner(System.in);
        try {
            int numberOfPlayers = scanner.nextInt();
            while (numberOfPlayers < 2 || numberOfPlayers > 5) {
                System.out.println("Le nombre de joueurs doit être compris entre 2 et 5. Veuillez réessayer :");
                numberOfPlayers = scanner.nextInt();
            }
            return numberOfPlayers;
        } finally {
            scanner.close();
        }
    }

    public ArrayList<Player> createPlayers() {
        Scanner scanner = new Scanner(System.in);
        try {
            for (int i = 1; i <= numberOfPlayers; i++) {
                System.out.println("Entrez le nom du joueur " + i + " :");
                String playerName = scanner.nextLine();
                Hand hand = new Hand();
                this.players.add(new Player(playerName, hand));
            }
        } finally {
            scanner.close();
        }
    }

    public void startGame() {
        for (Player player : players) {
            player.addCardToHand(deck.drawCard());
        }
        gameState = GameState.CardsDealt;
    }
}
