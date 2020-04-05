package iit.com.coursework2.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Language_Translate.db";
    private static final int DATABASE_VERSION = 1;

    protected static final String TABLE_PHRASE = "language_phrases";
    protected static final String COL1 = "ID";
    protected static final String COL2 = "phrases";

    protected static final String TABLE_TRANSLATE = "language_translate";
    protected static final String COLT1 = "ID";
    protected static final String COLT2 = "lang_code";
    protected static final String COLT3 = "lang_name";
    protected static final String COLT4 = "subscribed";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create_Table_Phrases = "CREATE TABLE " + TABLE_PHRASE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT)";
        String Create_Table_Translate = "CREATE TABLE " + TABLE_TRANSLATE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                                COLT2 + " TEXT," +
                                                                                COLT3 + " TEXT," +
                                                                                COLT4 + " INTEGER)";

        db.execSQL(Create_Table_Phrases);
        db.execSQL(Create_Table_Translate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PHRASE);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TRANSLATE);
        onCreate(db);
    }

}
