package com.example.clone_youtube;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


import java.util.List;


public class MainActivity extends AppCompatActivity {
    private YoutubeAdapter youtubeAdapter;
    private EditText searchInput;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private Handler handler;
    private List<Video> searchResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mProgressDialog = new ProgressDialog(this);
        searchInput = (EditText)findViewById(R.id.search_input);
        mRecyclerView = (RecyclerView) findViewById(R.id.videos_recycler_view);
        mProgressDialog.setTitle("Searching...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        handler = new Handler();

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                if(actionId == EditorInfo.IME_ACTION_SEARCH){

                    mProgressDialog.setMessage("Finding videos for "+v.getText().toString());
                    mProgressDialog.show();
                    searchOnYoutube(v.getText().toString());
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    return false;
                }
                return true;
            }
        });

    }
    private void searchOnYoutube(final String keywords){
        new Thread(){

            public void run(){
                Connector yc = new Connector(MainActivity.this);
                searchResults = yc.search(keywords);handler.post(new Runnable(){
                    public void run(){
                        fillYoutubeVideos();
                        mProgressDialog.dismiss();
                    }
                });
            }
        }.start();
    }


    private void fillYoutubeVideos(){
        youtubeAdapter = new YoutubeAdapter(getApplicationContext(),searchResults);
        mRecyclerView.setAdapter(youtubeAdapter);
        youtubeAdapter.notifyDataSetChanged();
    }
}
