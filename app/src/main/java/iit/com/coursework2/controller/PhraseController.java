package iit.com.coursework2.controller;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import iit.com.coursework2.model.DatabaseHelper;


public class PhraseController extends DatabaseHelper{

    public PhraseController(Context context) {
        super(context);
    }

    public boolean addPhrases(String phrase){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, phrase);

        long result = db.insert(TABLE_PHRASE, null, contentValues);

        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PHRASE;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getPhraseID(String phrase){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL1 + " FROM " + TABLE_PHRASE + " WHERE " + COL2 + " = '" + phrase + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public boolean updateData(String editPhrase, String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, editPhrase);

        db.update(TABLE_PHRASE, contentValues, "ID = ?", new String[] { id });
        return true;
    }
}
