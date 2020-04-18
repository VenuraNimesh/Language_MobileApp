package iit.com.coursework2.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.IdentifiableLanguages;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import iit.com.coursework2.R;
import iit.com.coursework2.controller.LanguageController;
import iit.com.coursework2.model.LanguageModel;

public class LanguageSubscriptionActivity extends AppCompatActivity {
    private static LanguageTranslator languageTranslator;
    ListView listViewLan;
    Button btnUpdate;
    LanguageController languageController = new LanguageController(this);
    ArrayList<LanguageModel> languageObjArray = new ArrayList<>();
    HashMap<String, Integer> subscribedHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_subscription);

        listViewLan = (ListView) findViewById(R.id.listViewLang);
        btnUpdate = (Button) findViewById(R.id.update);

        //Check whether language table has data
        Integer dataCount = languageController.checkLanguageTable();

        if (dataCount < 1) {

            //Check for internet connection
            if (checkInternetConnection()) {
                IamAuthenticator authenticator = new IamAuthenticator(getString(R.string.translateApiKey));
                languageTranslator = new LanguageTranslator("2018-05-01", authenticator);
                languageTranslator.setServiceUrl(getString(R.string.translateUrl));

                new GetLanguages().execute();
            } else {
                Toast.makeText(LanguageSubscriptionActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        listViewWithCheckBoxes();
        onClickUpdate();
    }


    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class GetLanguages extends AsyncTask<String, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(LanguageSubscriptionActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please Wait");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            IdentifiableLanguages languages = languageTranslator.listIdentifiableLanguages().execute().getResult();
            String languagesJson = String.valueOf(languages);

            if (languagesJson != null) {
                Boolean completed = addLanguagesToDB(languagesJson);
                if (completed) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            listViewWithCheckBoxes();
                        }
                    });
                }

            } else {
                Toast.makeText(LanguageSubscriptionActivity.this, "Something went wrong, Please Try Again", Toast.LENGTH_SHORT).show();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            return languagesJson;
        }

        private Boolean addLanguagesToDB(String allLanguagesJson) {

            try {
                JSONObject jsonObject = new JSONObject(allLanguagesJson);

                JSONArray languages = jsonObject.getJSONArray("languages");

                for (int i = 0; i < languages.length(); i++) {
                    JSONObject lang = languages.getJSONObject(i);

                    String code = lang.getString("language");
                    String name = lang.getString("name");
                    Integer subscribed = 0;

                    LanguageModel language = new LanguageModel(code, name, subscribed);

                    //Adding languages to the database
                    languageController.addAllLanguages(language);
                }


            } catch (final JSONException e) {
                Toast.makeText(LanguageSubscriptionActivity.this, "Something went wrong, Please Try Again", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

    }

    private void listViewWithCheckBoxes() {
        Cursor data = languageController.getAllLanguages();
        ArrayList<String> listLanguages = new ArrayList<>();
        ArrayList<Integer> checkedList = new ArrayList<>();
        while (data.moveToNext()) {
            String id = data.getString(0);
            String langCode = data.getString(1);
            String langName = data.getString(2);
            Integer subscribed = data.getInt(3);

            LanguageModel languageModel = new LanguageModel(id, langCode, langName, subscribed);
            languageObjArray.add(languageModel);

            listLanguages.add(langName);
            int checkedValue = data.getInt(3);

            if (checkedValue == 1) {
                checkedList.add(data.getInt(0));
            }
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, listLanguages);

        listViewLan.setAdapter(arrayAdapter);

        //Set the list view for the previously subscribed languages
        for (Integer position : checkedList) {
            listViewLan.setItemChecked(position - 1, true);
        }

        listViewLan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long lanId) {
                CheckedTextView item = (CheckedTextView) view;
                String selectedLanguage = (String) listViewLan.getItemAtPosition(position);

                for (int i = 0; i < languageObjArray.size(); i++) {
                    if (languageObjArray.get(i).getLang_name().equals(selectedLanguage)) {

                        //Changing subscription
                        if (item.isChecked()) {
                            languageObjArray.get(i).setSubscribed(1);
                            subscribedHashMap.put(languageObjArray.get(i).getID(), 1);
                        } else {
                            languageObjArray.get(i).setSubscribed(1);
                            subscribedHashMap.put(languageObjArray.get(i).getID(), 0);
                        }

                    }
                }
            }
        });

    }

    private void onClickUpdate() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subscribedHashMap != null) {
                    boolean updated = languageController.updateSubscription(subscribedHashMap);

                    if (updated) {
                        Toast.makeText(LanguageSubscriptionActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(LanguageSubscriptionActivity.this, "Something went wrong,Try again", Toast.LENGTH_SHORT).show();

                } else
                    Toast.makeText(LanguageSubscriptionActivity.this, "Nothing to Update", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

//https://cloud.ibm.com/apidocs/language-translator/language-translator?code=java
//Tutorial 6