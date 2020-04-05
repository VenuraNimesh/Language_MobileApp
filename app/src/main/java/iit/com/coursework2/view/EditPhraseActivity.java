package iit.com.coursework2.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import iit.com.coursework2.R;
import iit.com.coursework2.controller.PhraseController;

public class EditPhraseActivity extends AppCompatActivity {

    ListView listView;
    Button btnEdit, btnSave;
    EditText editText;
    String selectedValue;
    int phraseID;
    PhraseController phraseController = new PhraseController(this);
    ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phrase);

        listView = (ListView) findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.editText);
        btnEdit = (Button) findViewById(R.id.edit);
        btnSave = (Button) findViewById(R.id.save);

        displayWithRadioButtons();
        onclickEditButton();
        onclickSaveButton();
    }

    private void displayWithRadioButtons() {

        Cursor data = phraseController.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()) {
            //get the value from database in col 1
            //Add it to the list
            listData.add(data.getString(1));
        }
        Collections.sort(listData);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, listData);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listAdapter, View view, int position, long itemId) {
                CheckedTextView textView;

                selectedValue = (String) listView.getItemAtPosition(position);

                listView.invalidate();
                textView = (CheckedTextView) view;
                if (textView != null) {
                    textView.setTextColor(Color.BLUE);
                }
            }
        });
    }

    private void onclickEditButton() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedValue != null) {
                    editText.setText(selectedValue);

                    Cursor data = phraseController.getPhraseID(selectedValue);
                    while (data.moveToNext()) {
                        phraseID = data.getInt(0);
                    }
                }
            }
        });
    }

    private void onclickSaveButton() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editPhrase = editText.getText().toString();

                if (editPhrase.length() != 0) {
                    String newPhrase = editPhrase.substring(0, 1).toUpperCase() + editPhrase.substring(1);

                    boolean isUpdated = phraseController.updateData(newPhrase, String.valueOf(phraseID));

                    if (isUpdated) {
                        Toast.makeText(EditPhraseActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        arrayAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(EditPhraseActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    editText.setText("");
                } else {
                    Toast.makeText(EditPhraseActivity.this, "Cannot be blank", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}

