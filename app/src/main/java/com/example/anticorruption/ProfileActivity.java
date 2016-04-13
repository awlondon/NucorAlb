package com.example.anticorruption;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    LinearLayout highlightMenu;
    ExpandableListView expListView;
    ExpListAdapterProfile listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<Bundle>> listDataChild;
    RelativeLayout profileInfo;
    public Context mContext = this;
    Toolbar mToolbar;

    ProgressBar mProgressBar;

    UniversalMethodsAndVariables universalMethods = new UniversalMethodsAndVariables();

    SearchView searchView;

    ListAdapterResults mListAdapterResults;
    ListView mListViewResults;
    List<Bundle> mListBundleResults;

    String content_view = "profile";
    UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(mContext, mListBundleResults, content_view, highlightMenu);

    DatabaseHelper dh = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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

        um.setHighlightMenu(R.id.profileLayout);

        expListView = (ExpandableListView) findViewById(R.id.lvExpProfile);
        profileInfo = (RelativeLayout) findViewById(R.id.profileInfo);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mProgressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareListData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayUserInfo();
                        listAdapter = new ExpListAdapterProfile(mContext, listDataHeader, listDataChild);
                        expListView.setAdapter(listAdapter);
                        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                openPopUpActivity(groupPosition, childPosition);
                                return true;
                            }
                        });
                        mProgressBar.setVisibility(View.GONE);
                    }
                });

            }
        }).start();

        mListViewResults = (ListView)super.findViewById(R.id.lvResults);

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
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
            ExpandableListView expandableListView = (ExpandableListView) super.findViewById(R.id.lvExpProfile);
            expandableListView.setVisibility(View.VISIBLE);
            expandableListView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            expandableListView.animate().start();

            RelativeLayout profileInfo = (RelativeLayout) super.findViewById(R.id.profileInfo);
            profileInfo.setVisibility(View.VISIBLE);
            profileInfo.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            profileInfo.animate().start();

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
            ExpandableListView expandableListView = (ExpandableListView) super.findViewById(R.id.lvExpProfile);
            expandableListView.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
            expandableListView.animate().start();

            RelativeLayout profileInfo = (RelativeLayout) super.findViewById(R.id.profileInfo);
            profileInfo.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
            profileInfo.animate().start();

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
            expandableListView.setVisibility(View.GONE);
            profileInfo.setVisibility(View.GONE);
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding header data
        listDataHeader.add("Institutions");
        listDataHeader.add("Wikis");
        listDataHeader.add("Threads");

        Bundle[] institutionsBundleArray;
        Bundle[] wikisBundleArray;
        Bundle[] postsBundleArray;

        institutionsBundleArray = dh.getFollowingBundleArray(InstitutionTable.TABLE_NAME, UserTable.INSTITUTIONS_FOLLOWING, UserData.username);
        wikisBundleArray = dh.getFollowingBundleArray(WikiTable.TABLE_NAME, UserTable.WIKIS_FOLLOWING, UserData.username);
        postsBundleArray = dh.getFollowingBundleArray(PostTable.TABLE_NAME, UserTable.POSTS_FOLLOWING, UserData.username);

        List<Bundle> institutionsList = new ArrayList<>();
        List<Bundle> wikisList = new ArrayList<>();
        List<Bundle> postsList = new ArrayList<>();

        if (institutionsBundleArray != null)
            Collections.addAll(institutionsList, institutionsBundleArray);

        if (wikisBundleArray != null)
            Collections.addAll(wikisList, wikisBundleArray);

        if (postsBundleArray != null)
            Collections.addAll(postsList, postsBundleArray);

        listDataChild.put(listDataHeader.get(0), institutionsList);
        listDataChild.put(listDataHeader.get(1), wikisList);
        listDataChild.put(listDataHeader.get(2), postsList);
    }

    public void openPopUpActivity(int groupPosition, int childPosition) {
        Intent intent;
        final int INSTITUTION_SUMMARY = 0;
        final int WIKI_SUMMARY = 1;
        final int POST_SUMMARY = 2;
        List<Bundle> bundleList = listDataChild.get(listDataHeader.get(groupPosition));
        Bundle bundle = bundleList.get(childPosition);

        switch (groupPosition) {
            case INSTITUTION_SUMMARY:
                intent = new Intent(this, InstitutionPopUpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case WIKI_SUMMARY:
                intent = new Intent(this, WikiPopUpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case POST_SUMMARY:
                intent = new Intent(this, ThreadPopUpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }


    private void displayUserInfo() {

        final TextView usernameTv = (TextView) findViewById(R.id.usernameProfile);
        final TextView firstnameTv = (TextView) findViewById(R.id.firstnameProfile);
        final TextView lastnameTv = (TextView) findViewById(R.id.lastnameProfile);
        final TextView locationTv = (TextView) findViewById(R.id.locationProfile);
        final TextView emailTv = (TextView) findViewById(R.id.emailProfile);
        final ImageView profileIv = (ImageView) findViewById(R.id.imageProfile);
        final TextView enrollmentTv = (TextView) findViewById(R.id.enrolledProfile);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = dh.getUser(UserTable.USERNAME, UserData.username);
                final String username = bundle.getString("USERNAME");
                final String first_name = bundle.getString("FIRST_NAME");
                final String last_name = bundle.getString("LAST_NAME");
                final String location = bundle.getString("LOCATION");
                final String email = bundle.getString("EMAIL");
                final String date = bundle.getString("ENROLLMENT");
                final String profileIcon = bundle.getString("ICON");

                TypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

                String usernameString = "Username: " + username;
                final SpannableStringBuilder usernameSpan = new SpannableStringBuilder(usernameString);
                usernameSpan.setSpan(tfBold, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String firstnameString = "First Name: " + first_name;
                final SpannableStringBuilder firstnameSpan = new SpannableStringBuilder(firstnameString);
                firstnameSpan.setSpan(tfBold, 0, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String lastnameString = "Last Name: " + last_name;
                final SpannableStringBuilder lastnameSpan = new SpannableStringBuilder(lastnameString);
                lastnameSpan.setSpan(tfBold, 0, 11, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String locationString = "Location: " + location;
                final SpannableStringBuilder locationSpan = new SpannableStringBuilder(locationString);
                locationSpan.setSpan(tfBold, 0, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String emailString = "Email: " + email;
                final SpannableStringBuilder emailSpan = new SpannableStringBuilder(emailString);
                emailSpan.setSpan(tfBold, 0, 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
               // emailSpan.setSpan(um.blueSpan, 7, emailSpan.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String dateString = "User Since: " + universalMethods.sdf.format(Timestamp.valueOf(date));
                final SpannableStringBuilder dateSpan = new SpannableStringBuilder(dateString);
                dateSpan.setSpan(tfBold, 0, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                loadLogo(0, profileIv, profileIcon);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        usernameTv.setText(usernameSpan);
                        firstnameTv.setText(firstnameSpan);
                        lastnameTv.setText(lastnameSpan);
                        locationTv.setText(locationSpan);
                        emailTv.setText(emailSpan);
                        enrollmentTv.setText(dateSpan);

                        final UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(mContext, username, profileIv, null);

                        profileIv.setOnClickListener(um.imageButtonClicked);
                    }
                });
            }
        }).start();
    }

    public void openActivity(View view) {
        um.openActivity(view);
    }

    public void loadLogo(int resId, ImageView imageView, String logoURL) {
        if(cancelPotentialWork(resId, imageView)){
            final ThumbnailTask task = new ThumbnailTask(imageView, logoURL);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(resId);
        }
    }
    public static boolean cancelPotentialWork(int data, ImageView imageView){
        final ThumbnailTask thumbnailTask = getThumbnailWorkerTask(imageView);

        if (thumbnailTask != null) {
            final int bitmapData = thumbnailTask.data;
            if (bitmapData == 0 || bitmapData != data) {
                thumbnailTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    private static ThumbnailTask getThumbnailWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getThumbnailTask();
            }
        }
        return null;
    }

    private class ThumbnailTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String imageUrl;
        private int reqWidth;
        private int reqHeight;
        private int data = 0;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public ThumbnailTask(ImageView imageView, String imageUrl) {
            this.imageUrl = imageUrl;
            this.reqWidth = imageView.getWidth();
            this.reqHeight = imageView.getHeight();
            this.imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer...params) {
            data = params[0];
            if(imageUrl == null || !universalMethods.checkURL(imageUrl)) {
                return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_person_black_36dp);
            } else {
                return universalMethods.getBitmapFromURL(imageUrl, reqWidth, reqHeight);
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(isCancelled()) {
                bitmap = null;
            }

            if(imageViewReference!=null && bitmap!=null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView!=null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private  final WeakReference<ThumbnailTask> thumbnailTaskWeakReference;

        public AsyncDrawable(ThumbnailTask thumbnailTask) {
            thumbnailTaskWeakReference = new WeakReference<>(thumbnailTask);
        }
        public ThumbnailTask getThumbnailTask () {
            return thumbnailTaskWeakReference.get();
        }
    }



}

