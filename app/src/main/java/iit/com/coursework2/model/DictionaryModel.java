package iit.com.coursework2.model;

public class DictionaryModel {
    private String lang_id;
    private String phrase_id;
    private String phrase_name;
    private String translated_phrase;
    private String lang_name;

    public DictionaryModel(String lang_id, String phrase_id, String phrase_name, String translated_phrase, String lang_name) {
        this.lang_id = lang_id;
        this.phrase_id = phrase_id;
        this.phrase_name = phrase_name;
        this.translated_phrase = translated_phrase;
        this.lang_name = lang_name;
    }

    public String getLang_id() {
        return lang_id;
    }

    public void setLang_id(String lang_id) {
        this.lang_id = lang_id;
    }

    public String getPhrase_id() {
        return phrase_id;
    }

    public void setPhrase_id(String phrase_id) {
        this.phrase_id = phrase_id;
    }

    public String getPhrase_name() {
        return phrase_name;
    }

    public void setPhrase_name(String phrase_name) {
        this.phrase_name = phrase_name;
    }

    public String getTranslated_phrase() {
        return translated_phrase;
    }

    public void setTranslated_phrase(String translated_phrase) {
        this.translated_phrase = translated_phrase;
    }

    public String getLang_name() {
        return lang_name;
    }

    public void setLang_name(String lang_name) {
        this.lang_name = lang_name;
    }
}
