package iit.com.coursework2.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import iit.com.coursework2.R;

public class MainActivity extends AppCompatActivity {
    private Button btnAddPhrases, btnDisplayPharases, btnEditPhrases, btnLanguageSub, btnTranslate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddPhrases = (Button) findViewById(R.id.addPhrase);
        btnDisplayPharases = (Button) findViewById(R.id.displayPhrase);
        btnEditPhrases = (Button) findViewById(R.id.editPhrase);
        btnLanguageSub = (Button) findViewById(R.id.languageSub);
        btnTranslate = (Button) findViewById(R.id.translate);


        btnAddPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddPhrasesActivity.class);
                startActivity(intent);
            }
        });

        btnDisplayPharases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DisplayPhrasesActivity.class);
                startActivity(intent);
            }
        });

        btnEditPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditPhraseActivity.class);
                startActivity(intent);
            }
        });

        btnLanguageSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LanguageSubscriptionActivity.class);
                startActivity(intent);
            }
        });

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TranslateActivity.class);
                startActivity(intent);
            }
        });
    }
}
