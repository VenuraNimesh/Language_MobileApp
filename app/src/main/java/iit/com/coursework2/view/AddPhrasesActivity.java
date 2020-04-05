package iit.com.coursework2.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;

import iit.com.coursework2.R;
import iit.com.coursework2.controller.PhraseController;
import iit.com.coursework2.model.DatabaseHelper;

public class AddPhrasesActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnSave;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phrases);

        editText = (EditText) findViewById(R.id.phrase);
        btnSave = (Button) findViewById(R.id.save);
        databaseHelper = new DatabaseHelper(this);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phrase = editText.getText().toString();

                if (phrase.length() != 0) {
                    String newPhrase = phrase.substring(0, 1).toUpperCase() + phrase.substring(1);

                    //if (editText.length() != 0) {
                    AddData(newPhrase);
                    editText.setText("");
                    //}
                } else {
                    toastMessage("Enter a word or a phrase");
                }

            }
        });
    }

    public void AddData(String newPhrase) {
        PhraseController phraseController = new PhraseController(this);
        boolean insertData = phraseController.addPhrases(newPhrase);

        if (insertData) {
            toastMessage("Word/Phrase successfully inserted");
        } else {
            toastMessage("Something went wrong");
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
