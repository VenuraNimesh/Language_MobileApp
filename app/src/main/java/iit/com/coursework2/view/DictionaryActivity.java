package iit.com.coursework2.view;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import iit.com.coursework2.R;
import iit.com.coursework2.controller.LanguageController;

public class DictionaryActivity extends AppCompatActivity {
    LanguageController languageController = new LanguageController(this);
    ArrayList<HashMap<String, String>> translatedMap = new ArrayList<>();
    Spinner dropDown;
    ListView translatedListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        dropDown = (Spinner) findViewById(R.id.spinner);
        translatedListView = (ListView) findViewById(R.id.translatedListView);

        translatedLanguages();
    }

    private void translatedLanguages() {
        Cursor data = languageController.getAllTranslatedLanguages();
        ArrayList<String> translatedLanguages = new ArrayList<>();
        while(data.moveToNext()){
            String language = data.getString(0);

            translatedLanguages.add(language);
        }

        if (translatedLanguages.size() != 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DictionaryActivity.this, android.R.layout.simple_list_item_1, translatedLanguages);
            dropDown.setAdapter(adapter);

            dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    String selectedLanguage = adapterView.getItemAtPosition(position).toString();
                    Log.d("selectedLanguage",selectedLanguage);

                    translatedPhrasesView(selectedLanguage);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Toast.makeText(DictionaryActivity.this, "No language selected", Toast.LENGTH_SHORT).show();
                }
            });
        } else
            Toast.makeText(DictionaryActivity.this, "You have not translated in any Language", Toast.LENGTH_SHORT).show();
    }

    private void translatedPhrasesView(String selectedLanguage) {
        translatedMap.clear();
        Cursor data = languageController.getTranslatedPrases(selectedLanguage);

        while(data.moveToNext()){
            String phrase = data.getString(0);
            String translatedPhrase = data.getString(1);

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("First", phrase);
            hashMap.put("Second", translatedPhrase);

            translatedMap.add(hashMap);
        }

        Custom_ListAdapter adapter = new Custom_ListAdapter(this, translatedMap);
        translatedListView.setAdapter(adapter);
    }
}
