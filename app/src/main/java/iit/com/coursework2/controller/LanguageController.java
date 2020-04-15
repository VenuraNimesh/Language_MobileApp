package iit.com.coursework2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import iit.com.coursework2.model.DatabaseHelper;
import iit.com.coursework2.model.DictionaryModel;
import iit.com.coursework2.model.LanguageModel;

public class LanguageController extends DatabaseHelper {

    public LanguageController(Context context) {
        super(context);
    }

    public void addAllLanguages(LanguageModel languageModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLT2, languageModel.getLang_code());
        contentValues.put(COLT3, languageModel.getLang_name());
        contentValues.put(COLT4, languageModel.getSubscribed());

        db.insert(TABLE_TRANSLATE, null, contentValues);
    }

    public Cursor getAllLanguages() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSLATE;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    //Checking whether table has filled or not
    public Integer checkLanguageTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TRANSLATE;
        Cursor data = db.rawQuery(query, null);

        data.moveToFirst();
        int count = data.getInt(0);
        return count;
    }

    public boolean updateSubscription(HashMap<String, Integer> subscribedHashMap) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (String i : subscribedHashMap.keySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLT4, subscribedHashMap.get(i));

            db.update(TABLE_TRANSLATE, contentValues, "ID = ?", new String[]{(i)});
        }

        return true;
    }

    public void translateAllPhrases(DictionaryModel dictionaryModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLD1, dictionaryModel.getLang_id());
        contentValues.put(COLD2, dictionaryModel.getPhrase_id());
        contentValues.put(COLD3, dictionaryModel.getPhrase_name());
        contentValues.put(COLD4, dictionaryModel.getTranslated_phrase());
        contentValues.put(COLD5, dictionaryModel.getLang_name());

        db.insert(TABLE_DICTIONARY, null, contentValues);
    }

    //Get the languages added to the Dictionary
    public Cursor getAllTranslatedLanguages() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT DISTINCT " + COLD5 + " FROM " + TABLE_DICTIONARY;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getTranslatedPrases(String selectedLang) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COLD3 + "," + COLD4 + " FROM " + TABLE_DICTIONARY + " WHERE " + COLD5 + " = '" + selectedLang + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
