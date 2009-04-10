package com.williamgrose.android.songscribbler;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SongEdit extends Activity {

    private EditText mTitleText;
    //private EditText mBodyText;
    //private EditText mChordsText;
    private int scrollspeed;
    private Long mRowId;
    private SongScribblerDbAdapter mDbHelper;
    private boolean hasSavedInstanceState = false;


    private static final int ACTIVITY_VIEW=2;
    private static final int SAVE_ID = Menu.FIRST;
    private static final int VIEW_ID = Menu.FIRST+1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new SongScribblerDbAdapter(this);
        mDbHelper.open();
        setContentView(R.layout.song_edit);
        
        mTitleText = (EditText) findViewById(R.id.title);
//        mBodyText = (EditText) findViewById(R.id.body);
//        mChordsText = (EditText) findViewById(R.id.chords);


        mRowId = (savedInstanceState != null && 
        		savedInstanceState.containsKey(SongScribblerDbAdapter.KEY_ROWID)) ? 
        		savedInstanceState.getLong(SongScribblerDbAdapter.KEY_ROWID)
        		:null;
        if (mRowId == null) {
                Bundle extras = getIntent().getExtras();
                mRowId = extras != null ? 
                		extras.getLong(SongScribblerDbAdapter.KEY_ROWID)
                		: null;
        }

        populateFields();
        
        mTitleText.setOnFocusChangeListener(editTextListener);
//        mChordsText.setOnFocusChangeListener(editTextListener);
//        mBodyText.setOnFocusChangeListener(editTextListener);
	
    }
    
    private EditText.OnFocusChangeListener editTextListener =
        new EditText.OnFocusChangeListener() {
    	
    		public void onFocusChange(View view, boolean isFocused) {
    			if(isFocused == true){
     				if((view == mTitleText) 
     						&& (mTitleText.getText().toString().equals(
     								getString(R.string.edit_title)))){
     					mTitleText.selectAll();
    				}
//     				if((view == mChordsText) 
//     						&& (mChordsText.getText().toString().equals(
//     								getString(R.string.edit_chords)))){
//     					mChordsText.selectAll();
//    				}
//     				if((view == mBodyText) 
//     						&& (mBodyText.getText().toString().equals(
//     								getString(R.string.edit_body)))){
//     					mBodyText.selectAll();
//    				}
    			}
    		}
    	};
     

    private void populateFields() {
        if (mRowId != null) {
            Cursor song = mDbHelper.fetchSong(mRowId);
            startManagingCursor(song);
            mTitleText.setText(song.getString(
                    song.getColumnIndexOrThrow(SongScribblerDbAdapter.KEY_TITLE)));
//            mBodyText.setText(song.getString(
//                    song.getColumnIndexOrThrow(SongScribblerDbAdapter.KEY_BODY)));
//            mChordsText.setText(song.getString(
//                    song.getColumnIndexOrThrow(SongScribblerDbAdapter.KEY_CHORDS)));
            scrollspeed = song.getInt(
                    song.getColumnIndexOrThrow(SongScribblerDbAdapter.KEY_SCROLLSPEED));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	hasSavedInstanceState = true;
    	saveState();
    	if(mRowId != null){
    		outState.putLong(SongScribblerDbAdapter.KEY_ROWID, mRowId);
    	}
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!hasSavedInstanceState){
        	saveState();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SAVE_ID, 0, R.string.menu_save);
        menu.add(0, VIEW_ID, 0,  R.string.menu_view);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case SAVE_ID:
            setResult(RESULT_OK);
            finish();
            return true;
        case VIEW_ID:
            viewSong();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void viewSong() {
        saveState();
        Intent i = new Intent(this, SongView.class);
        i.putExtra(SongScribblerDbAdapter.KEY_ROWID, mRowId);
        startActivityForResult(i, ACTIVITY_VIEW);
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
//        String body = mBodyText.getText().toString();
//        String chords = mChordsText.getText().toString();
        Boolean isSongEdited = !(title.equals(getString(R.string.edit_title))
//				&& body.equals(getString(R.string.edit_body))
//				&& chords.equals(getString(R.string.edit_chords))
				);
        if(isSongEdited){
	        if (mRowId == null) {
	    		long id = mDbHelper.createSong(title, "",
					"", SongScribblerDbAdapter.DEFAULT_SCROLLSPEED);    		
	    		if (id > 0) {
	    			mRowId = id;
	    		}
	        } else {
	            mDbHelper.updateSong(mRowId, title, "", "", scrollspeed);
	        }
        }
    }

}
