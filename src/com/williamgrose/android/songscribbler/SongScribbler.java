package com.williamgrose.android.songscribbler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import net.routengn.*;

public class SongScribbler extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private SongScribblerDbAdapter mDbHelper;
    private String[] labels;
    private ArrayList<HashMap<String, String>> al;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.songs_list);
        //ListView od = (ListView)findViewById(android.R.id.list);
        //od.setDividerHeight(15); 
        mDbHelper = new SongScribblerDbAdapter(this);
        mDbHelper.open();
        fillData();
        //createSong();
    }

    private void fillData() {
        Cursor songsCursor = mDbHelper.fetchAllSongs();
        startManagingCursor(songsCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{SongScribblerDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.textRow};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter songs =
                    new SimpleCursorAdapter(this, R.layout.songs_row, songsCursor, from, to);
        //setListAdapter(songs);
        
        Carrier carrier = new Carrier();
        al = carrier.listCarriers();
        labels = new String[al.size()];
        
        for(int i=0; i<al.size(); i++) {
        	labels[i] = al.get(i).get("name");
        }
        
        setListAdapter(new SimpleAdapter(this, getData(labels),
        		R.layout.songs_row, new String[]{"title"}, new int[]{R.id.textRow}));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        menu.add(0, DELETE_ID, 0,  R.string.menu_delete);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case INSERT_ID:
            createSong();
            return true;
        case DELETE_ID:
            mDbHelper.deleteSong(getListView().getSelectedItemId());
            fillData();
            return true;
        }
 
        return super.onMenuItemSelected(featureId, item);
    }

    private void createSong() {
        Intent i = new Intent(this, SongEdit.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, SongEdit.class);
        i.putExtra(SongScribblerDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
    
    private List<Map<String, Object>> getData(String[] labels) {
        List<Map<String, Object>> list = new
        ArrayList<Map<String,Object>>();
        for (String label : labels) {
                list.add(getMap(label));
        }
        return list;
    }
    
    private Map<String, Object> getMap(String label) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", label);
        return map;
    } 
}
