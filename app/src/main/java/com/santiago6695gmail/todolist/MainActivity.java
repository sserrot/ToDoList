package com.santiago6695gmail.todolist;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    private EditText enterEdit;
    ArrayList<String> arrayList = new ArrayList<String>();
    private ArrayAdapter adapter;
    int pos=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

         enterEdit = (EditText) findViewById(R.id.enter);

        final ListView listView = (ListView) findViewById(R.id.listview);
        // get position of whatever textView you click in the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = listView.getItemAtPosition(position);
                enterEdit.setText(listItem.toString());
                pos = listView.getPositionForView(view);
            }

        });
        //set the textview layout
        adapter = new ArrayAdapter(this, R.layout.listview_content,arrayList);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
//set up the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                return true;

            case R.id.close:
                finish();
                return true;
// add whatever the edittext has to the arrayList which is passed to the listview through the adapter
            case R.id.add:
                arrayList.add(enterEdit.getText().toString());
                int num = arrayList.indexOf(enterEdit.getText().toString()) + 1;
                arrayList.remove(enterEdit.getText().toString());
                arrayList.add("" + num + "." + enterEdit.getText().toString());
                adapter.notifyDataSetChanged();
                return true;
// search for what was typed and then remove it .. NOTE: I could not figure out how to update the numbers in the list
            case R.id.delete:
                if(arrayList.contains(enterEdit.getText().toString()))
                {
                    int index = arrayList.indexOf(enterEdit.getText().toString());
                    arrayList.remove(index);
                }
                adapter.notifyDataSetChanged();
                return true;
// update whatever was input into the position of what was clicked
            case R.id.update:
                arrayList.remove(pos);
                arrayList.add(pos, "" + (pos + 1) + "." + enterEdit.getText().toString());
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
