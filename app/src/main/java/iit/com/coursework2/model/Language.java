package iit.com.coursework2.model;

public class Language {
    private String ID;
    private String lang_code;
    private String lang_name;
    private Integer subscribed;

    public Language(String ID, String lang_code, String lang_name, Integer subscribed) {
        this.ID = ID;
        this.lang_code = lang_code;
        this.lang_name = lang_name;
        this.subscribed = subscribed;
    }

    public Language(String lang_code, String lang_name, Integer subscribed) {
        this.lang_code = lang_code;
        this.lang_name = lang_name;
        this.subscribed = subscribed;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLang_code() {
        return lang_code;
    }

    public void setLang_code(String lang_code) {
        this.lang_code = lang_code;
    }

    public String getLang_name() {
        return lang_name;
    }

    public void setLang_name(String lang_name) {
        this.lang_name = lang_name;
    }

    public Integer getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(Integer subscribed) {
        this.subscribed = subscribed;
    }
}

