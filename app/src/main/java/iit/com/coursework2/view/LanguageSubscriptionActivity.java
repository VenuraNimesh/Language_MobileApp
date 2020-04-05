package iit.com.coursework2.view;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import iit.com.coursework2.R;
import iit.com.coursework2.controller.LanguageController;
import iit.com.coursework2.model.Language;

public class LanguageSubscriptionActivity extends AppCompatActivity {
    private static LanguageTranslator languageTranslator;
    ListView listViewLan;
    Button btnUpdate;
    LanguageController languageController = new LanguageController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_subscription);

        listViewLan = (ListView) findViewById(R.id.listViewLang);
        btnUpdate = (Button) findViewById(R.id.update);

        //Check whether language table has data
        Integer dataCount = languageController.checkLanguageTable();
        Log.d("counttttt", String.valueOf(dataCount));
        if (dataCount < 1) {

            //Check for internet connection
            if (checkInternetConnection()) {
                IamAuthenticator authenticator = new IamAuthenticator("IxPhl1QKewcp3r1uvdys9D_Bgdl1SuNYI6Mzojry_4TL");
                languageTranslator = new LanguageTranslator("2018-05-01", authenticator);
                languageTranslator.setServiceUrl("https://api.us-south.language-translator.watson.cloud.ibm.com/instances/d5abda90-19d0-4fd6-b9ed-75f33c61d881");

                new GetLanguages().execute();
            } else {
                Toast.makeText(LanguageSubscriptionActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        listViewWithCheckBoxes();
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class GetLanguages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            IdentifiableLanguages languages = languageTranslator.listIdentifiableLanguages().execute().getResult();
            String languagesJson = String.valueOf(languages);

            if (languagesJson != null) {
                addLanguagesToDB(languagesJson);
            } else {
                Toast.makeText(LanguageSubscriptionActivity.this, "Something went wrong, Please Try Again", Toast.LENGTH_SHORT).show();
            }

            return languagesJson;
        }

        private void addLanguagesToDB(String allLanguagesJson) {
            Log.d("cba", allLanguagesJson);

            try {
                JSONObject jsonObject = new JSONObject(allLanguagesJson);

                JSONArray languages = jsonObject.getJSONArray("languages");

                for (int i = 0; i < languages.length(); i++) {
                    JSONObject lang = languages.getJSONObject(i);

                    String code = lang.getString("language");
                    String name = lang.getString("name");
                    Integer subscribed = 0;

                    Language language = new Language(code, name, subscribed);
                    languageController.addAllLanguages(language);
                }

            } catch (final JSONException e) {
                Toast.makeText(LanguageSubscriptionActivity.this, "Something went wrong, Please Try Again", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void listViewWithCheckBoxes() {
        Cursor data = languageController.getAllLanguages();
        ArrayList<String> listLanguages = new ArrayList<>();
        while (data.moveToNext()) {
            //get the value from database in col 2
            //Add it to the list
            listLanguages.add(data.getString(2));
        }
        Collections.sort(listLanguages);

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, listLanguages);

        listViewLan.setAdapter(arrayAdapter);

    }


}
