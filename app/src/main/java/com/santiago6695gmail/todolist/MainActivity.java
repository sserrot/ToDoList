package com.santiago6695gmail.todolist;



import android.annotation.TargetApi;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnInitListener{

    private String text = "";
    private EditText enterEdit;
    private ArrayAdapter adapter;
    private OutputStreamWriter out;
    private TextToSpeech speaker;
    private static final String tag = "ToDo";
    int pos = 0;
    ArrayList<String> arrayList = new ArrayList<>();
    File file;


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
        adapter = new ArrayAdapter(this, R.layout.listview_content, arrayList);
        listView.setAdapter(adapter);

        // check for file, if exists, write to listview
        //open stream for reading from file
        file = getFileStreamPath("list.txt");
        if (file.exists()) {
            try {
                InputStream in = openFileInput("list.txt");
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader reader = new BufferedReader(isr);
                while ((text = reader.readLine()) != null) {
                    arrayList.add(text);
                    adapter.notifyDataSetChanged();
                }
                reader.close();
            } catch (IOException e) {
                Log.e(tag, e.getMessage());
            }
        }

        //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech(this, this);
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
                save();
                return true;
            case R.id.close:
                save();
                finish();
                return true;
// add whatever the edittext has to the arrayList which is passed to the listview through the adapter
            case R.id.add:
                add();
                return true;
// search for what was typed and then remove it .. NOTE: I could not figure out how to update the numbers in the list
            case R.id.delete:
                delete();
                return true;
// update whatever was input into the position of what was clicked
            case R.id.update:
                update();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //speaks the contents of output
    @TargetApi(21)
    public void speak(String output){
        //	speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null);  //for APIs before 21
        speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null, "Id 0");
    }
    // Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // If a language is not be available, the result will indicate it.
            int result = speaker.setLanguage(Locale.US);

            //  int result = speaker.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language data is missing or the language is not supported.
                Log.e(tag, "Language is not available.");
            } else {
                // The TTS engine has been successfully initialized
                Log.i(tag, "TTS Initialization successful.");
            }
        } else {
            // Initialization failed.
            Log.e(tag, "Could not initialize TextToSpeech.");
        }
    }

    public void save() {
        try {
            //open output stream
            if (!file.exists()) {
                file.createNewFile();
            }
            try {
                out = new OutputStreamWriter(openFileOutput("list.txt", MODE_PRIVATE)); // also try MODE_APPEND
            } catch (IOException e) {}
            // iterates through array and writes to a .txt file
            for (int i = 0; i < arrayList.size(); i++) {
                text = arrayList.get(i);
                out.write(text + "\n");
                out.flush();
                Log.e(tag,text);
            }
            //close writer
            out.close();
            enterEdit.setText("");
        } catch (IOException e) {
            Log.e(tag, e.getMessage());
        }
    }
    public void add() {
        speak("adding " + enterEdit.getText().toString());
        arrayList.add(enterEdit.getText().toString());
        int num = arrayList.indexOf(enterEdit.getText().toString()) + 1;
        arrayList.remove(enterEdit.getText().toString());
        arrayList.add("" + num + "." + enterEdit.getText().toString());
        adapter.notifyDataSetChanged();
        enterEdit.setText("");
    }
    public void delete() {
        String fix;
        speak("deleting" + enterEdit.getText().toString());
        if (arrayList.contains(enterEdit.getText().toString())) {
            int index = arrayList.indexOf(enterEdit.getText().toString());
            arrayList.remove(index);
        }
        // iterates through arraylist and updates the position of the string in array
        for (int i = 0; i < arrayList.size(); i++)
        {
            fix = arrayList.get(i);
            fix = fix.substring(fix.indexOf("."));
            fix = (i + 1) + fix;
            arrayList.remove(i);
            arrayList.add(i, fix);
        }
        adapter.notifyDataSetChanged();
        enterEdit.setText("");
    }
    public void update() {
        arrayList.remove(pos);
        arrayList.add(pos, "" + (pos + 1) + "." + enterEdit.getText().toString());
        adapter.notifyDataSetChanged();
        enterEdit.setText("");
    }

}
