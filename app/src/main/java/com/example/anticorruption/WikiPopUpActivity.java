package com.example.anticorruption;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class WikiPopUpActivity extends AppCompatActivity {
    static final int WIKI_POST = 0;
    static final int TAG_PAGE = 1;
    static final int CATEGORY_PAGE = 2;
    static final int EDIT_PAGE = 3;
    int menuType;

    ScrollView scrollView;
    TextView titleTv, contentTv, createdTv, updatedTv, categoryTv, viewsTv;
    TextView tagTv;
    ImageView editIv;
    LinearLayout tagsLayout;
    ListView mListView;
    EditText titleEt, contentEt;
    Spinner categorySpinner;
    ImageView updateIv;

    String title, content, category, tags, views, created, updated;
    String originalTitle;
    Integer id;
    String tag;
    ArrayAdapter<String> tagsAdapter;
    LinearLayout scrollableLayout;
    static final String delimiter = ";";

    boolean isFavorite;
    boolean ready;
    boolean new_wiki;

    DatabaseHelper dh = new DatabaseHelper(this);
    Context context;

    UniversalMethodsAndVariables universalMethods = new UniversalMethodsAndVariables();

    private android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        if (getIntent().getExtras().containsKey("CONTENT")) {
            menuType = WIKI_POST;
            id = getIntent().getExtras().getInt("ID");
            setContentView(R.layout.activity_wiki_pop_up);
            populateWikiPost();
        } else if (getIntent().getExtras().containsKey("TAG")) {
            menuType = TAG_PAGE;
            setContentView(R.layout.wiki_tag_page);
            populateTagPage();
        } else if (getIntent().getExtras().containsKey("CAT")) {
            menuType = CATEGORY_PAGE;
            setContentView(R.layout.wiki_tag_page);
            populateCategoryPage();
        } else if (getIntent().getExtras().containsKey("NEW")) {
            menuType = EDIT_PAGE;
            setContentView(R.layout.wiki_edit);
            populateEditPage();
        }

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_main; this adds items to the action bar if it is present.
        switch (menuType) {
            case WIKI_POST:
                getMenuInflater().inflate(R.menu.menu_wiki, menu);
                break;
            case TAG_PAGE:
                break;
            case CATEGORY_PAGE:
                break;
            case EDIT_PAGE:
                break;
        }

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.findItem(R.id.favorite) != null) {
            isFavorite = dh.isUserFollowing(UserTable.WIKIS_FOLLOWING, UserData.username, this.id);
            if (isFavorite) {
                toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_star_white_24dp);
            } else {
                toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_star_border_white_24dp);
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.favorite) {
            dh.updateUserFollowing(UserTable.WIKIS_FOLLOWING, UserData.username, this.id);
            isFavorite = dh.isUserFollowing(UserTable.WIKIS_FOLLOWING, UserData.username, this.id);
            if (isFavorite) {
                toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_star_white_24dp);
            } else {
                toolbar.getMenu().findItem(R.id.favorite).setIcon(R.drawable.ic_star_border_white_24dp);
            }
        } else if (id == R.id.addStory) {
            Intent intent = new Intent(this, AddStoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.addInstitution) {
            Intent intent = new Intent(this, AddInstitutionActivity.class);
            startActivity(intent);
        } else if (id == R.id.addWiki) {
            Intent intent = new Intent(this, WikiPopUpActivity.class);
            intent.putExtra("NEW", true);
            startActivity(intent);
        } else if (id == R.id.share) {
            StringBuilder wikiInfo = new StringBuilder();
            wikiInfo.append("I want to share this wiki article with you: ").append(title.toUpperCase()).append(", from the Anti-Corruption App.");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, (Serializable) wikiInfo);
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent, "Share this wiki article with..."));
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateWikiPost() {
        Bundle bundle = getIntent().getExtras();
        id = Integer.valueOf(bundle.getString("ID"));
        title = bundle.getString("TITLE");
        content = bundle.getString("CONTENT");
        category = bundle.getString("CATEGORY");
        tags = bundle.getString("TAGS");
        views = bundle.getString("VIEWS");
        created = bundle.getString("CREATED");
        updated = bundle.getString("UPDATED");

        if (views == null || views.equals("null")) {
            views = "1";
        }

        titleTv = (TextView) findViewById(R.id.titleText);
        categoryTv = (TextView) findViewById(R.id.category);
        contentTv = (TextView) findViewById(R.id.content);
        createdTv = (TextView) findViewById(R.id.created);
        updatedTv = (TextView) findViewById(R.id.updated);
        editIv = (ImageView) findViewById(R.id.edit);
        tagsLayout = (LinearLayout) findViewById(R.id.tagsLayout);
        viewsTv = (TextView) findViewById(R.id.views);

        titleTv.setText(title);
        contentTv.setText(content);
        viewsTv.setText(views);

        SpannableStringBuilder categoryString = new SpannableStringBuilder("Category: " + category);
        categoryString.setSpan(universalMethods.blueSpan, 9, categoryString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        categoryTv.setText(categoryString);
        String createdString = "Created: " + universalMethods.getTimeFromNow(created);
        createdTv.setText(createdString);
        String updatedString;

        if (updated != null && !updated.contains("null")) {
            updatedString = "Updated: " + universalMethods.getTimeFromNow(updated);
            updatedTv.setText(updatedString);
        } else {
            updatedTv.setVisibility(View.GONE);
        }

        String[] tagsArray = tags.split(delimiter);
        int superArrayPosition = 0;
        int noOfTagsToAdd;
        do {
            LinearLayout horizontalLayout = new LinearLayout(context);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setPadding(0, 0, 0, 0);
            if (tagsLayout.getChildCount() == 0) {
                TextView tgs = new TextView(context);
                tgs.setText("Tags: ");
                horizontalLayout.addView(tgs);
            } else {
                TextView tgs = new TextView(context);
                tgs.setText("Tags: ");
                tgs.setVisibility(View.INVISIBLE);
                horizontalLayout.addView(tgs);
            }
            int characters = 0;
            noOfTagsToAdd = 0;
            System.out.println("superArrayPosition: " + superArrayPosition);
            for (int k = superArrayPosition; k < tagsArray.length; k++) {
                characters += tagsArray[k].length();
                if (characters < 35) {
                    noOfTagsToAdd++;
                    System.out.println("Total nuber of characters: " + characters);
                    System.out.println("noOfTagsToAdd: " + noOfTagsToAdd);
                } else {
                    System.out.println("Breaking out of adding tags");
                    break;
                }
            }
            int startPosition = superArrayPosition;
            System.out.println("Starting index position: " + startPosition);
            System.out.println("Current tag add index goal: " + (startPosition + noOfTagsToAdd));
            for (int m = startPosition; m < startPosition + noOfTagsToAdd; m++) {
                TextView view = new TextView(context);
                if (m == tagsArray.length - 1) {
                    SpannableStringBuilder sb = new SpannableStringBuilder(" " + tagsArray[m]);
                    sb.setSpan(universalMethods.blueSpan, 0, sb.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    view.setText(sb);
                } else {
                    SpannableStringBuilder sb = new SpannableStringBuilder(" " + tagsArray[m] + ",");
                    sb.setSpan(universalMethods.blueSpan, 0, sb.length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    view.setText(sb);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView tag = (TextView) v;
                        Intent intent = new Intent(context, WikiPopUpActivity.class);
                        intent.putExtra("TAG", tag.getText().toString());
                        startActivity(intent);
                    }
                });

                horizontalLayout.addView(view);
                superArrayPosition++;
                System.out.println("superArrayPosition: " + superArrayPosition);
            }
            tagsLayout.addView(horizontalLayout);
        } while (superArrayPosition < tagsArray.length);

        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WikiPopUpActivity.class);
                intent.putExtra("CAT", category);
                startActivity(intent);
            }
        });

        editIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WikiPopUpActivity.class);
                intent.putExtra("NEW", false);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        });

        dh.updateViews(id, WikiTable.TABLE_NAME, WikiTable.ID, WikiTable.VIEWS);
    }

    public void populateTagPage() {
        tag = getIntent().getStringExtra("TAG");

        tagTv = (TextView) findViewById(R.id.tag);
        mListView = (ListView) findViewById(R.id.mListView);

        String tagString = "Tag: " + tag;
        tagTv.setText(tagString);
        tagTv.setTextSize(30f);

        final ArrayList<String> mTagArray = new ArrayList<>();
        Integer[] integers = dh.getWikisByQuery(tag);
        Bundle[] bundles = new Bundle[integers.length];
        for (int i = 0; i < integers.length; i++) {
            Integer integer = integers[i];
            Bundle mBundle = dh.getWiki(WikiTable.ID, String.valueOf(integer));
            bundles[i] = mBundle;
        }
        for (Bundle aBundle : bundles) {
            String string = aBundle.getString("TITLE");
            mTagArray.add(string);
        }
        ArrayAdapter<String> mTagArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mTagArray);
        mListView.setAdapter(mTagArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = mTagArray.get(position);
                Intent intent = new Intent(context, WikiPopUpActivity.class);
                intent.putExtras(dh.getWiki(WikiTable.TITLE, string));
                startActivity(intent);
            }
        });
    }

    public void populateCategoryPage() {
        category = getIntent().getStringExtra("CAT");

        categoryTv = (TextView) findViewById(R.id.tag);
        mListView = (ListView) findViewById(R.id.mListView);

        String tagString = "Category: " + category;
        categoryTv.setText(tagString);
        categoryTv.setTextSize(30f);

        final ArrayList<String> mCatArray = new ArrayList<>();
        Integer[] integers = dh.getWikisByQuery(category);
        Bundle[] bundles = new Bundle[integers.length];
        for (int i = 0; i < integers.length; i++) {
            Integer integer = integers[i];
            Bundle mBundle = dh.getWiki(WikiTable.ID, String.valueOf(integer));
            bundles[i] = mBundle;
        }
        for (Bundle aBundle : bundles) {
            String string = aBundle.getString("TITLE");
            mCatArray.add(string);
        }
        ArrayAdapter<String> mCatArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mCatArray);
        mListView.setAdapter(mCatArrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String string = mCatArray.get(position);
                Intent intent = new Intent(context, WikiPopUpActivity.class);
                intent.putExtras(dh.getWiki(WikiTable.TITLE, string));
                startActivity(intent);
            }
        });
        }

    public void populateEditPage() {
        titleEt = (EditText) findViewById(R.id.titleEditText);
        contentEt = (EditText) findViewById(R.id.contentEditText);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        tagsLayout = (LinearLayout) findViewById(R.id.tagsLayout);
        updateIv = (ImageView) findViewById(R.id.updateIv);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollableLayout = (LinearLayout) findViewById(R.id.scrollableLayout);

        Bundle bundle = null;
        new_wiki = getIntent().getBooleanExtra("NEW", true);
        if (!new_wiki) {
            id = getIntent().getIntExtra("ID", -1);
            bundle = dh.getWiki(WikiTable.ID, String.valueOf(id));
            originalTitle = bundle.getString("TITLE");
            titleEt.setText(originalTitle);
            contentEt.setText(bundle.getString("CONTENT"));
        }
        String[] categories = dh.getStringArray(WikiTable.TABLE_NAME, WikiTable.CATEGORY, new String[]{"(Select a category)", "Add a new category +"}, false);
        Integer pos_of_this_category = 0;
        if (!new_wiki) {
            for (int i = 0; i < categories.length; i++) {
                assert bundle != null;
                if (categories[i].equals(bundle.getString("CATEGORY"))) {
                    pos_of_this_category = i;
                    break;
                }
            }
        }
        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categories);
        categorySpinner.setAdapter(categoriesAdapter);
        categorySpinner.setSelection(pos_of_this_category);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    final EditText editText = new EditText(context);
                    editText.setWidth(parent.getWidth());
                    editText.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            scrollableLayout.removeView(editText);
                            parent.setSelection(0);
                            parent.setVisibility(View.VISIBLE);
                            return true;
                        }
                    });
                    scrollableLayout.addView(editText, scrollableLayout.indexOfChild(parent));
                    parent.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String tgs = "Tags: ";
        TextView tgsTv = new TextView(context);
        tgsTv.setText(tgs);
        tagsLayout.addView(tgsTv);

        String addNewTag = "Add a new tag +";
        final String selectTag = "(Select a tag)";
        final String[] superTagsArray = dh.getStringArray(WikiTable.TABLE_NAME, WikiTable.TAGS, new String[]{selectTag, addNewTag}, true);
        tagsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, superTagsArray);
        if (!new_wiki) {
            String[] localTagsArray = bundle.getString("TAGS").split(delimiter);
            for (String aLocalTagsArray : localTagsArray) {
                Spinner spinner = new Spinner(context);
                spinner.setAdapter(tagsAdapter);
                Integer pos_of_this_tag = 0;
                for (int k = 0; k < superTagsArray.length; k++) {
                    if (superTagsArray[k].equals(aLocalTagsArray)) {
                        pos_of_this_tag = k;
                    }
                }
                spinner.setSelection(pos_of_this_tag);
                spinner.setOnItemSelectedListener(spinnerToEditText);
                tagsLayout.addView(spinner);
            }
        }

        LinearLayout buttonsLayout = new LinearLayout(context);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        Button addTagButton = new Button(context);
        addTagButton.setGravity(View.FOCUS_RIGHT);
        addTagButton.setText("Add tag +");
        final Button removeTagButton = new Button(context);
        removeTagButton.setGravity(View.FOCUS_RIGHT);
        removeTagButton.setText("Remove tag -");
        buttonsLayout.addView(addTagButton);
        buttonsLayout.addView(removeTagButton);
        tagsLayout.addView(buttonsLayout);

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = new Spinner(context);
                spinner.setAdapter(tagsAdapter);
                spinner.setOnItemSelectedListener(spinnerToEditText);
                tagsLayout.addView(spinner, tagsLayout.getChildCount() - 1);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        removeTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int removable = tagsLayout.getChildCount() - 2;
                if (tagsLayout.getChildAt(removable) instanceof Spinner || tagsLayout.getChildAt(removable) instanceof EditText) {
                    tagsLayout.removeViewAt(removable);
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });

        updateIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ready = true;
                final String this_title = titleEt.getText().toString();
                final String this_content = contentEt.getText().toString();
                final String this_category = categorySpinner.getSelectedItem().toString();
                tags = "";
                for (int i = 0; i < tagsLayout.getChildCount(); i++) {
                    if (tagsLayout.getChildAt(i) instanceof EditText) {
                        EditText editText = (EditText) tagsLayout.getChildAt(i);
                        String string = editText.getText().toString();
                        if (tags == null || !string.isEmpty() || !tags.contains(string+delimiter)) {
                            tags += string + delimiter;
                        } else if (string.isEmpty()) {
                            Toast.makeText(context, "Please enter a tag", Toast.LENGTH_SHORT).show();
                            ready = false;
                            return;
                        } else {
                            Toast.makeText(context, "All tags must be unique", Toast.LENGTH_SHORT).show();
                            ready = false;
                            return;
                        }
                    } else
                    if (tagsLayout.getChildAt(i) instanceof Spinner) {
                        Spinner spinner = (Spinner) tagsLayout.getChildAt(i);
                        String string = spinner.getSelectedItem().toString();
                        if (string.equals(selectTag)) {
                            Toast.makeText(context, "Please select a tag", Toast.LENGTH_SHORT).show();
                            ready = false;
                            return;
                        } else if (tags.contains(string)) {
                            Toast.makeText(context, "All tags must be unique", Toast.LENGTH_SHORT).show();
                            ready = false;
                            return;
                        } else {
                            tags += string + delimiter;
                        }
                    }
                }
                if (this_title.isEmpty()) {
                    Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show();
                    ready = false;
                    return;
                }
                if (this_content.isEmpty()) {
                    Toast.makeText(context, "Please enter some content", Toast.LENGTH_SHORT).show();
                    ready = false;
                    return;
                }
                if (categorySpinner.getVisibility() != View.GONE && categorySpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(context, "Please select a category", Toast.LENGTH_SHORT).show();
                    ready = false;
                    return;
                } else if (categorySpinner.getVisibility() == View.VISIBLE) {
                    category = this_category;
                } else if (categorySpinner.getVisibility() == View.GONE) {
                    int i = scrollableLayout.indexOfChild(categorySpinner) - 1;
                    if (scrollableLayout.getChildAt(i) instanceof EditText) {
                        EditText editText = (EditText) scrollableLayout.getChildAt(i);
                        if (editText.getText().toString().isEmpty()) {
                            ready = false;
                            Toast.makeText(context, "Please enter a category", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            category = editText.getText().toString();
                        }
                    }
                }

                if (!new_wiki) {
                    if (!originalTitle.equals(this_title)) {
                        ready = false;
                        new AlertDialog.Builder(context).setMessage("Do you want to change the title or make a new entry?")
                                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (dh.getWiki(WikiTable.TITLE, this_title) != null) {
                                            Toast.makeText(context, "That entry already exists. Please provide a different title.", Toast.LENGTH_SHORT).show();
                                            ready = false;
                                        } else {
                                            ready = true;
                                        }
                                    }
                                })
                                .setNegativeButton("Make New", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dh.removeWiki(id);
                                        ready = true;
                                        new_wiki = true;
                                    }
                                })
                                .setNeutralButton("Go back", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                }
                if (ready) {
                    if (!new_wiki) {
                        dh.updateWiki(id, this_title, category, contentEt.getText().toString(), tags, String.valueOf(new Timestamp(new Date().getTime())));
                        Toast.makeText(context, "The wiki article was updated", Toast.LENGTH_SHORT).show();
                    } else {
                        dh.addWiki(this_title, category, categorySpinner.getSelectedItem().toString(), tags, String.valueOf(new Timestamp(new Date().getTime())));
                        Toast.makeText(context, "Your new wiki article was added", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(context, WikiPopUpActivity.class);
                    intent.putExtras(dh.getWiki(WikiTable.TITLE, titleEt.getText().toString()));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    AdapterView.OnItemSelectedListener spinnerToEditText = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 1) {
                final ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
                EditText editText = new EditText(context);
                editText.setWidth(view.getWidth());
                editText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Spinner spinner = new Spinner(context);
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(spinnerToEditText);
                        tagsLayout.addView(spinner, tagsLayout.indexOfChild(v));
                        tagsLayout.removeView(v);
                        return true;
                    }
                });
                tagsLayout.addView(editText, tagsLayout.indexOfChild(parent));
                tagsLayout.removeView(parent);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
