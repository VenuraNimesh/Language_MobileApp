package iit.com.coursework2.view;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.cloud.sdk.core.service.exception.RequestTooLargeException;
import com.ibm.cloud.sdk.core.service.exception.ServiceResponseException;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.language_translator.v3.util.Language;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import iit.com.coursework2.R;
import iit.com.coursework2.controller.LanguageController;
import iit.com.coursework2.controller.PhraseController;
import iit.com.coursework2.model.LanguageModel;


public class TranslateActivity extends AppCompatActivity {
    LanguageController languageController = new LanguageController(this);
    PhraseController phraseController = new PhraseController(this);
    ArrayList<LanguageModel> languageObjArray = new ArrayList<>();
    private LanguageTranslator translationService;

    private StreamPlayer player = new StreamPlayer();
    private TextToSpeech textService;

    TextView translatedWord;
    ListView phrasesListView;
    Spinner dropDown;
    Button btnTanslate, btnTranslateAll;
    ImageButton btnSpeaker;
    String selectedWord;
    String selectedLangCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        phrasesListView = (ListView) findViewById(R.id.phrasesListView);
        translatedWord = (TextView) findViewById(R.id.translatedWord);
        dropDown = (Spinner) findViewById(R.id.spinner);
        btnTanslate = (Button) findViewById(R.id.translate);
        btnTranslateAll = (Button) findViewById(R.id.translateAll);
        btnSpeaker = (ImageButton) findViewById(R.id.speakerButton);

        displayPhrases();
        subscribedLanguages();
        onClickTranslate();
        onClickPronounce();

    }

    private void displayPhrases() {
        Cursor data = phraseController.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {

            //get the value from database in col 1
            //Add it to the list
            listData.add(data.getString(1));
        }
        Collections.sort(listData);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);

        phrasesListView.setAdapter(arrayAdapter);

        phrasesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arrayAdapter, View view, int position, long itemId) {

                selectedWord = (String) phrasesListView.getItemAtPosition(position);

            }
        });
    }

    private void subscribedLanguages() {
        Cursor data = languageController.getAllLanguages();
        while (data.moveToNext()) {
            String id = data.getString(0);
            String langCode = data.getString(1);
            String langName = data.getString(2);
            Integer subscribed = data.getInt(3);

            LanguageModel languageModel = new LanguageModel(id, langCode, langName, subscribed);
            languageObjArray.add(languageModel);
        }

        if (languageObjArray.size() != 0) {
            ArrayList<String> subLanguageList = new ArrayList<>();
            for (int i = 0; i < languageObjArray.size(); i++) {
                if (languageObjArray.get(i).getSubscribed() == 1) {
                    subLanguageList.add(languageObjArray.get(i).getLang_name());
                }
            }
            if (subLanguageList.size() != 0) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(TranslateActivity.this, android.R.layout.simple_list_item_1, subLanguageList);
                dropDown.setAdapter(adapter);

                dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        String selectedLanguage = adapterView.getItemAtPosition(position).toString();

                        for (int i = 0; i < languageObjArray.size(); i++) {
                            if (languageObjArray.get(i).getLang_name() == selectedLanguage) {
                                selectedLangCode = languageObjArray.get(i).getLang_code();
                                Log.d("selectedLangCode", selectedLangCode);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        Toast.makeText(TranslateActivity.this, "No language selected", Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(TranslateActivity.this, "You have not subscribed any Language", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(TranslateActivity.this, "Check your internet connection and subscribed Languages", Toast.LENGTH_SHORT).show();
    }

    private void onClickTranslate() {
        btnTanslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check for internet connection
                if (checkInternetConnection()) {
                    if ((selectedLangCode != null) && (selectedWord != null)) {
                        translationService = initLanguageTranslatorService();

                        new TranslationTask().execute(selectedWord, selectedLangCode);
                    } else {
                        Toast.makeText(TranslateActivity.this, "Select a word and a language to translate", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(TranslateActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private LanguageTranslator initLanguageTranslatorService() {
        Authenticator authenticator = new IamAuthenticator(getString(R.string.translateApiKey));
        LanguageTranslator service = new LanguageTranslator("2018-05-01", authenticator);

        service.setServiceUrl(getString(R.string.translateUrl));

        return service;
    }

    private class TranslationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String firstTranslation = null;
            try {
                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .addText(params[0])
                        .source(Language.ENGLISH)
                        .target(params[1])
                        .build();
                TranslationResult result = translationService.translate(translateOptions).execute().getResult();
                firstTranslation = result.getTranslations().get(0).getTranslation();
                return firstTranslation;

            } catch (ServiceResponseException e) {
                Log.d("errorrr", e.getStatusCode() + e.getMessage());
                return String.valueOf(e.getStatusCode());
            } catch (Resources.NotFoundException e) {
                Log.d("errorr", e.getMessage());
                return String.valueOf(e);
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("404")) {
                Toast.makeText(TranslateActivity.this, "Selected Language is Currently Unavailable", Toast.LENGTH_SHORT).show();
            } else
                translatedWord.setText(result);

        }
    }

    private void onClickPronounce() {
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check for internet connection
                if (checkInternetConnection()) {
                    textService = initTextToSpeechService();

                    if (translatedWord.getText() != null) {
                        new SynthesisTask().execute(String.valueOf(translatedWord.getText().toString()));
                    } else
                        Toast.makeText(TranslateActivity.this, "Please Translate Again", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(TranslateActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private TextToSpeech initTextToSpeechService() {
        Authenticator authenticator = new IamAuthenticator(getString(R.string.speechApiKey));
        TextToSpeech service = new TextToSpeech(authenticator);
        service.setServiceUrl(getString(R.string.speechUrl));
        return service;
    }

    private class SynthesisTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                        .text(params[0])
                        .voice(SynthesizeOptions.Voice.EN_US_LISAV3VOICE)
                        .accept(HttpMediaType.AUDIO_WAV)
                        .build();

                player.playStream(textService.synthesize(synthesizeOptions).execute().getResult());

            } catch (Resources.NotFoundException e) {
                Log.d("errorr", e.getMessage());
            } catch (ServiceResponseException e) {
                Log.d("errorrr", e.getStatusCode() + e.getMessage());
            }
            return "Done";
        }
    }

}
