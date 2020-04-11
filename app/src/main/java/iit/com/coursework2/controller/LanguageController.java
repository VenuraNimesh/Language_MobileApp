package iit.com.coursework2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

import iit.com.coursework2.model.DatabaseHelper;
import iit.com.coursework2.model.LanguageModel;

public class LanguageController extends DatabaseHelper{

    public LanguageController(Context context) {
        super(context);
    }

    public void addAllLanguages(LanguageModel languageModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLT2, languageModel.getLang_code());
        contentValues.put(COLT3, languageModel.getLang_name());
        contentValues.put(COLT4, languageModel.getSubscribed());

        db.insert(TABLE_TRANSLATE, null, contentValues);
    }

    public Cursor getAllLanguages(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TRANSLATE;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Integer checkLanguageTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TRANSLATE;
        Cursor data = db.rawQuery(query, null);

        data.moveToFirst();
        int count = data.getInt(0);
        return count;
    }

    public Cursor getLanguageID(String language){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COLT1 + " FROM " + TABLE_TRANSLATE + " WHERE " + COLT3 + " = '" + language + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

//    public boolean updateSubscription(String id){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLT4, 1);
//
//        db.update(TABLE_TRANSLATE, contentValues, "ID = ?", new String[] { id });
//        return true;
//    }

    public boolean updateSubscription(HashMap<String, Integer> subscribedHashMap){
        SQLiteDatabase db = this.getWritableDatabase();

        for(String i : subscribedHashMap.keySet()){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLT4, subscribedHashMap.get(i));

            db.update(TABLE_TRANSLATE, contentValues, "ID = ?", new String[] {(i)});
        }

        return true;
    }
}
