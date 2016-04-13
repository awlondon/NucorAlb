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
import android.widget.SearchView;
import android.widget.TextView;

import java.util.List;

public class WikiActivity extends AppCompatActivity {
    LinearLayout highlightMenu;

    public Context mContext = this;

    private Toolbar mToolbar;

    private DatabaseHelper dh = new DatabaseHelper(this);

    SearchView searchView;

    ListAdapterResults mListAdapterResults;
    ListView mListViewResults;
    List<Bundle> mListBundleResults;

    String content_view = "wiki";
    UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(mContext, mListBundleResults, content_view, highlightMenu);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wiki);
        mContext = this;

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setLogo(R.drawable.nucoralb_logo_white_simple_text_24dp);
        setSupportActionBar(mToolbar);

        View logo = mToolbar.getChildAt(1);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                um.openActivity(v);
            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        um.setHighlightMenu(R.id.wikiLayout);

        mListViewResults = (ListView) findViewById(R.id.lvResults);

        final String[] categories = dh.getStringArray(WikiTable.TABLE_NAME, WikiTable.CATEGORY, null, false);
        final String[] tags = dh.getStringArray(WikiTable.TABLE_NAME, WikiTable.TAGS, null, true);

        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        mainLayout.setPaddingRelative(20, 10, 10, 10);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        TextView browseTv = new TextView(mContext);
        browseTv.setText("Browse");
        browseTv.setTextSize(30f);
        mainLayout.addView(browseTv);

        TextView categoriesTv = new TextView(mContext);
        categoriesTv.setText("Categories:");
        categoriesTv.setTextSize(20f);
        categoriesTv.setPaddingRelative(20, 20, 0, 0);
        mainLayout.addView(categoriesTv);

        ArrayAdapter<String> mCatArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, categories);
        ListView mCatListView = new ListView(mContext);
        mCatListView.setAdapter(mCatArrayAdapter);
        mCatListView.setMinimumHeight(200);
        mainLayout.addView(mCatListView);
        mCatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String string = categories[position];
                                Intent intent = new Intent(mContext, WikiPopUpActivity.class);
                                intent.putExtra("CAT", string);
                                startActivity(intent);
                            }
                        });

        TextView tagsTv = new TextView(mContext);
        tagsTv.setText("Tags:");
        tagsTv.setTextSize(20f);
        tagsTv.setPaddingRelative(20, 20, 0, 0);
        mainLayout.addView(tagsTv);

        ArrayAdapter<String> mTagArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, tags);
        ListView mTagListView = new ListView(mContext);
        mTagListView.setAdapter(mTagArrayAdapter);
        mTagListView.setMinimumHeight(200);
        mainLayout.addView(mTagListView);
        mTagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String string = tags[position];
                                Intent intent = new Intent(mContext, WikiPopUpActivity.class);
                                intent.putExtra("TAG", string);
                                startActivity(intent);
                            }
                        });


                        /*for (final String string : categories) {
                            TextView view = new TextView(mContext);
                            view.setTextSize(20f);
                            view.setPaddingRelative(20, 10, 0, 0);
                            Drawable bp = ResourcesCompat.getDrawable(getResources(), R.drawable.bullet_point, null);
                            assert bp != null;
                            bp.setBounds(0, 0, 15, 15);
                            view.setCompoundDrawables(bp, null, null, null);
                            view.setCompoundDrawablePadding(20);

                            SpannableStringBuilder stringSb = new SpannableStringBuilder(string);
                            stringSb.setSpan(universalMethods.blueSpan, 0, stringSb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            view.setText(stringSb);
                            mainLayout.addView(view);
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, WikiPopUpActivity.class);
                                    intent.putExtra("CAT", string);
                                    startActivity(intent);
                                }
                            });
                        }*/

/*
                        for (final String tag : tags) {
                            TextView tagView = new TextView(mContext);
                            tagView.setTextSize(20f);
                            tagView.setPaddingRelative(20, 10, 0, 0);
                            Drawable bp = ResourcesCompat.getDrawable(getResources(), R.drawable.bullet_point, null);
                            assert bp != null;
                            bp.setBounds(0, 0, 15, 15);
                            tagView.setCompoundDrawables(bp, null, null, null);
                            tagView.setCompoundDrawablePadding(20);

                            SpannableStringBuilder tagSb = new SpannableStringBuilder(tag);
                            tagSb.setSpan(universalMethods.blueSpan, 0, tagSb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            tagView.setText(tagSb);
                            mainLayout.addView(tagView);
                            tagView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, WikiPopUpActivity.class);
                                    intent.putExtra("TAG", tag);
                                    startActivity(intent);
                                }
                            });

                        }*/

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

    @Override
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
            LinearLayout mainLayout = (LinearLayout) super.findViewById(R.id.mainLayout);
            mainLayout.setVisibility(View.VISIBLE);
            mainLayout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            mainLayout.animate().start();

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
            LinearLayout mainLayout = (LinearLayout) super.findViewById(R.id.mainLayout);
            mainLayout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
            mainLayout.animate().start();

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
            mainLayout.setVisibility(View.GONE);

        }
        return super.onOptionsItemSelected(item);
    }

    public void openActivity(View view) {
        um.openActivity(view);
    }
}
