package com.example.anticorruption;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoryPopUpActivity extends AppCompatActivity {
    Context context = this;
    String story, type, date, institution, views;
    String thread;
    Integer id;

    final UniversalMethodsAndVariables um = new UniversalMethodsAndVariables(context);

    ExpandableListView commentsExpListView;
    List<String> listDataHeader;
    HashMap<String, List<Bundle>> listDataChild;
    ExpListAdapterComments listAdapter;

    final DatabaseHelper dh = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_pop_up);

        context = this;

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final FrameLayout institutionFrame = (FrameLayout) findViewById(R.id.institutionFrame);

        final Bundle bundle = getIntent().getExtras();
        id = Integer.valueOf(bundle.getString("ID"));
        story = bundle.getString("CONTENT");
        type = bundle.getString("TYPE");
        date = bundle.getString("DATE");
        institution = bundle.getString("INSTITUTION");
        views = bundle.getString("VIEWS");

        if (views == null || views.equals("null")) {
            views = "1";
        }

        thread = "Story" + id;

        new Thread(new Runnable() {
            @Override
            public void run() {

                populateStoryInfo();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            institutionFrame.addView(populateInstitutionFrame(dh.getInstitution(InstitutionTable.INSTITUTION, institution)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

        institutionFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstitutionPopUp();
            }
        });

        commentsExpListView = (ExpandableListView) findViewById(R.id.expListView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCommentsData(thread);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listAdapter = new ExpListAdapterComments(context, listDataHeader, listDataChild, thread);
                        commentsExpListView.setAdapter(listAdapter);

                        commentsExpListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                            @Override
                            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                Intent intent = new Intent(context, ThreadPopUpActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("THREAD", thread);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                finish();
                                return true;
                            }
                        });
                    }
                });
            }
        }).start();

        dh.updateViews(id, StoryTable.TABLE_NAME, StoryTable.ID, StoryTable.VIEWS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_story, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.share) {
            StringBuilder storyInfo = new StringBuilder();
            storyInfo.append("This corruption report was written about ").append(institution).append(" regarding ").append(type).append(".");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, (Serializable) storyInfo);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "Share this story with..."));
        } else if (id == R.id.addStory) {
            um.addStory(institution);
        } else if (id == R.id.addInstitution) {
            um.addInstitution();
        } else if (id == R.id.addWiki) {
            um.addWiki();
        } else if (id == R.id.logout) {
            um.logout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateStoryInfo() {
        final TextView storyTv = (TextView) findViewById(R.id.story);
        final TextView typeTv = (TextView) findViewById(R.id.type);
        final TextView dateTv = (TextView) findViewById(R.id.date);
        final TextView viewsTv = (TextView) findViewById(R.id.views);

        storyTv.setMovementMethod(new ScrollingMovementMethod());

        new Thread(new Runnable() {
            @Override
            public void run() {
                TypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

                String typeString = "Type: " + type;
                final SpannableStringBuilder typeSpan = new SpannableStringBuilder(typeString);
                typeSpan.setSpan(tfBold, 0, 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                final String dateString = um.getTimeFromNow(date);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        storyTv.setText(story);
                        typeTv.setText(typeSpan);
                        dateTv.setText(dateString);
                        viewsTv.setText(views);
                    }
                });
            }
        }).start();
    }

    public void openInstitutionPopUp() {
        Intent intent = new Intent(this, InstitutionPopUpActivity.class);
        intent.putExtras(dh.getInstitution(InstitutionTable.INSTITUTION, institution));
        finish();
        startActivity(intent);
    }

    private void prepareCommentsData(final String thread) {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeader.add("Comments");

        Bundle[] commentsBundleArray = new Bundle[]{};
        if (dh.getPostsByThread(thread) != null)
            commentsBundleArray = dh.getPostsByThreadAndMostRecent(thread);

        List<Bundle> commentsList = new ArrayList<>();

        for (int i = 0; i < commentsBundleArray.length; i++) {
            commentsList.add(commentsBundleArray[i]);
        }
        listDataChild.put(listDataHeader.get(0), commentsList);
    }

    public View populateInstitutionFrame(final Bundle bundle) throws IOException {
        RelativeLayout titleBar;
        View convertView = getLayoutInflater().inflate(R.layout.institution_summary, null);

        titleBar = (RelativeLayout) convertView.findViewById(R.id.titleBar);
        titleBar.setVisibility(View.GONE);

        final TextView institutionTv = (TextView) convertView.findViewById(R.id.institution);
        final TextView addressTv = (TextView) convertView.findViewById(R.id.address);
        final TextView cityTv = (TextView) convertView.findViewById(R.id.city);
        final ImageView logoIv = (ImageView) convertView.findViewById(R.id.logo);
        final TextView positiveTv = (TextView) convertView.findViewById(R.id.positive);
        final TextView negativeTv = (TextView) convertView.findViewById(R.id.negative);
        final RelativeLayout mainLayout = (RelativeLayout) convertView.findViewById(R.id.mainLayout);
        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.INVISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String institution = bundle.getString("INSTITUTION");
                final String address = bundle.getString("ADDRESS");
                final String city = bundle.getString("CITY");
                final String logo = bundle.getString("LOGO");
                final int positive = Integer.valueOf(bundle.getString("POSITIVE"));
                final int negative = Integer.valueOf(bundle.getString("NEGATIVE"));

                TypefaceSpan tfBold = new CustomTypefaceSpan("", Typeface.create("", Typeface.BOLD));

                String addressString = "Address: " + address;
                final SpannableStringBuilder addressSpan = new SpannableStringBuilder(addressString);
                addressSpan.setSpan(tfBold, 0, 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                String cityString = "City: " + city;
                final SpannableStringBuilder citySpan = new SpannableStringBuilder(cityString);
                citySpan.setSpan(tfBold, 0, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                final String posString = um.getPercentage("pos", positive, negative) + "%";

                final String negString = um.getPercentage("neg", positive, negative) + "%";

                if (logo != null && institution != null && um.checkURL(logo)) {
                    new Thread(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
                        @Override
                        public void run() {
                            final Bitmap bitmap = um.getBitmapFromURL(logo, logoIv.getWidth(), logoIv.getHeight());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    logoIv.setImageBitmap(bitmap);
                                    logoIv.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));
                                    logoIv.getAnimation().setDuration(500);
                                    logoIv.animate().start();
                                }
                            });
                        }
                    }).start();
                } else {
                    logoIv.setImageResource(R.drawable.ic_account_balance_black_48dp);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        institutionTv.setText(institution);
                        addressTv.setText(addressSpan);
                        cityTv.setText(citySpan);
                        positiveTv.setText(posString);
                        negativeTv.setText(negString);
                        progressBar.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();

        return convertView;
    }
}