package com.example.anticorruption;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alexlondon on 3/20/16.
 */
public class InstitutionsActivity extends AppCompatActivity {
    public Context context = this;

    ListAdapterInstitutions institutionsListAdapter;
    ListView institutionsListView;
    List<Bundle> institutionsListData;
    LinearLayout highlightMenu;

    ListAdapterResults resultsListAdapter;
    ListView resultsListView;
    List<Bundle> resultsListData;
    Integer[] mIntegerArrayInstitutions;

    HashMap<String, String> queriesHashMap = new HashMap<>();
    ArrayList<String> queriesList;

    String content_view = "institutions";
    UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(context, resultsListData, content_view, highlightMenu);

    final DatabaseHelper dh = new DatabaseHelper(this);

    ProgressBar progressBar;

    private Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_institutions);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setLogo(ContextCompat.getDrawable(context, R.drawable.nucoralb_logo_white_simple_text_24dp));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        View logo = toolbar.getChildAt(1);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                um.openActivity(v);
            }
        });

        um.setHighlightMenu(R.id.institutionsLayout);

        institutionsListView = (ListView) findViewById(R.id.lvFeed);
        resultsListView = (ListView) findViewById(R.id.lvResults);

        Spinner spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        Spinner spinnerType = (Spinner) findViewById(R.id.spinnerType);

        final String[] cities = dh.getStringArray(InstitutionTable.TABLE_NAME, InstitutionTable.CITY, new String[]{"(Filter by city)"}, false);
        final String[] types = dh.getStringArray(InstitutionTable.TABLE_NAME, InstitutionTable.TYPE, new String[]{"(Filter by type)"}, false);
        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, cities);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, types);
        spinnerCity.setAdapter(citiesAdapter);
        spinnerType.setAdapter(typesAdapter);


        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                queriesList = new ArrayList<>();
                String query;
                if (position!=0) {
                    queriesHashMap.put("city", cities[position]);
                } else {
                    queriesHashMap.put("city", "");
                }
                queriesList.addAll(queriesHashMap.values());
                System.out.println("QueriesHashMap Values" + queriesHashMap.values().toString());
                System.out.println("QueriesList: " + queriesList.toString());
                query = Arrays.toString(queriesList.toArray()).replaceAll("\\p{P}", "").replace("null","");
                System.out.println("Query: " + query);
                if (query.isEmpty()) {
                    query = null;
                }

                prepareInstitutionsListData(query);
                institutionsListAdapter = new ListAdapterInstitutions(context, institutionsListData, null);
                institutionsListView.setAdapter(institutionsListAdapter);
                //institutionsListAdapter.notifyDataSetChanged();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                queriesList = new ArrayList<>();
                String query;
                if (position != 0) {
                    queriesHashMap.put("type", types[position]);
                } else {
                    queriesHashMap.put("type", "");
                }
                queriesList.addAll(queriesHashMap.values());
                System.out.println("QueriesHashMap Values" + queriesHashMap.values().toString());
                System.out.println("QueriesList: " + queriesList.toString());
                query = Arrays.toString(queriesList.toArray()).replaceAll("\\p{P}", "").replace("null", "");
                System.out.println("Query: " + query);
                if (query.isEmpty()) {
                    query = null;
                }

                prepareInstitutionsListData(query);
                institutionsListAdapter = new ListAdapterInstitutions(context, institutionsListData, null);
                institutionsListView.setAdapter(institutionsListAdapter);
                //institutionsListAdapter.notifyDataSetChanged();
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Thread backgroundData = new Thread(new Runnable() {
            @Override
            public void run() {
                prepareInstitutionsListData(null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        institutionsListAdapter = new ListAdapterInstitutions(context, institutionsListData, null);
                        institutionsListView.setAdapter(institutionsListAdapter);
                        institutionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        System.out.println("Opening institution activity...");
                                        um.openPopUpActivity(3, position, institutionsListData);
                                }
                            });

                        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                TextView activityType = (TextView) view.findViewById(R.id.header);

                                switch (activityType.getText().toString()) {
                                    case "Story":
                                        System.out.println("Opening story activity...");
                                        um.openPopUpActivity(0, position, resultsListData);
                                        break;
                                    case "Wiki Article":
                                        System.out.println("Opening wiki article activity...");
                                        um.openPopUpActivity(1, position, resultsListData);
                                        break;
                                    case "Forum Post":
                                        System.out.println("Opening forum post activity...");
                                        um.openPopUpActivity(2, position, resultsListData);
                                        break;
                                    case "Institution":
                                        System.out.println("Opening institution activity...");
                                        um.openPopUpActivity(3, position, resultsListData);
                                }
                            }
                        });

                        SearchView searchView = (SearchView) toolbar.findViewById(R.id.searchView);

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(final String query) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.VISIBLE);
                                        System.out.println("search initiated...");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        um.prepareResultsListData(query);
                                                        resultsListData = um.resultsListData;
                                                        resultsListAdapter = new ListAdapterResults(context, resultsListData, query);
                                                        resultsListView.setAdapter(resultsListAdapter);
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                                return true;
                            }

                            @Override
                            public boolean onQueryTextChange(final String newText) {
                                return false;
                            }
                        });
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        backgroundData.start();
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
            ListView lvFeed = (ListView) super.findViewById(R.id.lvFeed);
            lvFeed.setVisibility(View.VISIBLE);
            lvFeed.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
            lvFeed.animate().start();
            Spinner spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
            Spinner spinnerType = (Spinner) findViewById(R.id.spinnerType);
            spinnerCity.setVisibility(View.VISIBLE);
            spinnerType.setVisibility(View.VISIBLE);


            ListView lvResults = (ListView) super.findViewById(R.id.lvResults);
            View button_menu = super.findViewById(R.id.button_menu);
            button_menu.setVisibility(View.VISIBLE);
            button_menu.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
            button_menu.animate().start();

            SearchView searchView = (SearchView) findViewById(R.id.searchView);
            searchView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.from_scale_x_right));
            searchView.animate().start();

            toolbar = (Toolbar) findViewById(R.id.tool_bar);
            toolbar.setLogo(R.drawable.nucoralb_logo_white_simple_text_24dp);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            searchView.setVisibility(View.GONE);
            lvResults.setVisibility(View.GONE);

        } else if (id == R.id.searchAll) {
            ListView lvFeed = (ListView) super.findViewById(R.id.lvFeed);
            lvFeed.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
            lvFeed.animate().start();

            toolbar.getMenu().removeItem(R.id.searchAll);
            toolbar.getMenu().removeItem(R.id.addStory);
            toolbar.getMenu().removeItem(R.id.addInstitution);
            toolbar.getMenu().removeItem(R.id.addWiki);
            toolbar.getMenu().removeItem(R.id.share);
            toolbar.getMenu().removeItem(R.id.logout);
            toolbar.setLogo(null);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            SearchView searchView = (SearchView) findViewById(R.id.searchView);
            searchView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.to_scale_x_left));
            searchView.setVisibility(View.VISIBLE);
            searchView.animate().start();
            searchView.setIconified(false);

            View button_menu = super.findViewById(R.id.button_menu);
            button_menu.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out));
            button_menu.animate().start();
            button_menu.setVisibility(View.GONE);
            lvFeed.setVisibility(View.GONE);
            Spinner spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
            Spinner spinnerType = (Spinner) findViewById(R.id.spinnerType);
            spinnerCity.setVisibility(View.GONE);
            spinnerType.setVisibility(View.GONE);

            ListView lvResults = (ListView) super.findViewById(R.id.lvResults);
            lvResults.setAlpha(1.00f);
            lvResults.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareInstitutionsListData(String query) {
        DatabaseHelper dh = new DatabaseHelper(context);
        institutionsListData = new ArrayList<>();

        mIntegerArrayInstitutions = dh.getInstitutionsByQuery(query);
        for (Integer integer : mIntegerArrayInstitutions) {
            Bundle mBundle = new Bundle();
            mBundle.putString("ID", String.valueOf(integer));
            institutionsListData.add(mBundle);
        }

        }

    private void setHeaderText (List<Bundle> listData, ListView listView) {
        if (listView.getHeaderViewsCount() > 0) {
            View oldView = listView.findViewWithTag(this.getClass().getSimpleName() + "header");
            if (oldView != null) {
                listView.removeHeaderView(oldView);
            }
        }
        String headerString = listData.size() + " results";
        TextView headerText = new TextView(context);
        headerText.setTag(this.getClass().getSimpleName()+"header");
        headerText.setText(headerString);
        headerText.setGravity(Gravity.CENTER);
        headerText.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
        headerText.setPaddingRelative(0, 5, 0, 5);
        headerText.setGravity(Gravity.CENTER);
        listView.addHeaderView(headerText);
    }

    public void openActivity(View view) {
        um.openActivity(view);
    }

}


