package com.appdev.flashycard.overview.card;

public class Flashcard {
    private long id;
    private long setId;
    private String term;
    private String definition;

    public Flashcard(long id, long setId, String term, String definition) {
        this.id = id;
        this.setId = setId;
        this.term = term;
        this.definition = definition;
    }

    public long getId() { return id; }
    public String getTerm() { return term; }
    public String getDefinition() { return definition; }
}
