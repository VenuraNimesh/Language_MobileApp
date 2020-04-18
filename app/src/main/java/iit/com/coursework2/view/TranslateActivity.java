package iit.com.coursework2.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import java.util.List;
import java.util.concurrent.TimeoutException;

import iit.com.coursework2.R;
import iit.com.coursework2.controller.LanguageController;
import iit.com.coursework2.controller.PhraseController;
import iit.com.coursework2.model.DictionaryModel;
import iit.com.coursework2.model.LanguageModel;
import iit.com.coursework2.model.PhraseModel;


public class TranslateActivity extends AppCompatActivity {
    LanguageController languageController = new LanguageController(this);
    PhraseController phraseController = new PhraseController(this);
    ArrayList<LanguageModel> languageObjArray = new ArrayList<>();
    private LanguageTranslator translationService;
    ArrayList<PhraseModel> phraseObjArray = new ArrayList<>();

    private StreamPlayer player = new StreamPlayer();
    private TextToSpeech textService;

    TextView translatedWord;
    ListView phrasesListView;
    Spinner dropDown;
    Button btnTranslate, btnTranslateAll;
    ImageView btnSpeaker;
    String selectedWord;
    String selectedLangCode;
    String selectedLangID;
    String selectedLangName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        phrasesListView = (ListView) findViewById(R.id.phrasesListView);
        translatedWord = (TextView) findViewById(R.id.translatedWord);
        dropDown = (Spinner) findViewById(R.id.spinner);
        btnTranslate = (Button) findViewById(R.id.translate);
        btnTranslateAll = (Button) findViewById(R.id.translateAll);
        btnSpeaker = (ImageView) findViewById(R.id.speakerButton);

        displayPhrases();
        subscribedLanguages();
        onClickTranslate();
        onClickTranslateAll();
        onClickPronounce();

    }

    private void displayPhrases() {
        Cursor data = phraseController.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            Integer id = data.getInt(0);
            String phrase = data.getString(1);

            PhraseModel phraseModel = new PhraseModel(id, phrase);
            phraseObjArray.add(phraseModel);

            listData.add(phrase);
        }
        Collections.sort(listData);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, listData);

        phrasesListView.setAdapter(arrayAdapter);

        phrasesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arrayAdapter, View view, int position, long itemId) {

                //Get the selected word/phrase
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
                    //Add the subscribed languages to the dropdown
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

                        translatedWord.setText("");

                        for (int i = 0; i < languageObjArray.size(); i++) {
                            if (languageObjArray.get(i).getLang_name().equals(selectedLanguage)) {
                                selectedLangCode = languageObjArray.get(i).getLang_code();
                                selectedLangID = languageObjArray.get(i).getID();
                                selectedLangName = languageObjArray.get(i).getLang_name();

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
        btnTranslate.setOnClickListener(new View.OnClickListener() {
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

    private void onClickTranslateAll() {
        btnTranslateAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check for internet connection
                if (checkInternetConnection()) {
                    if (selectedLangCode != null && phraseObjArray.size() != 0) {

                        //Get list of phrases from the object
                        ArrayList<String> phrasesList = new ArrayList<>();
                        for (int i = 0; i < phraseObjArray.size(); i++) {
                            phrasesList.add(phraseObjArray.get(i).getPhrase());
                        }

                        translationService = initLanguageTranslatorService();

                        new TranslateAllTask().execute(phrasesList);

                    } else {
                        Toast.makeText(TranslateActivity.this, "Select a Language to Translate", Toast.LENGTH_SHORT).show();
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
        private ProgressDialog dialog = new ProgressDialog(TranslateActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please Wait");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .addText(params[0])
                        .source(Language.ENGLISH)
                        .target(params[1])
                        .build();
                TranslationResult result = translationService.translate(translateOptions).execute().getResult();
                String translatedWord = result.getTranslations().get(0).getTranslation();
                return translatedWord;

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
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result.equals("404") || result.equals("503")) {
                Toast.makeText(TranslateActivity.this, "Selected Language is Currently Unavailable", Toast.LENGTH_SHORT).show();
            } else {
                translatedWord.setVisibility(View.VISIBLE);
                translatedWord.setText(result);
            }
        }
    }

    private class TranslateAllTask extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {
        private ProgressDialog dialog = new ProgressDialog(TranslateActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please Wait");
            this.dialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... params) {
            ArrayList<String> translatedList = new ArrayList<>();

            try {
                TranslateOptions translateOptions = new TranslateOptions.Builder()
                        .text(params[0])
                        .source(Language.ENGLISH)
                        .target(selectedLangCode)
                        .build();
                TranslationResult result = translationService.translate(translateOptions).execute().getResult();

                for (int i = 0; i < result.getTranslations().size(); i++) {
                    String translatedPhrase = result.getTranslations().get(i).getTranslation();

                    translatedList.add(translatedPhrase);
                }

            } catch (ServiceResponseException e) {
                Log.d("errorrr", e.getStatusCode() + e.getMessage());

            } catch (Resources.NotFoundException e) {
                Log.d("errorr", e.getMessage());
            }
            return translatedList;

        }

        @Override
        protected void onPostExecute(ArrayList<String> resultList) {
            super.onPostExecute(resultList);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (resultList.size() == 0) {
                Toast.makeText(TranslateActivity.this, "Selected Language is Currently Unavailable", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(TranslateActivity.this, "Saved to Dictionary", Toast.LENGTH_SHORT).show();
                translatedListToDB(resultList);
            }


        }
    }

    private void translatedListToDB(ArrayList<String> translatedList) {
        if (phraseObjArray.size() != 0 && selectedLangID != null && translatedList.size() != 0) {
            String langID = selectedLangID;
            String langName = selectedLangName;

            for (int i = 0; i < phraseObjArray.size(); i++) {
                String phraseID = String.valueOf(phraseObjArray.get(i).getID());
                String phraseName = phraseObjArray.get(i).getPhrase();
                String translatedWord = translatedList.get(i);

                DictionaryModel dictionaryModel = new DictionaryModel(langID, phraseID, phraseName, translatedWord, langName);

                //Add dictionary models to the database
                languageController.translateAllPhrases(dictionaryModel);
            }

        }
    }

    private void onClickPronounce() {
        btnSpeaker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Check for internet connection
                if (checkInternetConnection()) {
                    textService = initTextToSpeechService();

                    if (translatedWord.getText().toString().matches("")) {
                        Toast.makeText(TranslateActivity.this, "Please Translate a Word First", Toast.LENGTH_SHORT).show();

                    } else {
                        new SynthesisTask().execute(String.valueOf(translatedWord.getText().toString()));
                    }

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
        private ProgressDialog dialog = new ProgressDialog(TranslateActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please Wait");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                SynthesizeOptions synthesizeOptions = new SynthesizeOptions.Builder()
                        .text(params[0])
                        .voice(SynthesizeOptions.Voice.EN_US_LISAV3VOICE)
                        .accept(HttpMediaType.AUDIO_WAV)
                        .build();

                player.playStream(textService.synthesize(synthesizeOptions).execute().getResult());
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            } catch (Resources.NotFoundException e) {
                Log.d("errorr", e.getMessage());
                Toast.makeText(TranslateActivity.this, "Internal Error Try Again", Toast.LENGTH_SHORT).show();
            } catch (ServiceResponseException e) {
                Log.d("errorrr", e.getStatusCode() + e.getMessage());
                Toast.makeText(TranslateActivity.this, "Internal Error Try Again", Toast.LENGTH_SHORT).show();
            }
            return "Done";
        }

    }

}
