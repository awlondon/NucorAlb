package com.example.anticorruption;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InstitutionPopUpActivity extends AppCompatActivity {

    ExpandableListView expListView;
    ExpListAdapterInstitution listAdapter;
    List<String> listDataHeader;
    HashMap<String, Integer[]> listDataChild;
    DatabaseHelper dh = new DatabaseHelper(this);
    String institution, type, logo, address, manager, phone, city, abuse_of_discretion,
            blackmail, bribery, embezzlement, extortion, fraud, nepotism, other_corruption,
            appointment_wait_time, document_wait_time, permit_wait_time;
    int positive, negative;
    Integer id;
    Boolean isFavorite;
    String views;
    ProgressBar progressBar;
    LinearLayout superLayout;

    Context context = this;

    UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(context);

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution_pop_up);

        context = this;
        expListView = (ExpandableListView) findViewById(R.id.expListView);
        superLayout = (LinearLayout) findViewById(R.id.superLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        expListView.setVisibility(View.INVISIBLE);
        superLayout.setVisibility(View.INVISIBLE);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Thread backgroundData = new Thread(new Runnable() {
            @Override
            public void run() {
                dh.updateViews(id, InstitutionTable.TABLE_NAME, InstitutionTable.ID, InstitutionTable.VIEWS);

                final Bundle bundle = getIntent().getExtras();
                id = Integer.valueOf(bundle.getString("ID"));
                institution = bundle.getString("INSTITUTION");
                type = bundle.getString("TYPE");
                address = bundle.getString("ADDRESS");
                city = bundle.getString("CITY");
                views = bundle.getString("VIEWS");
                abuse_of_discretion = bundle.getString("ABUSE_OF_DISCRETION");
                blackmail = bundle.getString("BLACKMAIL");
                bribery = bundle.getString("BRIBERY");
                embezzlement = bundle.getString("EMBEZZLEMENT");
                extortion = bundle.getString("EXTORTION");
                fraud = bundle.getString("FRAUD");
                nepotism = bundle.getString("NEPOTISM");
                other_corruption = bundle.getString("OTHER_CORRUPTION");
                appointment_wait_time = bundle.getString("APPOINTMENT_WAIT_TIME");
                document_wait_time = bundle.getString("DOCUMENT_WAIT_TIME");
                permit_wait_time = bundle.getString("PERMIT_WAIT_TIME");

                if (views == null || views.equals("null")) {
                    views = "1";
                }

                if (bundle.getString("LOGO") != null)
                    logo = bundle.getString("LOGO");
                else
                    logo = null;

                manager = bundle.getString("MANAGER");
                phone = bundle.getString("PHONE");
                if (bundle.getString("POSITIVE") != null && bundle.getString("POSITIVE") != "null") {
                    positive = Integer.valueOf(bundle.getString("POSITIVE"));
                } else {
                    positive = 0;
                }
                if (bundle.getString("NEGATIVE") != null && bundle.getString("NEGATIVE") != "null") {
                    negative = Integer.valueOf(bundle.getString("NEGATIVE"));
                } else {
                    positive = 0;
                }

                prepareListData();


                listAdapter = new ExpListAdapterInstitution(context, listDataHeader, listDataChild);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayInstitutionInfo();
                        expListView.setAdapter(listAdapter);

                        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                List<Bundle> bundleList = new ArrayList<>();
                                for (String key : listDataChild.keySet()){
                                    Bundle mBundle = new Bundle();
                                    mBundle.putString("ID", String.valueOf(listDataChild.get(key)));
                                    bundleList.add(mBundle);
                                }
                                um.openPopUpActivity(groupPosition, childPosition, bundleList);
                                return true;
                            }
                        });
                        progressBar.setVisibility(View.GONE);
                        superLayout.setVisibility(View.VISIBLE);
                        expListView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        backgroundData.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_institution, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (dh.isInFinishedSurveys(UserData.username, this.id)) {
            menu.findItem(R.id.takeSurvey).setEnabled(false).getIcon().setAlpha(100);
        } else {
            menu.findItem(R.id.takeSurvey).setEnabled(true).getIcon().setAlpha(255);
        }
        isFavorite = dh.isUserFollowing(UserTable.INSTITUTIONS_FOLLOWING, UserData.username, this.id);
        if (isFavorite) {
            toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_star_white_24dp);
        } else {
            toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_star_border_white_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
            finish();
        else if (id == R.id.takeSurvey) {
            Intent intent = new Intent(this, SurveyPopUpActivity.class);
            intent.putExtra("ID", this.id)
                    .putExtra("INSTITUTION", institution)
                    .putExtra("LAYOUT", R.layout.survey_1);
            finish();
            startActivity(intent);
        } else if (id == R.id.favorite) {
            dh.updateUserFollowing(UserTable.INSTITUTIONS_FOLLOWING, UserData.username, this.id);
            isFavorite = dh.isUserFollowing(UserTable.INSTITUTIONS_FOLLOWING, UserData.username, this.id);
            if (isFavorite) {
                toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_star_white_24dp);
            } else {
                toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_star_border_white_24dp);
            }
        } else if (id == R.id.addStory) {
            um.addStory(institution);
        } else if (id == R.id.addInstitution) {
            um.addInstitution();
        } else if (id == R.id.addWiki) {
            um.addWiki();
        } else if (id == R.id.share) {
            um.share("institution", institution);
        } else if (id == R.id.logout) {
            um.logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<>();

        // Adding header data
        listDataHeader.add("Corruption Stories");
        listDataHeader.add("Wiki Entries");
        listDataHeader.add("Forum Posts");

        // Adding child data
        Integer[] stories, wikis, posts;

        stories = dh.getStoriesByQuery(institution);
        wikis = dh.getWikisByQuery(institution);
        posts = dh.getPostsByQuery(institution);

        listDataChild.put(listDataHeader.get(0), stories); // Header, child data
        listDataChild.put(listDataHeader.get(1), wikis);
        listDataChild.put(listDataHeader.get(2), posts);

    }

    public void displayInstitutionInfo() {

        TextView institutionTv = (TextView) findViewById(R.id.institution);
        TextView typeTv = (TextView) findViewById(R.id.type);
        TextView addressTv = (TextView) findViewById(R.id.address);
        final ImageView logoIv = (ImageView) findViewById(R.id.logo);
        TextView positiveTv = (TextView) findViewById(R.id.positive);
        TextView negativeTv = (TextView) findViewById(R.id.negative);
        TextView managerTv = (TextView) findViewById(R.id.manager);
        TextView phoneTv = (TextView) findViewById(R.id.phone);
        TextView viewsTv = (TextView) findViewById(R.id.views);
        TextView cityTv = (TextView) findViewById(R.id.city);

        ImageView callPhoneIv = (ImageView) findViewById(R.id.callPhone);

        callPhoneIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDial = new Intent(Intent.ACTION_DIAL);
                intentDial.setData(Uri.parse("tel:" + phone));
                startActivity(intentDial);
            }
        });

        TypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

        String typeString = "Type: " + type;
        SpannableStringBuilder typeSpan = new SpannableStringBuilder(typeString);
        typeSpan.setSpan(tfBold, 0, 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        String addressString = "Address: " + address;
        SpannableStringBuilder addressSpan = new SpannableStringBuilder(addressString);
        addressSpan.setSpan(tfBold, 0, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        String cityString = "City: " + city;
        SpannableStringBuilder citySpan = new SpannableStringBuilder(cityString);
        citySpan.setSpan(tfBold, 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        String posString = um.getPercentage("pos", positive, negative) + "%";
        SpannableStringBuilder posSpan = new SpannableStringBuilder(posString);

        String negString = um.getPercentage("neg", positive, negative) + "%";
        SpannableStringBuilder negSpan = new SpannableStringBuilder(negString);

        String managerString = "Manager: " + manager;
        SpannableStringBuilder managerSpan = new SpannableStringBuilder(managerString);
        managerSpan.setSpan(tfBold, 0, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        String phoneString = "Phone: " + phone;
        SpannableStringBuilder phoneSpan = new SpannableStringBuilder(phoneString);
        phoneSpan.setSpan(tfBold, 0, 7, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        int resId = (int) (Math.random()*10000);
        if(logo!=null) {
            resId = logo.hashCode();
        }
        loadLogo(resId, logoIv, institution, logo);

        institutionTv.setText(institution);
        typeTv.setText(typeSpan);
        addressTv.setText(addressSpan);
        cityTv.setText(citySpan);
        positiveTv.setText(posSpan);
        negativeTv.setText(negSpan);
        managerTv.setText(managerSpan);
        phoneTv.setText(phoneSpan);
        viewsTv.setText(views);

        LinearLayout corruptionNumbers = (LinearLayout) findViewById(R.id.corruptionNumbers);
        final TextView corruptionStats = new TextView(context);
        SpannableStringBuilder corruptionStatsSpan = new SpannableStringBuilder("Corruption stats +");
        //corruptionStatsSpan.setSpan(tfBold, 0, corruptionStatsSpan.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        corruptionStats.setText(corruptionStatsSpan);
        corruptionNumbers.addView(corruptionStats);
        final LinearLayout innerLayout = new LinearLayout(context);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setVisibility(View.GONE);
        corruptionNumbers.addView(innerLayout);
        String[] corruptionValues = {abuse_of_discretion, blackmail, bribery, embezzlement, extortion,
                fraud, nepotism, other_corruption, appointment_wait_time, document_wait_time, permit_wait_time};

        String sig = ": ";
        String[] corruptionStrings = {"Abuse of Discretion" + sig+ abuse_of_discretion, "Blackmail"+sig+ blackmail,
        "Bribery"+sig+bribery, "Embezzlement"+sig+embezzlement, "Extortion"+sig+extortion, "Fraud"+sig+fraud,
        "Nepotism"+sig+nepotism, "Other"+sig+other_corruption, "Appointment wait time"+sig+appointment_wait_time,
        "Document wait time"+sig+document_wait_time, "Permit wait time"+sig+permit_wait_time};

        for (String string:corruptionStrings) {
            TextView textView = new TextView(context);
            textView.setText(string);
            textView.setTextSize(11.5f);
            innerLayout.addView(textView);
        }

        corruptionStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (innerLayout.getVisibility()==View.GONE) {
                    innerLayout.setVisibility(View.VISIBLE);
                    corruptionStats.setText("Corruption stats -");
                } else {
                    innerLayout.setVisibility(View.GONE);
                    corruptionStats.setText("Corruption stats +");
                }
            }
        });

    }

    public void loadLogo(int resId, ImageView imageView, String inst, String logoURL) {
        if(cancelPotentialWork(resId, imageView)){
            final ThumbnailTask task = new ThumbnailTask(imageView, inst, logoURL);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(Resources.getSystem(), BitmapFactory.decodeResource(context.getResources(), um.PLACE_HOLDER), task);
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
        private String inst;
        private String logoURL;
        private int reqWidth;
        private int reqHeight;
        private int data = 0;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public ThumbnailTask(ImageView imageView, String inst, String logoURL) {
            this.inst = inst;
            this.logoURL = logoURL;
            this.reqWidth = imageView.getWidth();
            this.reqHeight = imageView.getHeight();
            this.imageViewReference = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Integer...params) {
            data = params[0];
            if(logoURL==null || inst == null || !um.checkURL(logoURL)) {
                return BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_account_balance_black_48dp);
            } else {
                return um.getBitmapFromURL(logoURL, reqWidth, reqHeight);
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

        public AsyncDrawable(Resources res, Bitmap bitmap, ThumbnailTask thumbnailTask) {
            super(res, bitmap);
            thumbnailTaskWeakReference = new WeakReference<>(thumbnailTask);
        }
        public ThumbnailTask getThumbnailTask () {
            return thumbnailTaskWeakReference.get();
        }
    }
}
