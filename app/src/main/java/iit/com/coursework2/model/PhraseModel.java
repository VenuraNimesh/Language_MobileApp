package iit.com.coursework2.model;

public class PhraseModel {
    private Integer ID;
    private String phrase;

    public PhraseModel(Integer ID, String phrase) {
        this.ID = ID;
        this.phrase = phrase;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }
}
