package iit.com.coursework2.view;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import com.ibm.watson.language_translator.v3.util.Language;

import iit.com.coursework2.R;

public class TranslateActivity extends AppCompatActivity {
    private LanguageTranslator translationService;
    TextView translatedWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        translatedWord = (TextView) findViewById(R.id.translatedWord);

        translationService = initLanguageTranslatorService();
        Log.d("abc", String.valueOf(translationService));
        new TranslationTask().execute("Hello World and my friend");
    }

    private LanguageTranslator initLanguageTranslatorService() {
        Authenticator authenticator = new IamAuthenticator("IxPhl1QKewcp3r1uvdys9D_Bgdl1SuNYI6Mzojry_4TL");
        LanguageTranslator service = new LanguageTranslator("2018-05-01", authenticator);

        service.setServiceUrl("https://api.us-south.language-translator.watson.cloud.ibm.com/instances/d5abda90-19d0-4fd6-b9ed-75f33c61d881");
        return service;
    }


    private class TranslationTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            TranslateOptions translateOptions = new TranslateOptions.Builder()
                    .addText(params[0])
                    .source(Language.ENGLISH)
                    .target("es")
                    .build();
            TranslationResult result = translationService.translate(translateOptions).execute().getResult();
            String firstTranslation = result.getTranslations().get(0).getTranslation();
            return firstTranslation;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            translatedWord.setText(s);
        }
    }

}
