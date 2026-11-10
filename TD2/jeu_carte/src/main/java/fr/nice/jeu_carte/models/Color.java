package fr.nice.jeu_carte.models;

public enum Color {
    HEARTS("Hearts", 1),
    DIAMONDS("Diamonds", 2),
    CLUBS("Clubs", 3),
    SPADES("Spades", 4);

    private String name;
    private int value;

    Color(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }
}
