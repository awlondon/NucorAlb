package com.example.anticorruption;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForumActivity extends AppCompatActivity {
    LinearLayout highlightMenu;

    public Context mContext = this;

    private Toolbar mToolbar;

    private DatabaseHelper dh = new DatabaseHelper(this);

    private UniversalMethodsAndVariables universalMethods = new UniversalMethodsAndVariables();

    SearchView searchView;

    ListAdapterResults mListAdapterResults;
    ListView mListViewResults;
    List<Bundle> mListBundleResults;

    String content_view = "forum";
    UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(mContext, mListBundleResults, content_view, highlightMenu);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forum);
        mContext = this;

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setLogo(R.drawable.nucoralb_logo_white_simple_text_24dp);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View logo = mToolbar.getChildAt(1);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                um.openActivity(v);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        um.setHighlightMenu(R.id.forumLayout);

        mListViewResults = (ListView) findViewById(R.id.lvResults);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        //mProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> threadsDisposable = new ArrayList<String>();
                Collections.addAll(threadsDisposable, (String[]) dh.getStringArray(PostTable.TABLE_NAME, PostTable.THREAD, new String[]{"Start a new thread +"}, false));
                boolean hasStory;
                do {
                    hasStory = false;
                    for (int i = 0; i < threadsDisposable.size(); i++) {
                        if (threadsDisposable.get(i).contains("Story")) {
                            threadsDisposable.remove(i);
                            hasStory = true;
                        }
                    }
                } while (hasStory);
                final ArrayList<String> threadsList = threadsDisposable;
                final ArrayAdapter<String> threadsAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, threadsList);
                System.out.println("Threads: " + threadsList.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView threadsLv = (ListView) findViewById(R.id.lvThreads);
                        threadsLv.setAdapter(threadsAdapter);
                        threadsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (position==0){
                                    Intent intent = new Intent (mContext, ThreadPostEditActivity.class);
                                    startActivity(intent);
                                } else {
                                    String thread = threadsList.get(position);
                                    Intent intent = new Intent(mContext, ThreadPopUpActivity.class);
                                    intent.putExtra("THREAD", thread);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                });
            }
        }).start();

        mListViewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView activityType = (TextView) view.findViewById(R.id.header);

                switch (activityType.getText().toString()) {
                    case "Story":
                        System.out.println("Opening story activity...");
                        um.openPopUpActivity(0, position, mListBundleResults);
                        break;
                    case "Wiki Article":
                        System.out.println("Opening wiki article activity...");
                        um.openPopUpActivity(1, position, mListBundleResults);
                        break;
                    case "Forum Post":
                        System.out.println("Opening forum post activity...");
                        um.openPopUpActivity(2, position, mListBundleResults);
                        break;
                    case "Institution":
                        System.out.println("Opening institution activity...");
                        um.openPopUpActivity(3, position, mListBundleResults);
                }
            }
        });

        searchView = (SearchView) mToolbar.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                um.prepareResultsListData(query);
                mListBundleResults = um.resultsListData;
                mListViewResults.setAdapter(mListAdapterResults);
                mListAdapterResults = new ListAdapterResults(mContext, mListBundleResults, query);
                mListAdapterResults.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                um.prepareResultsListData(newText);
                mListBundleResults = um.resultsListData;
                mListViewResults.setAdapter(mListAdapterResults);
                mListAdapterResults = new ListAdapterResults(mContext, mListBundleResults, newText);
                mListAdapterResults.notifyDataSetChanged();
                return true;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addStory) {
            um.addStory(null);
        } else if (id == R.id.addInstitution) {
            um.addInstitution();
        } else if (id == R.id.addWiki) {
            um.addWiki();
        } else if (id == R.id.share) {
            um.share("app", getResources().getString(R.string.app_name));
        } else if (id == R.id.logout) {
            um.logout();
        } else if (id == android.R.id.home) {
            ListView lvThreads = (ListView) super.findViewById(R.id.lvThreads);
            lvThreads.setVisibility(View.VISIBLE);
            lvThreads.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            lvThreads.animate().start();

            ListView lvResults = (ListView) super.findViewById(R.id.lvResults);
            View button_menu = super.findViewById(R.id.button_menu);
            button_menu.setVisibility(View.VISIBLE);
            button_menu.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            button_menu.animate().start();

            searchView = (SearchView) findViewById(R.id.searchView);
            searchView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.from_scale_x_right));
            searchView.animate().start();

            mToolbar = (Toolbar) findViewById(R.id.tool_bar);
            mToolbar.setLogo(R.drawable.nucoralb_logo_white_simple_text_24dp);
            setSupportActionBar(mToolbar);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            searchView.setVisibility(View.GONE);
            lvResults.setVisibility(View.GONE);

        } else if (id == R.id.searchAll) {
            ListView lvThreads = (ListView) super.findViewById(R.id.lvThreads);
            lvThreads.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
            lvThreads.animate().start();

            mToolbar.getMenu().removeItem(R.id.searchAll);
            mToolbar.getMenu().removeItem(R.id.addStory);
            mToolbar.getMenu().removeItem(R.id.addInstitution);
            mToolbar.getMenu().removeItem(R.id.addWiki);
            mToolbar.getMenu().removeItem(R.id.share);
            mToolbar.getMenu().removeItem(R.id.logout);
            mToolbar.setLogo(null);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            searchView = (SearchView) findViewById(R.id.searchView);
            searchView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.to_scale_x_left));
            searchView.setVisibility(View.VISIBLE);
            searchView.animate().start();
            searchView.setIconified(false);

            View button_menu = super.findViewById(R.id.button_menu);
            button_menu.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
            button_menu.animate().start();
            button_menu.setVisibility(View.GONE);

            ListView lvResults = (ListView) super.findViewById(R.id.lvResults);
            lvResults.setAlpha(1.00f);
            lvResults.setVisibility(View.VISIBLE);
            lvThreads.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }

    public void openActivity(View view) {
        um.openActivity(view);
    }
}
