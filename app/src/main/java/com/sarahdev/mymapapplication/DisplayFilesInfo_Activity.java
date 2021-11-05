package com.sarahdev.mymapapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.MapView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DisplayFilesInfo_Activity extends AppCompatActivity {
    public final static String EXTRA_FILESINFOS = "com.sarahdev.mymapapplication.checkedfilesinfos";
    public final static String EXTRA_CHECKEDFILENAMES = "com.sarahdev.mymapapplication.checkedfilenames";
    public final static String EXTRA_DELETEDFILENAMES = "com.sarahdev.mymapapplication.deletedfilenames";
    private Map <String, List<String>> fileInfos = new HashMap <> ();
    public  List<String> checkedFilenames = new ArrayList<>();
    private List<String> deletedFilenames = new ArrayList<>();

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    protected final static String TAG_DELETE = "toDelete";
    protected final static String TAG_DISPLAY = "toDisplay";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        Intent intent = getIntent();
        checkedFilenames = (List<String>) intent.getSerializableExtra(EXTRA_CHECKEDFILENAMES);
        fileInfos = (Map <String, List<String>>) intent.getSerializableExtra(EXTRA_FILESINFOS);

        listView = findViewById(R.id.listView);
        listDataChild = new HashMap<>(fileInfos);
        listDataHeader = new ArrayList<>(fileInfos.keySet());
        Collections.sort(listDataHeader, Collections.reverseOrder());
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        listView.setAdapter(listAdapter);

        for (String name : checkedFilenames) {
            int i = listDataHeader.indexOf(name);
            listView.expandGroup(i);
        }
    }

    public void setChanges(int position, boolean addToList, String TAG) {
        switch (TAG) {
            case TAG_DELETE :
                if (addToList)
                    listView.collapseGroup(position);
                changeFileList(addToList, deletedFilenames, listDataHeader.get(position));
                break;
            case TAG_DISPLAY:
                changeFileList(addToList, checkedFilenames, listDataHeader.get(position));
                break;
        }
    }

    private void changeFileList(boolean addToList, List<String> fileList, String filename) {
        if (addToList) {
            if (!fileList.contains(filename)) {
                fileList.add(filename);
            }
        } else {
            if (fileList.contains(filename)) {
                fileList.remove(filename);
            }
        }
    }


/*    public void toDeleteFile(int position, boolean toDelete) {
        if (toDelete)
            listView.collapseGroup(position);
        if (toDelete) {
            if (!deletedFilenames.contains(listDataHeader.get(position))) {
                deletedFilenames.add(listDataHeader.get(position));
            }
        } else {
            if (deletedFilenames.contains(listDataHeader.get(position))) {
                deletedFilenames.remove((Object) listDataHeader.get(position));
            }
        }
    }

    public void isCheckedFile(int position, boolean isChecked) {
        if (isChecked) {
            if (!checkedFilenames.contains(listDataHeader.get(position))) {
                checkedFilenames.add(listDataHeader.get(position));
            }
        } else {
            if (checkedFilenames.contains(listDataHeader.get(position))) {
                checkedFilenames.remove(listDataHeader.get(position));
            }
        }
     }
*/
    public void envoyer(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_CHECKEDFILENAMES, (Serializable) checkedFilenames);
        resultIntent.putExtra(EXTRA_DELETEDFILENAMES, (Serializable) deletedFilenames);
        resultIntent.putExtra(EXTRA_FILESINFOS, (Serializable) fileInfos);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
