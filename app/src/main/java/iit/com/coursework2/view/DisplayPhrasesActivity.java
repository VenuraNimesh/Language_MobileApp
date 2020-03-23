package iit.com.coursework2.view;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import iit.com.coursework2.R;
import iit.com.coursework2.model.DatabaseHelper;

public class DisplayPhrasesActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_phrases);

        listView = (ListView) findViewById(R.id.listView);
        databaseHelper = new DatabaseHelper(this);

        displayListView();
    }

    private void displayListView() {

        Cursor data = databaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            //get the value from database in col 1
            //Add it to the list
            listData.add(data.getString(1));
        }
        Collections.sort(listData);
        ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);

        listView.setAdapter(listAdapter);
    }
}
