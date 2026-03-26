package com.appdev.flashycard.main.set;

public class FlashcardSet {
    private long id;
    private String title;
    private String colorHex;
    private String description;

    public FlashcardSet(long id, String title, String colorHex, String description) {
        this.id = id;
        this.title = title;
        this.colorHex = colorHex;
        this.description = description;
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getColorHex() { return colorHex; }
    public String getDescription() { return description; }
}
