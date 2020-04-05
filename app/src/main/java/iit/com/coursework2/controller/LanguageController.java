package iit.com.coursework2.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import iit.com.coursework2.model.DatabaseHelper;
import iit.com.coursework2.model.Language;

public class LanguageController extends DatabaseHelper{

    public LanguageController(Context context) {
        super(context);
    }

    public void addAllLanguages(Language languageModel){
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
}
