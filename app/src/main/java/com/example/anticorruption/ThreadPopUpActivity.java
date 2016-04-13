package com.example.anticorruption;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadPopUpActivity extends AppCompatActivity {

    String title, content, username, thread, previous, up_votes, down_votes, date;
    Integer id;

    DatabaseHelper dh = new DatabaseHelper(this);
    private android.support.v7.widget.Toolbar toolbar;

    Context context = this;

    UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(context);

    Boolean isFavorite;

    ProgressBar progressBar;

    ListView threadListView;
    List<Bundle> listData;
    ListAdapterThread listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pop_up);

        context = this;

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        threadListView = (ListView) findViewById(R.id.threadLv);

        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bundle bundle = getIntent().getExtras();
                thread = bundle.getString("THREAD");
                id = dh.getPost(PostTable.THREAD, thread).getInt("ID");

                prepareListData();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getSupportActionBar().setTitle("Forum Thread");

                        if (!thread.contains("Story")) {
                            getSupportActionBar().setSubtitle(thread);
                            getSupportActionBar().setDisplayShowTitleEnabled(true);
                        }

                        listAdapter = new ListAdapterThread(context, listData);
                        threadListView.setAdapter(listAdapter);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thread, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        isFavorite = dh.isUserFollowing(UserTable.POSTS_FOLLOWING, UserData.username, id);
        if (isFavorite) {
            menu.findItem(R.id.favorite).setIcon(R.drawable.ic_star_white_24dp);
        }
        else {
            menu.findItem(R.id.favorite).setIcon(R.drawable.ic_star_border_white_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.favorite) {
            dh.updateUserFollowing(UserTable.POSTS_FOLLOWING, UserData.username, this.id);
            isFavorite = dh.isUserFollowing(UserTable.POSTS_FOLLOWING, UserData.username, this.id);
            if (isFavorite) {
                toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_star_white_24dp);
            } else {
                toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_star_border_white_24dp);
            }
        } else if (id == R.id.addStory) {
            um.addStory(null);
        } else if (id == R.id.addInstitution) {
            um.addInstitution();
        } else if (id == R.id.addWiki) {
            um.addWiki();
        } else if (id == R.id.share) {
            um.share("thread", thread);
        } else if (id == R.id.logout) {
            um.logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareListData() {
        listData = new ArrayList<>();

        Bundle[] posts = dh.getPostsByThreadAndReplies(thread);

        Collections.addAll(listData, posts);
    }
}
