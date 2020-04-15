package iit.com.coursework2.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import iit.com.coursework2.R;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = (GridLayout) findViewById(R.id.gridLayout);

        handleLayout(gridLayout);
    }

    private void handleLayout(GridLayout gridLayout) {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            CardView cardView = (CardView) gridLayout.getChildAt(i);
            final int cardNo = i + 1;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    switch (cardNo) {
                        case 1:
                            intent = new Intent(MainActivity.this, AddPhrasesActivity.class);
                            startActivity(intent);
                            break;

                        case 2:
                            intent = new Intent(MainActivity.this, DisplayPhrasesActivity.class);
                            startActivity(intent);
                            break;

                        case 3:
                            intent = new Intent(MainActivity.this, EditPhraseActivity.class);
                            startActivity(intent);
                            break;

                        case 4:
                            intent = new Intent(MainActivity.this, LanguageSubscriptionActivity.class);
                            startActivity(intent);
                            break;

                        case 5:
                            intent = new Intent(MainActivity.this, TranslateActivity.class);
                            startActivity(intent);
                            break;

                        case 6:
                            intent = new Intent(MainActivity.this, DictionaryActivity.class);
                            startActivity(intent);
                            break;
                    }

                }
            });
        }
    }

}
